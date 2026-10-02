package br.gov.pp.consumer.service;

import br.gov.pp.consumer.dto.InformePreliminarPagamentoRequest;
import br.gov.pp.consumer.dto.PPResponse;
import br.gov.pp.consumer.dto.PagamentoPixEstaticoEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Orquestra: recebe o lote vindo do Kafka, quebra em requisicoes que respeitam
 * o limite de transacoes do contrato e envia cada uma.
 */
@ApplicationScoped
public class InformePreliminarService {

    private static final Logger LOG = Logger.getLogger(InformePreliminarService.class);

    @Inject
    InformePreliminarMapper mapper;

    @Inject
    PixEstaticoSender sender;

    /** O contrato aceita no maximo 1000 transacoes por requisicao. */
    @ConfigProperty(name = "informe.max-transacoes-por-requisicao", defaultValue = "1000")
    int maxTransacoes;

    public void processar(List<PagamentoPixEstaticoEvent> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            return;
        }
        for (int inicio = 0; inicio < eventos.size(); inicio += maxTransacoes) {
            int fim = Math.min(inicio + maxTransacoes, eventos.size());
            enviarLote(eventos.subList(inicio, fim));
        }
    }

    private void enviarLote(List<PagamentoPixEstaticoEvent> lote) {
        InformePreliminarPagamentoRequest request = mapper.paraRequisicao(lote);
        PPResponse resposta = sender.enviar(request);
        LOG.infof("Informe preliminar enviado: %d transacoes, resourceId=%s, validos=%s, erros=%s",
                lote.size(),
                resposta != null ? resposta.resourceId() : null,
                resposta != null ? resposta.numValidos() : null,
                resposta != null ? resposta.numErros() : null);
        if (resposta != null && resposta.numErros() != null && resposta.numErros() > 0) {
            LOG.warnf("A Plataforma Publica rejeitou %d item(ns): %s",
                    resposta.numErros(), resposta.errors());
        }
    }
}
