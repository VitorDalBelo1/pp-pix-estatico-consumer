package br.gov.pp.consumer.client;

import br.gov.pp.consumer.dto.InformePreliminarPagamentoRequest;
import br.gov.pp.consumer.dto.PPResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Cliente do endpoint de Informe Preliminar de Pagamento (Pix Estatico).
 *
 * A URL base vem de quarkus.rest-client.pp-split-payment.url.
 * O header X-JWS-Signature e enviado com um valor configuravel - a assinatura
 * real deve ser gerada antes de ir para producao.
 */
@Path("/api/v1/pix-estatico")
@RegisterRestClient(configKey = "pp-split-payment")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PixEstaticoClient {

    @POST
    @Path("/informe-preliminar-pagamento")
    @ClientHeaderParam(name = "X-JWS-Signature", value = "${pp.jws-signature}")
    PPResponse informePreliminarPagamento(InformePreliminarPagamentoRequest request);
}
