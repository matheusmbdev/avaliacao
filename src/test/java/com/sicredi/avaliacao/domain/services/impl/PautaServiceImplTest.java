package com.sicredi.avaliacao.domain.services.impl;

import com.sicredi.avaliacao.domain.exception.PautaNaoEncontradaException;
import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.repositories.PautaRepository;
import com.sicredi.avaliacao.util.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PautaServiceImplTest {

    @InjectMocks
    private PautaServiceImpl pautaService;

    @Mock
    private PautaRepository pautaRepository;

    private Pauta pauta;
    private Pauta pautaSalva;

    @BeforeEach
    void setUp() {
        pauta = Factory.criarPautaVazia();
        pautaSalva = Factory.criarPauta();
    }

    @Test
    void deveriaRetornarPauta_QuandoSalvarPauta() {
        // given
        when(pautaRepository.save(pauta)).thenReturn(pautaSalva);

        // when
        var result = pautaService.salvar(pauta);

        // then
        assertNotNull(result);
        verify(pautaRepository, times(1)).save(pauta);
    }

    @Test
    void deveriaRetornarPauta_QuandoBuscarOuFalharComPautaExistente() {
        // given
        var existingId = UUID.randomUUID();
        when(pautaRepository.findById(existingId)).thenReturn(Optional.of(pautaSalva));

        // when
        var result = pautaService.buscarOuFalhar(existingId);

        // then
        assertNotNull(result);
        verify(pautaRepository, times(1)).findById(existingId);
    }

    @Test
    void lancaPautaNaoEncontradaException_QuandoBuscarOuFalharComPautaNaoExistente() {
        // given
        var nonExistsId = UUID.randomUUID();
        when(pautaRepository.findById(nonExistsId)).thenReturn(Optional.empty());

        // when
        assertThrows((PautaNaoEncontradaException.class), () -> pautaService.buscarOuFalhar(nonExistsId));

        // then
        verify(pautaRepository, times(1)).findById(nonExistsId);
    }
}