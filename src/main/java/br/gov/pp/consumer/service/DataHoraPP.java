package br.gov.pp.consumer.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * O contrato exige data/hora no formato yyyy-MM-ddTHH:mm:ss-03:00
 * (offset fixo, sem milissegundos).
 */
public final class DataHoraPP {

    private static final ZoneOffset OFFSET_BR = ZoneOffset.ofHours(-3);

    private DataHoraPP() {
    }

    public static String agora() {
        return OffsetDateTime.now(OFFSET_BR)
                .truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
