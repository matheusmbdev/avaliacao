package com.sicredi.avaliacao.domain.services.impl;

import com.sicredi.avaliacao.domain.exception.PautaNaoEncontradaException;
import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.repositories.PautaRepository;
import com.sicredi.avaliacao.domain.services.PautaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PautaServiceImpl implements PautaService {

    private final PautaRepository pautaRepository;

    @Override
    public Pauta salvar(Pauta pauta) {
        return pautaRepository.save(pauta);
    }

    @Override
    public Pauta buscarOuFalhar(UUID pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException(pautaId));
    }
}
