package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.domain.models.Student;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

  Optional<Student> findByTrackingId(UUID trackingId);

  Optional<Student> findByEmail(String email);

  Long countByStatutKYC(KycStatus statut);

  Page<Student> findByStatutKYCOrderByCreatedAtAsc(KycStatus statut, Pageable pageable);
}
