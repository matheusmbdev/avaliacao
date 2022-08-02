package com.sicredi.avaliacao.api.v1.model;

import com.sicredi.avaliacao.domain.enums.StatusCpf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssociadoCpf implements Serializable {

    private static final long serialVersionUID = 3079724930873531302L;

    private StatusCpf status;
}
