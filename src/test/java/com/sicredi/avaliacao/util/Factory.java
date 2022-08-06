package com.sicredi.avaliacao.util;

import com.sicredi.avaliacao.domain.enums.VotoEnum;
import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Sessao;
import com.sicredi.avaliacao.domain.model.Voto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Factory {

    public static Pauta criarPauta() {
        return Pauta.builder()
                .pautaId(UUID.randomUUID())
                .tema("Tema")
                .dataCriacao(OffsetDateTime.now())
                .build();
    }

    public static Pauta criarPautaVazia() {
        return new Pauta();
    }

    public static Sessao criarSessao() {
        return Sessao.builder()
                .sessaoId(UUID.randomUUID())
                .aberta(true)
                .build();
    }

    public static Sessao criarSessaoVazia() {
        return new Sessao();
    }

    public static Voto criarVoto() {
        return Voto.builder()
                .votoId(UUID.randomUUID())
                .votoAssociado(VotoEnum.SIM)
                .cpfAssociado("000.000.000.00")
                .build();
    }

    public static Voto criarVotoVazio() {
        return new Voto();
    }
}
