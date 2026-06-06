package com.backend.gns.user.infrastructure.repositories;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.gns.user.domain.models.UniversityAdmin;

@Repository
public interface UniversityAdminRepository extends JpaRepository<UniversityAdmin, Long> {
  Optional<UniversityAdmin> findByTrackingId(UUID trackingId);

  org.springframework.data.domain.Page<UniversityAdmin> findByUniversiteTrackingId(
      UUID universiteTrackingId, org.springframework.data.domain.Pageable pageable);
}
