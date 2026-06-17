package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.models.DocumentEtudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentEtudiantRepository extends JpaRepository<DocumentEtudiant, Long> {
    Optional<DocumentEtudiant> findByTrackingId(UUID trackingId);
    Page<DocumentEtudiant> findByOwnerTrackingId(UUID ownerTrackingId, Pageable pageable);
    List<DocumentEtudiant> findByOwnerTrackingId(UUID ownerTrackingId);
}
