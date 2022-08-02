package com.sicredi.avaliacao.api.v1.model;

import com.sicredi.avaliacao.domain.enums.VotoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VotoModel implements Serializable {


    private static final long serialVersionUID = -8499663574950470398L;

    @NotNull
    private VotoEnum votoAssociado;
    @CPF
    @NotBlank
    private String cpfAssociado;
}
