package com.sicredi.avaliacao.api.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@Schema(name = "Problema")
public class Problem {
    @Schema(example = "400")
    private Integer status;

    @Schema(example = "https://avaliacao.com.br/dados-invalidos")
    private String type;

    @Schema(example = "Dados inválidos")
    private String title;

    @Schema(example = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.")
    private String detail;

    @Schema(example = "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.")
    private String userMessage;

    @Schema(example = "2022-08-02T00:52:41.6249978Z")
    private OffsetDateTime timestamp;

    @Schema(description = "Lista de objetos ou campos que geraram o erro")
    private List<Object> objects;

    @Getter
    @Builder
    @Schema(name = "ObjetoProblema")
    public static class Object {

        @Schema(example = "tema")
        private String name;
        @Schema(example = "Não deve estar em branco")
        private String userMessage;
    }
}
