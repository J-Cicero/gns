package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findByTrackingId(UUID trackingId);
}
