package com.sicredi.avaliacao.domain.services;

import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Sessao;

public interface SessaoService {

    Sessao salvar(Sessao sessao);

    void verificarSeExisteSessao(Pauta pauta);

    void fecharVotacao(Sessao sessao, Pauta pauta);

    void validarSessao(Pauta pauta);
}
