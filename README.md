# Consumidor Kafka → Informe Preliminar de Pagamento (Pix Estático)

Aplicação Quarkus (JDK 21) que consome um tópico Kafka e, para cada lote de
mensagens, chama `POST /api/v1/pix-estatico/informe-preliminar-pagamento` da
Plataforma Pública de Split Payment.

## Fluxo

```
tópico  pagamentos.pix-estatico
   │  (lote de até max.poll.records registros)
   ▼
PagamentoPixEstaticoConsumer  @Incoming batch
   ▼
InformePreliminarService      quebra em requisições de até 1000 transações
   ▼
InformePreliminarMapper       monta infRequisicao.dtHrMsg + transacoes[].index
   ▼
PixEstaticoSender             @Retry + @Timeout
   ▼
POST /api/v1/pix-estatico/informe-preliminar-pagamento
```

O offset só é commitado depois que o lote inteiro é aceito pelo endpoint. Se
falhar mesmo após os retries, os registros vão para `pagamentos.pix-estatico.DLQ`.

## Premissa do formato da mensagem

**Cada mensagem do tópico é uma transação** (um pagamento Pix estático já
segregado), não a requisição inteira. O campo `index` não vem na mensagem — ele é
atribuído na montagem do lote, de 1 a n, e o `dtHrMsg` é gerado na hora do envio.

Exemplo de mensagem (`exemplo-evento.json`):

```json
{
  "e2eId": "1234ABDOASD11231234ABDOASD112312345",
  "vlPago": 10.02,
  "vlCbsSegr": 1.02,
  "vlIbsSegr": 2.03,
  "docFiscal": "1234567890123456789B123A5678901234567891123412345",
  "cnpjRaizPspRecInd": "87654321",
  "cnpjRaizPspRecDir": "12345678",
  "cnpjRaizPspPag": "87654321",
  "cnpjRec": "11444777000142",
  "cnpjCpfPagEfet": "11444777000142",
  "dtHrPgto": "2026-03-20T10:33:46-03:00",
  "dtHrLiq": "2026-03-20T10:33:46-03:00"
}
```

Campos extras no JSON são ignorados, então o tópico pode carregar metadados
próprios sem quebrar o consumidor.

> Se no seu caso cada mensagem já for a requisição completa (`infRequisicao` +
> `transacoes`), troque o tipo do `@Incoming` para
> `InformePreliminarPagamentoRequest`, desligue o `batch` e chame o client
> direto — o resto da estrutura continua valendo.

## Como rodar

1. Suba a API (o projeto `pp-split-payment-mock`, na porta 8080):

```bash
cd ../pp-split-payment-mock && mvn quarkus:dev
```

2. Suba o consumidor (porta 8081). Em modo dev o Quarkus levanta um Kafka
   sozinho via Dev Services — não precisa de broker instalado, só Docker:

```bash
mvn quarkus:dev
```

3. Publique uma mensagem. Com o Redpanda do `docker-compose.yml`:

```bash
docker compose up -d
rpk topic create pagamentos.pix-estatico
cat exemplo-evento.json | jq -c . | rpk topic produce pagamentos.pix-estatico
```

Ou, usando o console do Dev UI: http://localhost:8081/q/dev-ui (card do Kafka,
opção "Topics" → produzir mensagem).

O log deve mostrar:

```
Informe preliminar enviado: 1 transacoes, resourceId=UEkBbgR-..., validos=1, erros=0
```

## Configuração principal

| Propriedade | Default | Para que serve |
|---|---|---|
| `mp.messaging.incoming.pagamentos-pix-estatico.topic` | `pagamentos.pix-estatico` | tópico consumido |
| `mp.messaging.incoming.pagamentos-pix-estatico.group.id` | `pp-informe-preliminar-pix-estatico` | consumer group |
| `mp.messaging.incoming.pagamentos-pix-estatico.max.poll.records` | `500` | tamanho do lote por invocação |
| `mp.messaging.incoming.pagamentos-pix-estatico.failure-strategy` | `dead-letter-queue` | `fail` ou `ignore` também valem |
| `quarkus.rest-client.pp-split-payment.url` | `http://localhost:8080` | base da API |
| `informe.max-transacoes-por-requisicao` | `1000` | limite do contrato |
| `pp.jws-signature` | valor fictício | header `X-JWS-Signature` |
| `%prod.kafka.bootstrap.servers` | `${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}` | broker em produção |

## O que ficou de fora (de propósito)

- **Assinatura JWS real**: o header vai com um valor fixo de configuração. Para
  valer, gere a assinatura por requisição em um `ClientRequestFilter`.
- **mTLS**: a API real exige certificado de cliente. Quando for apontar para o
  ambiente real, configure
  `quarkus.rest-client.pp-split-payment.key-store` / `trust-store`.
- **Idempotência**: reprocessamento após rebalance pode reenviar o mesmo lote.
  Se o negócio exigir, guarde os `e2eId` já enviados.
- **Validação de payload**: o consumidor não valida patterns/obrigatórios; quem
  reclama é a Plataforma Pública (e o mock não reclama de nada).
