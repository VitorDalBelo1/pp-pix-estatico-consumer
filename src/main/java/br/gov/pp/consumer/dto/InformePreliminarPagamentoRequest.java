package br.gov.pp.consumer.dto;

import java.util.List;

/** Corpo do POST /api/v1/pix-estatico/informe-preliminar-pagamento. */
public record InformePreliminarPagamentoRequest(
        InfRequisicao infRequisicao,
        List<TransacaoPixEstatico> transacoes) {
}
