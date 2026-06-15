package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.models.ScolariteYear;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScolariteYearRepository extends JpaRepository<ScolariteYear, Long> {
  Optional<ScolariteYear> findByTrackingId(UUID trackingId);

  Optional<ScolariteYear> findByIsOpenTrue();
}
