package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("SELECT a FROM Admin a WHERE a.email = :email")
    Optional<Admin> findByEmail(@Param("email") String email);

    @Query("SELECT a FROM Admin a WHERE a.trackingId = :trackingId")
    Optional<Admin> findByTrackingId(@Param("trackingId") UUID trackingId);
}
