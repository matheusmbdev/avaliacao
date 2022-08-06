package com.sicredi.avaliacao.util;

import com.sicredi.avaliacao.domain.model.Pauta;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Factory {

    public static Pauta createPauta(){
        return Pauta.builder()
                .pautaId(UUID.randomUUID())
                .tema("Tema")
                .dataCriacao(OffsetDateTime.now())
                .build();
    }

    public static Pauta createEmptyPauta(){
        return new Pauta();
    }
}
