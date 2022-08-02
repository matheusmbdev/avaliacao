package com.sicredi.avaliacao.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicredi.avaliacao.api.v1.assembler.PautaInputModelDissasembler;
import com.sicredi.avaliacao.api.v1.assembler.PautaModelAssembler;
import com.sicredi.avaliacao.api.v1.assembler.VotoModelDissasembler;
import com.sicredi.avaliacao.api.v1.controller.PautaController;
import com.sicredi.avaliacao.api.v1.model.PautaModel;
import com.sicredi.avaliacao.api.v1.model.VotoModel;
import com.sicredi.avaliacao.api.v1.model.input.PautaInputModel;
import com.sicredi.avaliacao.domain.enums.PautaStatus;
import com.sicredi.avaliacao.domain.enums.VotoEnum;
import com.sicredi.avaliacao.domain.exception.PautaNaoEncontradaException;
import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Sessao;
import com.sicredi.avaliacao.domain.model.Voto;
import com.sicredi.avaliacao.domain.repositories.PautaRepository;
import com.sicredi.avaliacao.domain.services.PautaService;
import com.sicredi.avaliacao.domain.services.SessaoService;
import com.sicredi.avaliacao.domain.services.VotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PautaService pautaService;

    @MockBean
    private PautaRepository pautaRepository;

    @MockBean
    private SessaoService sessaoService;

    @MockBean
    private VotoService votoService;

    @MockBean
    private PautaModelAssembler pautaModelAssembler;

    @MockBean
    private PautaInputModelDissasembler pautaInputModelDissasembler;

    @MockBean
    private VotoModelDissasembler votoModelDissasembler;

    @Autowired
    private ObjectMapper objectMapper;

    private PageImpl<PautaModel> pautaModelPage;
    private PageImpl<Pauta> pautaPage;

    private Pauta pauta;
    private PautaModel pautaModel;

    private PautaInputModel pautaInputModel;
    private UUID pautaId;

    private Sessao sessao;

    private Voto voto;

    private VotoModel votoModel;

    @BeforeEach
    void setUp() {
        pautaId = UUID.randomUUID();
        pauta = getPauta();
        pautaModel = getPautaModel();
        pautaInputModel = getPautaInputModel();
        sessao = getSessao();
        sessao.setPauta(pauta);
        votoModel = getVotoModel();
        voto = getVoto();
    }

    @Test
    void listar() throws Exception {
        // given
        List<Pauta> pautaList = List.of(pauta);
        List<PautaModel> pautaModelList = List.of(pautaModel);
        pautaPage = new PageImpl<>(pautaList);
        when(pautaRepository.findAll(any(PageRequest.class))).thenReturn(pautaPage);
        when(pautaModelAssembler.toCollectionModel(pautaPage.getContent())).thenReturn(pautaModelList);

        // when
        mockMvc.perform(get("/v1/pauta")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        verify(pautaRepository, times(1)).findAll(any(PageRequest.class));

    }

    @Test
    void deveriRetornaPauta_QuandoBuscarPautaExistente() throws Exception {
        // given
        when(pautaService.buscarOuFalhar(pautaId)).thenReturn(pauta);
        when(pautaModelAssembler.toModel(pauta)).thenReturn(pautaModel);

        // when
        mockMvc.perform(get("/v1/pauta/{pautaId}", pautaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tema").exists())
                .andExpect(jsonPath("$.pautaStatus").exists());

        // then
        verify(pautaService, times(1)).buscarOuFalhar(pautaId);
        verify(pautaModelAssembler, times(1)).toModel(pauta);

    }

    @Test
    void deveriRetornaPautaNaoEncontradaException_QuandoBuscarPautaInexistente() throws Exception {
        // given
        when(pautaService.buscarOuFalhar(pautaId)).thenThrow(PautaNaoEncontradaException.class);

        // when
        mockMvc.perform(get("/v1/pauta/{pautaId}", pautaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // then
        verify(pautaService, times(1)).buscarOuFalhar(pautaId);
    }

    @Test
    void deveriaRetornarApauta_QuandoAdicionarUmaNovaPauta() throws Exception {
        // given
        var pautaSalva = getPauta();
        when(pautaInputModelDissasembler.toDomainObject(pautaInputModel)).thenReturn(pauta);
        when(pautaService.salvar(pauta)).thenReturn(pautaSalva);
        when(pautaModelAssembler.toModel(pautaSalva)).thenReturn(pautaModel);

        var jsonBody = objectMapper.writeValueAsString(pautaInputModel);

        // when
        mockMvc.perform(post("/v1/pauta")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tema").exists())
                .andExpect(jsonPath("$.pautaStatus").exists());

        // then
        verify(pautaService, times(1)).salvar(pauta);
        verify(pautaModelAssembler, times(1)).toModel(pautaSalva);
        verify(pautaInputModelDissasembler, times(1)).toDomainObject(pautaInputModel);
    }

    @Test
    void deveriAbrirSessaoParaUmaPauta_QuandoAbrirSessao() throws Exception {
        when(pautaService.buscarOuFalhar(pautaId)).thenReturn(pauta);
        doNothing().when(sessaoService).verificarSeExisteSessao(pauta);
        when(sessaoService.salvar(sessao)).thenReturn(sessao);

        // when
        mockMvc.perform(post("/v1/pauta/{pautaId}/abrir-sessao", pautaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // then
        verify(pautaService, times(1)).buscarOuFalhar(pautaId);
        verify(sessaoService, times(1)).verificarSeExisteSessao(pauta);
        verify(sessaoService, times(1)).salvar(sessao);

    }

    @Test
    void votar() throws Exception {
        // given
        when(pautaService.buscarOuFalhar(pautaId)).thenReturn(pauta);
        doNothing().when(sessaoService).validarSessao(pauta);
        doNothing().when(votoService).validarVoto(votoModel.getCpfAssociado(), pauta);
        when(votoModelDissasembler.toDomainObject(votoModel)).thenReturn(voto);
        when(votoService.salvar(voto)).thenReturn(voto);
        var jsonBody = objectMapper.writeValueAsString(votoModel);
        //when
        mockMvc.perform(post("/v1/pauta/{pautaId}/votar", pautaId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        verify(pautaService, times(1)).buscarOuFalhar(pautaId);
        verify(sessaoService, times(1)).validarSessao(pauta);
        verify(votoService, times(1)).validarVoto(votoModel.getCpfAssociado(), pauta);
        verify(sessaoService, times(1)).validarSessao(pauta);
        verify(votoService, times(1)).salvar(voto);
    }

    private Pauta getPauta() {
        return Pauta.builder()
                .pautaId(UUID.randomUUID())
                .tema("tema")
                .dataCriacao(OffsetDateTime.now())
                .build();
    }

    private PautaModel getPautaModel() {
        return PautaModel.builder()
                .tema("tema model")
                .pautaStatus(PautaStatus.AGUARDANDO_VOTACAO)
                .build();
    }

    private PautaInputModel getPautaInputModel() {
        return PautaInputModel.builder()
                .tema("input tema")
                .build();
    }

    private Sessao getSessao() {
        return Sessao.builder()
                .aberta(true)
                .build();
    }

    private VotoModel getVotoModel() {
        return VotoModel.builder()
                .votoAssociado(VotoEnum.SIM)
                .cpfAssociado("083.827.043-36")
                .build();
    }

    private Voto getVoto() {
        return Voto.builder()
                .votoAssociado(VotoEnum.SIM)
                .cpfAssociado("083.827.043-36")
                .build();
    }
}