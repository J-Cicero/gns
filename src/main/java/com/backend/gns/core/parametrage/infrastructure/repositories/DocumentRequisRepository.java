package com.backend.gns.core.infrastructure.repositories;

import com.backend.gns.core.domain.models.DocumentRequis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

import java.util.List;
import com.backend.gns.student.domain.enums.TargetType;

@Repository
public interface DocumentRequisRepository extends JpaRepository<DocumentRequis, Long> {
    Optional<DocumentRequis> findByTrackingId(UUID trackingId);
    List<DocumentRequis> findByTargetTypeAndEstActifTrueAndObligatoireTrue(TargetType targetType);
}
