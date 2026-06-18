package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.models.ScolariteYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScolariteYearRepository extends JpaRepository<ScolariteYear, Long> {
  Optional<ScolariteYear> findByTrackingId(UUID trackingId);

  Optional<ScolariteYear> findByIsOpenTrue();
}
