package br.gov.pp.consumer.service;

import br.gov.pp.consumer.dto.InfRequisicao;
import br.gov.pp.consumer.dto.InformePreliminarPagamentoRequest;
import br.gov.pp.consumer.dto.PagamentoPixEstaticoEvent;
import br.gov.pp.consumer.dto.TransacaoPixEstatico;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

/**
 * Converte os eventos lidos do Kafka no corpo esperado pelo endpoint.
 * O campo "index" e atribuido aqui: sequencial de 1..n dentro do lote.
 */
@ApplicationScoped
public class InformePreliminarMapper {

    public InformePreliminarPagamentoRequest paraRequisicao(List<PagamentoPixEstaticoEvent> eventos) {
        List<TransacaoPixEstatico> transacoes = new ArrayList<>(eventos.size());
        int index = 1;
        for (PagamentoPixEstaticoEvent e : eventos) {
            transacoes.add(new TransacaoPixEstatico(
                    index++,
                    e.e2eId(),
                    e.vlPago(),
                    e.vlCbsSegr(),
                    e.vlIbsSegr(),
                    e.docFiscal(),
                    e.cnpjRaizPspRecInd(),
                    e.cnpjRaizPspRecDir(),
                    e.cnpjRaizPspPag(),
                    e.cnpjRec(),
                    e.cnpjCpfPagEfet(),
                    e.dtHrPgto(),
                    e.dtHrLiq()));
        }
        return new InformePreliminarPagamentoRequest(new InfRequisicao(DataHoraPP.agora()), transacoes);
    }
}
