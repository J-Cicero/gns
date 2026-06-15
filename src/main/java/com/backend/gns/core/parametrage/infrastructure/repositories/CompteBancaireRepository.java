package com.backend.gns.core.infrastructure.repositories;

import com.backend.gns.core.domain.models.CompteBancaire;
import com.backend.gns.core.domain.enums.ProprietaireType ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompteBancaireRepository extends JpaRepository<CompteBancaire, Long> {
    Optional<CompteBancaire> findByTrackingId(UUID trackingId);
    java.util.List<CompteBancaire> findByOwnerType(ProprietaireType typeProprietaire);
}