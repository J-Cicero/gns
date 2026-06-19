package com.backend.gns.core.parametrage.infrastructure.repositories;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.models.CompteBancaire;
import com.backend.gns.core.parametrage.domain.models.DocumentBanque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentBanqueRepository extends JpaRepository<DocumentBanque, Long> {

    Optional<DocumentBanque> findByTrackingId(UUID trackingId);
    Optional<DocumentBanque> findByCompteBancaireTrackingIdAndDocumentType(UUID compteTrackingId, TypeDocument documentType);
}