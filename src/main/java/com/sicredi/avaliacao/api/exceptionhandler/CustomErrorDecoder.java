package com.sicredi.avaliacao.api.exceptionhandler;

import com.sicredi.avaliacao.domain.exception.NegocioException;
import feign.Response;
import feign.codec.ErrorDecoder;


public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        if (response.status() == 404) {
            return new NegocioException("Cpf inválido.");
        }
        return new Exception();
    }
}
