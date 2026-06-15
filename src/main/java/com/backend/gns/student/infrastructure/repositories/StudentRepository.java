package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.core.domain.enums.KycStatus;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.models.Universite;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

  Optional<Student> findByTrackingId(UUID trackingId);

  @Query("SELECT s FROM Student s WHERE s.wallet.trackingId = :walletTrackingId")
  Optional<Student> findByWalletTrackingId(@Param("walletTrackingId") UUID walletTrackingId);

  Optional<Student> findByEmail(String email);

  Long countByKycStatus(KycStatus statut);

  long countByIsActive(boolean isActive);

  Page<Student> findByKycStatusOrderByCreatedAtAsc(KycStatus statut, Pageable pageable);

  Page<Student> findByUniversiteTrackingId(UUID universiteTrackingId, Pageable pageable);

  long countByUniversite(Universite universite);

  @Query(
      "SELECT COUNT(s) FROM Student s WHERE s.universite = :universite AND s.kycStatus = :statut")
  Long countByUniversiteAndKycStatus(Universite universite, KycStatus statut);
}
