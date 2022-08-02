package com.sicredi.avaliacao.api.v1.openapi.controller;

import com.sicredi.avaliacao.api.v1.model.PautaModel;
import com.sicredi.avaliacao.api.v1.model.VotoModel;
import com.sicredi.avaliacao.api.v1.model.input.PautaInputModel;
import com.sicredi.avaliacao.api.v1.openapi.controller.annotation.PageableParameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Pautas")
public interface PautaControlleOpenApi {

    @Operation(summary = "Lista as pautas")
    @PageableParameter
    ResponseEntity<Page<PautaModel>> listar(@Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "Busca uma pauta por ID", responses = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(ref = "PautaModel"))
            }),
            @ApiResponse(responseCode = "404",
                    description = "Pauta não encontrada.",
                    content = @Content(schema = @Schema(ref = "Problema")))
    })
    ResponseEntity<PautaModel> buscar(@Parameter(description = "ID de uma pauta",
            example = "52b3a36c-6b69-45f9-9f28-e0b69d98a451", required = true) UUID pautaId);

    @Operation(summary = "Cadastra uma nova pauta", responses = {
            @ApiResponse(responseCode = "201", description = "CREATED", content = {
                    @Content(mediaType = "application/json", schema = @Schema(ref = "PautaModel"))
            })
    })
    ResponseEntity<PautaModel> adicionar(@RequestBody(description = "Representação de uma nova pauta",
            required = true) PautaInputModel pautaInputModel);

    @Operation(summary = "Cria uma sessão de votação para uma pauta", responses = {
            @ApiResponse(responseCode = "201", description = "Sessão de votação iniciada.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(example = "Sessão de votação iniciada"))
            }),
            @ApiResponse(responseCode = "404",
                    description = "Pauta não encontrada.",
                    content = @Content(schema = @Schema(ref = "Problema")))
    })
    ResponseEntity<String> abrirSessao(@Parameter(description = "ID de uma pauta",
            example = "52b3a36c-6b69-45f9-9f28-e0b69d98a451", required = true) UUID pautaId);

    @Operation(summary = "Vota em uma pauta", responses = {
            @ApiResponse(responseCode = "200", description = "Voto contabilizado com sucesso!"),
            @ApiResponse(responseCode = "404",
                    description = "Pauta não encontrada.",
                    content = @Content(schema = @Schema(ref = "Problema")))
    })
    ResponseEntity<String> votar(@Schema(example = "Voto contabilizado com sucesso!") @Parameter(description = "ID de uma pauta",
            example = "52b3a36c-6b69-45f9-9f28-e0b69d98a451", required = true) UUID pautaId,
                                 @RequestBody(description = "Representação de um voto", required = true) VotoModel votoModel);
}
