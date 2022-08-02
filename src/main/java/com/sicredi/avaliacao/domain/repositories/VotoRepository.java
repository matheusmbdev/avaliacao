package com.sicredi.avaliacao.domain.repositories;

import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VotoRepository extends JpaRepository<Voto, UUID> {
    boolean existsByCpfAssociadoAndPauta(String cpfAssociado, Pauta pauta);
}
