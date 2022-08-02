package com.sicredi.avaliacao.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sessao {

    public Sessao(Pauta pauta) {
        this.pauta = pauta;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID sessaoId;

    @OneToOne()
    @JoinColumn(name = "pauta_id")
    private Pauta pauta;

    private boolean aberta = true;

    public void fechar() {
        setAberta(false);
    }
}
