package com.sicredi.avaliacao.domain.enums;

import lombok.Getter;

@Getter
public enum PautaStatus {
    APROVADO("Aprovado"),
    REPROVADO("Reprovado"),

    EM_VOTACAO("Em votação"),

    AGUARDANDO_VOTACAO("Aguardando votação");

    private String descricao;

    PautaStatus(String descricao) {
        this.descricao = descricao;
    }
}
