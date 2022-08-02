package com.sicredi.avaliacao.api.v1.model;

import com.sicredi.avaliacao.domain.enums.PautaStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PautaModel")
public class PautaModel implements Serializable {

    private static final long serialVersionUID = -2794410215142600458L;

    @Schema(example = "52b3a36c-6b69-45f9-9f28-e0b69d98a451")
    private UUID pautaId;

    @Schema(example = "PEC 98795")
    private String tema;

    @Schema(example = "2022-08-01T17:34:32.346995-03:00")
    private OffsetDateTime dataCriacao;

    @Schema(example = "AGUARDANDO_VOTACAO")
    private PautaStatus pautaStatus;
}
