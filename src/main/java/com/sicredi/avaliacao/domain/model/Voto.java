package com.sicredi.avaliacao.domain.model;

import com.sicredi.avaliacao.domain.enums.VotoEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID votoId;

    @Enumerated(EnumType.STRING)
    private VotoEnum votoAssociado;

    private String cpfAssociado;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id")
    @ToString.Exclude
    private Pauta pauta;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Voto voto = (Voto) o;
        return votoId != null && Objects.equals(votoId, voto.votoId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
