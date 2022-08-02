package com.sicredi.avaliacao.domain.services;

import com.sicredi.avaliacao.domain.model.Pauta;

import java.util.UUID;

public interface PautaService {

    Pauta salvar(Pauta pauta);

    Pauta buscarOuFalhar(UUID pautaId);
}
