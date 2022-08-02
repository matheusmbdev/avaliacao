package com.sicredi.avaliacao.domain.repositories;

import com.sicredi.avaliacao.domain.model.Pauta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, UUID> {

    @EntityGraph(attributePaths = "votos", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Pauta> findById(UUID pautaId);
}
