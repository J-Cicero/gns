package com.backend.gns.core.parametrage.infrastructure.repositories;

import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.models.CompteBancaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Added List import
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompteBancaireRepository extends JpaRepository<CompteBancaire, Long> {
    Optional<CompteBancaire> findByTrackingId(UUID trackingId);
    java.util.List<CompteBancaire> findByOwnerType(ProprietaireType typeProprietaire);
    Optional<CompteBancaire> findByProprietaireTrackingId(UUID proprietaireTrackingId);
}
