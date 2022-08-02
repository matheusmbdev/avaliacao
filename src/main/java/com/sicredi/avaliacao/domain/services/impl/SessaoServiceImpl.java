package com.sicredi.avaliacao.domain.services.impl;

import com.sicredi.avaliacao.domain.enums.PautaStatus;
import com.sicredi.avaliacao.domain.enums.VotoEnum;
import com.sicredi.avaliacao.domain.exception.NegocioException;
import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Sessao;
import com.sicredi.avaliacao.domain.model.Voto;
import com.sicredi.avaliacao.domain.repositories.SessaoRepository;
import com.sicredi.avaliacao.domain.services.PautaService;
import com.sicredi.avaliacao.domain.services.SessaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class SessaoServiceImpl implements SessaoService {

    private final SessaoRepository sessaoRepository;
    private final PautaService pautaService;

    @Override
    public Sessao salvar(Sessao sessao) {
        return sessaoRepository.save(sessao);
    }

    public void fecharVotacao(Sessao sessao, Pauta pauta) {
        sessao.fechar();
        sessaoRepository.save(sessao);
        log.info("Fechamento de sessão de votação sessaoId {}, pautaId {}", sessao.getSessaoId(), pauta.getPautaId());
        pauta = pautaService.buscarOuFalhar(pauta.getPautaId());

        VotoEnum resultado = contarVotos(pauta);

        pauta.setPautaStatus(resultado.equals(VotoEnum.SIM) ? PautaStatus.APROVADO : PautaStatus.REPROVADO);
        pautaService.salvar(pauta);
        log.info("Votos contabilizados e resultado gerado para a pauta {}", pauta.toString());
    }

    @Override
    public void validarSessao(Pauta pauta) {
        Optional<Sessao> sessaoOptional = sessaoRepository.findByPauta(pauta);
        if (sessaoOptional.isEmpty()) {
            log.warn("Pauta sem sessão de votação, pauta {}", pauta.toString());
            throw new NegocioException("Ainda não foi aberto uma sessão de votação para essa pauta.");
        } else if (!sessaoOptional.get().isAberta()) {
            log.warn("Sessão de votação encerrada para a pauta {}", pauta.toString());
            throw new NegocioException("Sessão de votação encerrada.");
        }
    }

    public void verificarSeExisteSessao(Pauta pauta) {
        Optional<Sessao> sessaoOptional = sessaoRepository.findByPauta(pauta);
        if (sessaoOptional.isPresent()) {
            log.warn("Já esxiste uma sessão de votação para a pauta {}", pauta.toString());
            throw new NegocioException("Existe uma sessão de votação para essa pauta em aberto ou já encerrada, " +
                    "favor verificar na pauta seu status.");
        }
    }

    private static VotoEnum contarVotos(Pauta pauta) {
        List<VotoEnum> votoEnums = pauta.getVotos().stream()
                .map(Voto::getVotoAssociado).collect(Collectors.toList());

        Map<VotoEnum, Integer> votosMap = votoEnums.stream()
                .collect(Collectors.toMap(Function.identity(), voto -> 1, Integer::sum));

        Optional<Map.Entry<VotoEnum, Integer>> resultado = votosMap.entrySet().stream()
                .max((votoAFavor, votoContra) -> votoAFavor.getValue() >= votoContra.getValue() ? 1 : -1);

        if (resultado.isPresent()) {
            return resultado.get().getKey();
        }
        return VotoEnum.NAO;
    }
}
