package com.backend.gns.core.parametrage.infrastructure.repositories;

import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByTrackingId(UUID trackingId);
    List<Document> findByOwnerTrackingId(UUID ownerTrackingId);
    List<Document> findByOwnerTrackingIdAndDocumentType(UUID ownerTrackingId, TypeDocument documentType);
    List<Document> findByOwnerType(ProprietaireType ownerType);
    void deleteByTrackingId(UUID trackingId);
}
