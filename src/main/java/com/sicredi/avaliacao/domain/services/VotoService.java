package com.sicredi.avaliacao.domain.services;

import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Voto;

public interface VotoService {

    Voto salvar(Voto voto);

    void validarVoto(String cpfAssociado, Pauta pauta);
}
