package com.sicredi.avaliacao.domain.services.impl;

import com.sicredi.avaliacao.api.cloud.feignclient.UserInfoClient;
import com.sicredi.avaliacao.api.v1.model.AssociadoCpf;
import com.sicredi.avaliacao.domain.enums.StatusCpf;
import com.sicredi.avaliacao.domain.exception.NegocioException;
import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Voto;
import com.sicredi.avaliacao.domain.repositories.VotoRepository;
import com.sicredi.avaliacao.domain.services.VotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class VotoServiceImpl implements VotoService {

    private final VotoRepository votoRepository;
    private final UserInfoClient userInfoClient;

    @Override
    public Voto salvar(Voto voto) {
        return votoRepository.save(voto);
    }

    @Override
    public void validarVoto(String cpfAssociado, Pauta pauta) {
        if (votoRepository.existsByCpfAssociadoAndPauta(cpfAssociado, pauta)) {
            log.warn("Voto do associado de CPF: {}, já foi contabilizado na pauta {}}", cpfAssociado, pauta.toString());
            throw new NegocioException(String.format("Voto do associado de CPF: %s, já foi contabilizado na pauta %s", cpfAssociado, pauta.getPautaId()));
        }

        AssociadoCpf associadoCpf = userInfoClient.checarCpf(sanitizarCpf(cpfAssociado));
        if (Objects.nonNull(associadoCpf) && associadoCpf.getStatus().equals(StatusCpf.UNABLE_TO_VOTE)) {
            log.warn("Associado de CPF: {}, ilegível para votação na pauta {}}", cpfAssociado, pauta.toString());
            throw new NegocioException("Associado ilegível para votação nesta Pauta.");
        }
    }

    private String sanitizarCpf(String cpfAssociado) {
        return cpfAssociado.replace(".", "")
                .replace("-", "");
    }
}
