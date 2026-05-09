package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.BanqueEtudiant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BanqueEtudiantRepository extends JpaRepository<BanqueEtudiant, Long> {
    Optional<BanqueEtudiant> findByTrackingId(UUID trackingId);
    Page<BanqueEtudiant> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable);
}
