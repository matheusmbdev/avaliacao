package com.sicredi.avaliacao.domain.repositories;

import com.sicredi.avaliacao.domain.model.Pauta;
import com.sicredi.avaliacao.domain.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, UUID> {

    Optional<Sessao> findByPauta(Pauta pauta);
}
