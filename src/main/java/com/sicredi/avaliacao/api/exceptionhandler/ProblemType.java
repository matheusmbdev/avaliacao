package com.sicredi.avaliacao.api.exceptionhandler;

import lombok.Getter;

@Getter
public enum ProblemType {
    DADOS_INVALIDOS("/dados-invalidos", "Dados inválidos"),
    ERRO_DE_SISTEMA("/erro-de-sistema", "Erro de Sistema"),
    MENSAGEM_INCOMPREENSIVEL("/mensagem-incompreensivel", "Mensgem imcompreensível"),
    RECURSO_NAO_ENCONTRADA("/recurso-nao-encontrada", "Recurso não encontrada"),
    ERRO_NEGOCIO("/erro-negocio", "Violação de regra de negócio");

    private String uri;
    private String title;

    ProblemType(String path, String title) {
        this.uri = "https://avaliacao.com.br" + path;
        this.title = title;
    }
}
