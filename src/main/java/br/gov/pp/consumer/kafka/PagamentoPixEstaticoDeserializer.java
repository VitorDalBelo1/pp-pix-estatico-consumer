package br.gov.pp.consumer.kafka;

import br.gov.pp.consumer.dto.PagamentoPixEstaticoEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

/** Desserializa o valor do registro Kafka (JSON) em PagamentoPixEstaticoEvent. */
public class PagamentoPixEstaticoDeserializer extends ObjectMapperDeserializer<PagamentoPixEstaticoEvent> {

    public PagamentoPixEstaticoDeserializer() {
        super(PagamentoPixEstaticoEvent.class);
    }
}
