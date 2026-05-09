package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Admin;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

  Optional<Admin> findByEmail(String email);

  Optional<Admin> findByTrackingId(UUID trackingId);
}
