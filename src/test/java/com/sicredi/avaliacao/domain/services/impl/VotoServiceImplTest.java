package com.sicredi.avaliacao.domain.services.impl;

import com.sicredi.avaliacao.api.cloud.feignclient.UserInfoClient;
import com.sicredi.avaliacao.api.v1.model.AssociadoCpf;
import com.sicredi.avaliacao.domain.enums.StatusCpf;
import com.sicredi.avaliacao.domain.enums.VotoEnum;
import com.sicredi.avaliacao.domain.exception.NegocioException;
import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Voto;
import com.sicredi.avaliacao.domain.repositories.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class VotoServiceImplTest {

    @InjectMocks
    private VotoServiceImpl votoService;

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private UserInfoClient userInfoClient;

    private Voto voto;
    private Voto votoSalvo;
    private Pauta pauta;
    private AssociadoCpf associadoCpf;

    @BeforeEach
    void setUp() {
        voto = new Voto();
        voto.setCpfAssociado("000.000.000-00");
        votoSalvo = getVoto();
        pauta = new Pauta();
        associadoCpf = new AssociadoCpf();
    }

    @Test
    void deveriaRetornarVoto_QuandoSalvarVoto() {
        // given
        when(votoRepository.save(voto)).thenReturn(votoSalvo);

        // when
        var result = votoService.salvar(voto);

        // then
        assertNotNull(result.getVotoId());
        verify(votoRepository, times(1)).save(voto);
    }

    @Test
    void naoFazNada_QuandoValidarVotoQaundoAssociadoAindaNaoVotouECpfValido() {
        // given
        associadoCpf.setStatus(StatusCpf.ABLE_TO_VOTE);
        when(votoRepository.existsByCpfAssociadoAndPauta(voto.getCpfAssociado(), pauta)).thenReturn(false);
        when(userInfoClient.checarCpf(voto.getCpfAssociado())).thenReturn(associadoCpf);

        // when
        votoService.validarVoto(voto.getCpfAssociado(), pauta);

        // then
        verify(votoRepository, times(1)).existsByCpfAssociadoAndPauta(voto.getCpfAssociado(), pauta);
        verify(userInfoClient, times(1)).checarCpf(sanitizarCpf(voto.getCpfAssociado()));
    }

    @Test
    void lancaNegocioException_QuandoValidarVotoQuandoAssociadoJaVotou() {
        // given
        pauta.setPautaId(UUID.randomUUID());
        when(votoRepository.existsByCpfAssociadoAndPauta(voto.getCpfAssociado(), pauta)).thenReturn(true);

        // when
        assertThrows((NegocioException.class), () -> votoService.validarVoto(voto.getCpfAssociado(), pauta));

        // then
        verify(votoRepository, times(1)).existsByCpfAssociadoAndPauta(voto.getCpfAssociado(), pauta);
    }

    @Test
    void lancaNegocioException_QuandoValidarVotoQuandoAssociadoNaoPodeVotar() {
        // given
        associadoCpf.setStatus(StatusCpf.UNABLE_TO_VOTE);
        when(votoRepository.existsByCpfAssociadoAndPauta(voto.getCpfAssociado(), pauta)).thenReturn(false);
        when(userInfoClient.checarCpf(sanitizarCpf(voto.getCpfAssociado()))).thenReturn(associadoCpf);

        // when
        assertThrows((NegocioException.class), () -> votoService.validarVoto(voto.getCpfAssociado(), pauta));

        // then
        verify(votoRepository, times(1)).existsByCpfAssociadoAndPauta(voto.getCpfAssociado(), pauta);
        verify(userInfoClient, times(1)).checarCpf(sanitizarCpf(voto.getCpfAssociado()));
    }

    private Voto getVoto() {
        return Voto.builder()
                .votoId(UUID.randomUUID())
                .votoAssociado(VotoEnum.SIM)
                .cpfAssociado("000.000.000-00")
                .build();
    }
    private String sanitizarCpf(String cpfAssociado) {
        return cpfAssociado.replace(".", "")
                .replace("-", "");
    }
}