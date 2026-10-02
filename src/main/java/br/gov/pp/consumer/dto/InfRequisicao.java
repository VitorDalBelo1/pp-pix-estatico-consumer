package br.gov.pp.consumer.dto;

/**
 * Bloco infRequisicao do contrato (InfoRequestSemCnpj).
 *
 * @param dtHrMsg data/hora da mensagem no formato ISO 8601 com fuso -03:00
 */
public record InfRequisicao(String dtHrMsg) {
}
