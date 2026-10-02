package br.gov.pp.consumer.kafka;

import br.gov.pp.consumer.dto.PagamentoPixEstaticoEvent;
import br.gov.pp.consumer.service.InformePreliminarService;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Consome o topico de pagamentos Pix estatico em lotes e repassa para o
 * endpoint de Informe Preliminar de Pagamento.
 *
 * O canal esta configurado com batch=true, entao cada invocacao recebe todos os
 * registros trazidos por um poll (ate max.poll.records). O offset so e commitado
 * depois que o metodo retorna sem excecao; se lancar, o registro segue a
 * failure-strategy do canal (por padrao, DLQ).
 */
@ApplicationScoped
public class PagamentoPixEstaticoConsumer {

    private static final Logger LOG = Logger.getLogger(PagamentoPixEstaticoConsumer.class);

    @Inject
    InformePreliminarService service;

    @Incoming("pagamentos-pix-estatico")
    @Blocking
    public void consumir(List<PagamentoPixEstaticoEvent> eventos) {
        LOG.debugf("Lote recebido do Kafka com %d evento(s)", eventos.size());
        service.processar(eventos);
    }
}
