package com.sicredi.avaliacao.domain.exception;

import java.util.UUID;

public class PautaNaoEncontradaException extends EntidadeNaoEncontradaException {
    public PautaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public PautaNaoEncontradaException(UUID pautaId) {
        super(String.format("Não existe um cadastro de pauta com código %s", pautaId));
    }
}
