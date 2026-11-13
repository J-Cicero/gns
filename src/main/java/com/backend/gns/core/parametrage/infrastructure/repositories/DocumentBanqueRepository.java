package com.backend.gns.core.parametrage.infrastructure.repositories;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.models.DocumentBanque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentBanqueRepository extends JpaRepository<DocumentBanque, Long> {

    Optional<DocumentBanque> findByTrackingId(UUID trackingId);
    Optional<DocumentBanque> findFirstByCompteBancaireTrackingIdAndDocumentTypeOrderByUploadedAtDesc(UUID compteTrackingId, TypeDocument documentType);
    List<DocumentBanque> findAllByCompteBancaireTrackingIdAndDocumentTypeOrderByUploadedAtDesc(UUID compteTrackingId, TypeDocument documentType);
    List<DocumentBanque> findByCompteBancaireTrackingIdOrderByUploadedAtDesc(UUID compteTrackingId);
    List<DocumentBanque> findByCompteBancaireProprietaireTrackingIdOrderByUploadedAtDesc(UUID proprietaireTrackingId);
}