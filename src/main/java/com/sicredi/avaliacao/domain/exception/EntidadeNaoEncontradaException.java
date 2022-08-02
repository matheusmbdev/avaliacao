package com.sicredi.avaliacao.domain.exception;

public abstract class EntidadeNaoEncontradaException extends NegocioException {

    protected EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
