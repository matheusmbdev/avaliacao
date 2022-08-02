package com.sicredi.avaliacao.api.v1.controller;

import com.sicredi.avaliacao.api.v1.assembler.PautaInputModelDissasembler;
import com.sicredi.avaliacao.api.v1.assembler.PautaModelAssembler;
import com.sicredi.avaliacao.api.v1.assembler.VotoModelDissasembler;
import com.sicredi.avaliacao.api.v1.model.PautaModel;
import com.sicredi.avaliacao.api.v1.model.VotoModel;
import com.sicredi.avaliacao.api.v1.model.input.PautaInputModel;
import com.sicredi.avaliacao.api.v1.openapi.controller.PautaControlleOpenApi;
import com.sicredi.avaliacao.domain.enums.PautaStatus;
import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Sessao;
import com.sicredi.avaliacao.domain.model.Voto;
import com.sicredi.avaliacao.domain.repositories.PautaRepository;
import com.sicredi.avaliacao.domain.services.PautaService;
import com.sicredi.avaliacao.domain.services.SessaoService;
import com.sicredi.avaliacao.domain.services.VotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController()
@RequestMapping("/v1/pauta")
@RequiredArgsConstructor
@Log4j2
public class PautaController implements PautaControlleOpenApi {

    public static final long TEMPO_DE_SESSAO = 60L;
    private final PautaService pautaService;
    private final PautaRepository pautaRepository;
    private final SessaoService sessaoService;
    private final VotoService votoService;
    private final PautaModelAssembler pautaModelAssembler;
    private final PautaInputModelDissasembler pautaInputModelDissasembler;
    private final VotoModelDissasembler votoModelDissasembler;

    @Override
    @GetMapping()
    public ResponseEntity<Page<PautaModel>> listar(@PageableDefault(sort = "dataCriacao", direction = Sort.Direction.DESC)
                                                   Pageable pageable) {
        Page<Pauta> pautasPage = pautaRepository.findAll(pageable);
        List<PautaModel> pautaModelList = pautaModelAssembler.toCollectionModel(pautasPage.getContent());
        return ResponseEntity.ok(new PageImpl<>(pautaModelList, pageable, pautasPage.getTotalElements()));
    }

    @Override
    @GetMapping("/{pautaId}")
    public ResponseEntity<PautaModel> buscar(@PathVariable UUID pautaId) {
        Pauta pauta = pautaService.buscarOuFalhar(pautaId);
        return ResponseEntity.ok(pautaModelAssembler.toModel(pauta));
    }

    @Override
    @PostMapping()
    public ResponseEntity<PautaModel> adicionar(@RequestBody() @Valid PautaInputModel pautaInputModel) {
        Pauta pauta = pautaService.salvar(pautaInputModelDissasembler.toDomainObject(pautaInputModel));
        log.debug("POST cadastro de pauta, pautaId salva {}", pauta.getPautaId());
        log.info("Pauta salva com sucesso, pautaId {}", pauta.getPautaId());
        return new ResponseEntity<>(pautaModelAssembler.toModel(pauta), HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/{pautaId}/abrir-sessao")
    public ResponseEntity<String> abrirSessao(@PathVariable UUID pautaId) {
        Pauta pauta = pautaService.buscarOuFalhar(pautaId);
        sessaoService.verificarSeExisteSessao(pauta);
        pauta.setPautaStatus(PautaStatus.EM_VOTACAO);
        Sessao sessao = new Sessao(pauta);
        sessaoService.salvar(sessao);
        log.debug("POST cadastro de sessão, sessaoId salva {}, pautaId {}", sessao.getSessaoId(), pautaId);
        log.info("Sessão de votação criada com sucesso, sessaoId {}, pautaId {}", sessao.getSessaoId(), pautaId);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> sessaoService.fecharVotacao(sessao, pauta), TEMPO_DE_SESSAO, TimeUnit.SECONDS);

        return new ResponseEntity<>("Sessão de votação iniciada.", HttpStatus.CREATED);
    }

    @Override
    @PostMapping("/{pautaId}/votar")
    public ResponseEntity<String> votar(@PathVariable UUID pautaId, @RequestBody() @Valid VotoModel votoModel) {
        Pauta pauta = pautaService.buscarOuFalhar(pautaId);
        sessaoService.validarSessao(pauta);

        votoService.validarVoto(votoModel.getCpfAssociado(), pauta);
        Voto voto = votoModelDissasembler.toDomainObject(votoModel);
        voto.setPauta(pauta);
        votoService.salvar(voto);
        log.debug("POST cadastro do voto, associado {}, pautaId {}", votoModel.getCpfAssociado(), pautaId);
        log.info("Voto registrado com sucesso para o associado {}, na pauta {}", votoModel.getCpfAssociado(), pauta.toString());
        return ResponseEntity.ok("Voto contabilizado com sucesso!");
    }
}
