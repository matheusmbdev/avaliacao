package com.sicredi.avaliacao.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SessaoTest {

    @Test
    void deveriaFecharSessao() {
        // given
        Sessao sessao = new Sessao();

        // when
        sessao.fechar();

        // then
        assertFalse(sessao.isAberta());
    }
}