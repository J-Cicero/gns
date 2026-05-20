package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.enums.TypeRegleBourse;
import com.backend.gns.student.domain.models.RegleBourseDbs;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegleBourseDbsRepository extends JpaRepository<RegleBourseDbs, Long> {
    Optional<RegleBourseDbs> findByTrackingId(UUID trackingId);
    Optional<RegleBourseDbs> findByTypeRegleAndEstActifTrue(TypeRegleBourse typeRegle);
}
