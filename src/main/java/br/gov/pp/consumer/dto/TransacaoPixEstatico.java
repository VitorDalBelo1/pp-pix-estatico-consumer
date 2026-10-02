package br.gov.pp.consumer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Item de "transacoes" do InformePreliminarDePagamentoPixEstaticoRequest.
 * Campos opcionais ausentes sao omitidos do JSON enviado.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransacaoPixEstatico(
        Integer index,
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
