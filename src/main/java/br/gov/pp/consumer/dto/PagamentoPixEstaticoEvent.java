package br.gov.pp.consumer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Evento lido do topico Kafka: um pagamento Pix estatico ja segregado.
 *
 * Nao tem o campo "index" - ele e atribuido na montagem do lote, conforme a
 * posicao do item dentro da requisicao enviada a Plataforma Publica.
 *
 * Campos desconhecidos no JSON sao ignorados, entao o topico pode carregar
 * metadados extras sem quebrar o consumidor.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PagamentoPixEstaticoEvent(
        String e2eId,
        BigDecimal vlPago,
        BigDecimal vlCbsSegr,
        BigDecimal vlIbsSegr,
        String docFiscal,
        String cnpjRaizPspRecInd,
        String cnpjRaizPspRecDir,
        String cnpjRaizPspPag,
        String cnpjRec,
        String cnpjCpfPagEfet,
        String dtHrPgto,
        String dtHrLiq) {
}
