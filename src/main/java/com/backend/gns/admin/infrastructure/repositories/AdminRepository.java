package com.backend.gns.admin.infrastructure.repositories;

import com.backend.gns.admin.domain.models.Admin;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

  Optional<Admin> findByEmail(String email);

  Optional<Admin> findByTrackingId(UUID trackingId);
}
