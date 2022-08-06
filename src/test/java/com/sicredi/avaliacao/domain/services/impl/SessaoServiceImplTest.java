package com.sicredi.avaliacao.domain.services.impl;

import com.sicredi.avaliacao.domain.enums.PautaStatus;
import com.sicredi.avaliacao.domain.enums.VotoEnum;
import com.sicredi.avaliacao.domain.exception.NegocioException;
import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Sessao;
import com.sicredi.avaliacao.domain.model.Voto;
import com.sicredi.avaliacao.domain.repositories.SessaoRepository;
import com.sicredi.avaliacao.domain.services.PautaService;
import com.sicredi.avaliacao.util.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SessaoServiceImplTest {

    public static final String AINDA_NAO_FOI_ABERTO_UMA_SESSAO_DE_VOTACAO_PARA_ESSA_PAUTA = "Ainda não foi aberto uma sessão de votação para essa pauta.";
    public static final String SESSAO_DE_VOTACAO_ENCERRADA = "Sessão de votação encerrada.";
    public static final String EXISTE_UMA_SESSAO_DE_VOTACAO_PARA_ESSA_PAUTA = "Existe uma sessão de votação para essa pauta em aberto ou já encerrada, favor verificar na pauta seu status.";
    @InjectMocks
    private SessaoServiceImpl sessaoService;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PautaService pautaService;

    private Sessao sessao;
    private Sessao sessaoSalva;
    private Pauta pauta;
    private Voto voto;

    @BeforeEach
    void setUp() {
        sessao = Factory.criarSessaoVazia();
        sessaoSalva = Factory.criarSessao();
        pauta = Factory.criarPauta();
        voto = Factory.criarVoto();
    }

    @Test
    void deveriaRetornarSessao_QuandoSalvarSessao() {
        // given
        when(sessaoRepository.save(sessao)).thenReturn(sessaoSalva);

        // when
        var result = sessaoService.salvar(sessao);

        // then
        assertNotNull(result.getSessaoId());
        verify(sessaoRepository, times(1)).save(sessao);
    }

    @Test
    void deveriaFecharSessaoEAprovarPauta_QuandoFecharSessaoComMaisVotosPositivos() {
        // given
        pauta.getVotos().add(voto);
        when(sessaoRepository.save(sessao)).thenReturn(sessaoSalva);
        when(pautaService.buscarOuFalhar(pauta.getPautaId())).thenReturn(pauta);
        when(pautaService.salvar(pauta)).thenReturn(pauta);

        // when
        sessaoService.fecharVotacao(sessaoSalva, pauta);

        // then
        assertFalse(sessaoSalva.isAberta());
        assertEquals(PautaStatus.APROVADO, pauta.getPautaStatus());
        verify(sessaoRepository, times(1)).save(sessaoSalva);
        verify(pautaService, times(1)).buscarOuFalhar(pauta.getPautaId());
        verify(pautaService, times(1)).salvar(pauta);
    }

    @Test
    void deveriaFecharSessaoEReprovarPauta_QuandoFecharSessaoComMaisVotosNegativos() {
        // given
        voto.setVotoAssociado(VotoEnum.NAO);
        pauta.getVotos().add(voto);
        when(sessaoRepository.save(sessao)).thenReturn(sessaoSalva);
        when(pautaService.buscarOuFalhar(pauta.getPautaId())).thenReturn(pauta);
        when(pautaService.salvar(pauta)).thenReturn(pauta);

        // when
        sessaoService.fecharVotacao(sessaoSalva, pauta);

        // then
        assertFalse(sessaoSalva.isAberta());
        assertEquals(PautaStatus.REPROVADO, pauta.getPautaStatus());
        verify(sessaoRepository, times(1)).save(sessaoSalva);
        verify(pautaService, times(1)).buscarOuFalhar(pauta.getPautaId());
        verify(pautaService, times(1)).salvar(pauta);
    }

    @Test
    void deveriaFecharSessaoEReprovarPautaSemVotos_QuandoFecharSessao() {
        // given
        when(sessaoRepository.save(sessao)).thenReturn(sessaoSalva);
        when(pautaService.buscarOuFalhar(pauta.getPautaId())).thenReturn(pauta);
        when(pautaService.salvar(pauta)).thenReturn(pauta);

        // when
        sessaoService.fecharVotacao(sessaoSalva, pauta);

        // then
        assertFalse(sessaoSalva.isAberta());
        assertEquals(PautaStatus.REPROVADO, pauta.getPautaStatus());
        verify(sessaoRepository, times(1)).save(sessaoSalva);
        verify(pautaService, times(1)).buscarOuFalhar(pauta.getPautaId());
        verify(pautaService, times(1)).salvar(pauta);
    }

    @Test
    void naoFazNada_QuandoValidarSessaoComSessaoAberta() {
        // given
        when(sessaoRepository.findByPauta(pauta)).thenReturn(Optional.of(sessaoSalva));

        // when
        sessaoService.validarSessao(pauta);

        // then
        verify(sessaoRepository, times(1)).findByPauta(pauta);
    }

    @Test
    void lancaNegocioException_QuandoValidarSessaoComSessaoFechada() {
        // given
        sessaoSalva.setAberta(false);
        when(sessaoRepository.findByPauta(pauta)).thenReturn(Optional.of(sessaoSalva));

        // when
        var negocioException = assertThrows((NegocioException.class), () -> sessaoService.validarSessao(pauta));

        // then
        assertEquals(SESSAO_DE_VOTACAO_ENCERRADA, negocioException.getMessage());
        verify(sessaoRepository, times(1)).findByPauta(pauta);
    }

    @Test
    void lancaNegocioException_QuandoValidarSessaoSemSessao() {
        // given
        when(sessaoRepository.findByPauta(pauta)).thenReturn(Optional.empty());

        // when
        var negocioException = assertThrows((NegocioException.class), () -> sessaoService.validarSessao(pauta));

        // then
        assertEquals(AINDA_NAO_FOI_ABERTO_UMA_SESSAO_DE_VOTACAO_PARA_ESSA_PAUTA, negocioException.getMessage());
        verify(sessaoRepository, times(1)).findByPauta(pauta);
    }

    @Test
    void naoFazNada_QuandoVerificarSeExisteSessaoSemSessaoExistente() {
        // given
        when(sessaoRepository.findByPauta(pauta)).thenReturn(Optional.empty());

        // when
        sessaoService.verificarSeExisteSessao(pauta);

        // then
        verify(sessaoRepository, times(1)).findByPauta(pauta);
    }

    @Test
    void lancaNegocioException_QuandoVerificarSeExisteSessaoComSessaoExistente() {
        // given
        when(sessaoRepository.findByPauta(pauta)).thenReturn(Optional.of(sessaoSalva));

        // when
        var negocioException = assertThrows((NegocioException.class), () -> sessaoService.verificarSeExisteSessao(pauta));

        // then
        assertEquals(EXISTE_UMA_SESSAO_DE_VOTACAO_PARA_ESSA_PAUTA, negocioException.getMessage());
        verify(sessaoRepository, times(1)).findByPauta(pauta);
    }
}