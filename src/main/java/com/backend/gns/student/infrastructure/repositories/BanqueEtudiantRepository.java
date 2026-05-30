package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.models.BanqueEtudiant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanqueEtudiantRepository extends JpaRepository<BanqueEtudiant, Long> {
  Optional<BanqueEtudiant> findByTrackingId(UUID trackingId);

  Page<BanqueEtudiant> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable);
}
