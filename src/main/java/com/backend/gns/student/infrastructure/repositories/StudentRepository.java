package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.core.domain.enums.KycStatus;
import com.backend.gns.student.domain.models.Student;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

  Optional<Student> findByTrackingId(UUID trackingId);

  Optional<Student> findByEmail(String email);

  Long countByStatutKYC(KycStatus statut);

  long countByEstActif(boolean estActif);

  Page<Student> findByStatutKYCOrderByCreatedAtAsc(KycStatus statut, Pageable pageable);

  Page<Student> findByUniversiteTrackingId(UUID universiteTrackingId, Pageable pageable);

  long countByUniversite(com.backend.gns.academique.domain.models.Universite universite);
  
  long countByUniversiteAndStatutKYC(com.backend.gns.academique.domain.models.Universite universite, KycStatus statut);
}
