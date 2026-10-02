package br.gov.pp.consumer.service;

import br.gov.pp.consumer.client.PixEstaticoClient;
import br.gov.pp.consumer.dto.InformePreliminarPagamentoRequest;
import br.gov.pp.consumer.dto.PPResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ClientErrorException;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.temporal.ChronoUnit;

/**
 * Faz a chamada HTTP propriamente dita.
 *
 * Fica em um bean separado de proposito: @Retry so e interceptado quando a
 * chamada passa pelo proxy do CDI, ou seja, quando vem de outro bean.
 *
 * Erros 4xx nao sao repetidos (o payload nao vai melhorar sozinho); falhas de
 * rede e 5xx sao repetidas ate 3 vezes.
 */
@ApplicationScoped
public class PixEstaticoSender {

    @Inject
    @RestClient
    PixEstaticoClient client;

    @Retry(maxRetries = 3, delay = 500, jitter = 200, delayUnit = ChronoUnit.MILLIS,
            retryOn = Exception.class, abortOn = ClientErrorException.class)
    @Timeout(value = 15, unit = ChronoUnit.SECONDS)
    public PPResponse enviar(InformePreliminarPagamentoRequest request) {
        return client.informePreliminarPagamento(request);
    }
}
