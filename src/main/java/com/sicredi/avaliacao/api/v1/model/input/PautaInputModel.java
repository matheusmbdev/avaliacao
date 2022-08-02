package com.sicredi.avaliacao.api.v1.model.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PautaInputModel implements Serializable {

    private static final long serialVersionUID = -4432267468796879380L;

    @NotBlank
    private String tema;
}
