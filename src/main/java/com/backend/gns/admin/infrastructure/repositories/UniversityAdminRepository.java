package com.backend.gns.admin.infrastructure.repositories;

import com.backend.gns.Shared.user.domain.models.UniversityAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UniversityAdminRepository extends JpaRepository<UniversityAdmin, Long> {
    Optional<UniversityAdmin> findByTrackingId(UUID trackingId);
    org.springframework.data.domain.Page<UniversityAdmin> findByUniversiteTrackingId(UUID universiteTrackingId, org.springframework.data.domain.Pageable pageable);
}
