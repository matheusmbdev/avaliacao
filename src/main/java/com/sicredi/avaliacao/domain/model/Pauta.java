package com.sicredi.avaliacao.domain.model;

import com.sicredi.avaliacao.domain.enums.PautaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID pautaId;

    @Column(nullable = false)
    private String tema;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PautaStatus pautaStatus = PautaStatus.AGUARDANDO_VOTACAO;

    @OneToMany(mappedBy = "pauta", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    Set<Voto> votos = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Pauta pauta = (Pauta) o;
        return pautaId != null && Objects.equals(pautaId, pauta.pautaId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
