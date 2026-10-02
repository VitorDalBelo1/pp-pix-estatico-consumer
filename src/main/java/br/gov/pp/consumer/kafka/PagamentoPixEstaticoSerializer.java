package br.gov.pp.consumer.kafka;

import br.gov.pp.consumer.dto.PagamentoPixEstaticoEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class PagamentoPixEstaticoSerializer extends ObjectMapperSerializer<PagamentoPixEstaticoEvent> {
}