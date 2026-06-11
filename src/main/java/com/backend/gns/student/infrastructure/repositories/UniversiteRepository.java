package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.models.Universite;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversiteRepository extends JpaRepository<Universite, Long> {
  Optional<Universite> findByTrackingId(UUID trackingId);

  Optional<Universite> findByCode(String code);
}
