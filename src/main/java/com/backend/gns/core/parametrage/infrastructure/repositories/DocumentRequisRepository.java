package com.backend.gns.core.parametrage.infrastructure.repositories;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.models.DocumentRequis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface DocumentRequisRepository extends JpaRepository<DocumentRequis, Long> {
    Optional<DocumentRequis> findByTrackingId(UUID trackingId);
    Optional<DocumentRequis> findByTypeDocument(TypeDocument typeDocument);
    List<DocumentRequis> findByRequiredTrue();
    List<DocumentRequis> findByNiveauRequis(com.backend.gns.student.domain.enums.StudentNiveau niveauRequis);
}
