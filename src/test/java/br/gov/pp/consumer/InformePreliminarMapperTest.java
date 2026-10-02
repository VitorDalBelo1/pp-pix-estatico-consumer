package br.gov.pp.consumer;

import br.gov.pp.consumer.dto.InformePreliminarPagamentoRequest;
import br.gov.pp.consumer.dto.PagamentoPixEstaticoEvent;
import br.gov.pp.consumer.service.InformePreliminarMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InformePreliminarMapperTest {

    private final InformePreliminarMapper mapper = new InformePreliminarMapper();

    private PagamentoPixEstaticoEvent evento(String e2eId) {
        return new PagamentoPixEstaticoEvent(
                e2eId,
                new BigDecimal("10.02"),
                new BigDecimal("1.02"),
                new BigDecimal("2.03"),
                "1234567890123456789B123A5678901234567891123412345",
                "87654321",
                "12345678",
                "87654321",
                "11444777000142",
                "11444777000142",
                "2026-03-20T10:33:46-03:00",
                "2026-03-20T10:33:46-03:00");
    }

    @Test
    void atribuiIndexSequencialComecandoEmUm() {
        InformePreliminarPagamentoRequest request =
                mapper.paraRequisicao(List.of(evento("A"), evento("B"), evento("C")));

        assertEquals(3, request.transacoes().size());
        assertEquals(1, request.transacoes().get(0).index());
        assertEquals(2, request.transacoes().get(1).index());
        assertEquals(3, request.transacoes().get(2).index());
        assertEquals("A", request.transacoes().get(0).e2eId());
    }

    @Test
    void preencheDtHrMsgNoFormatoDoContrato() {
        String dtHrMsg = mapper.paraRequisicao(List.of(evento("A"))).infRequisicao().dtHrMsg();
        assertTrue(dtHrMsg.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}-03:00$"),
                "dtHrMsg fora do formato esperado: " + dtHrMsg);
    }
}
