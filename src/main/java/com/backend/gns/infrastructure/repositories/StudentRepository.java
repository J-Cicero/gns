package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Student;
import com.backend.gns.domain.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.trackingId = :trackingId")
    Optional<Student> findByTrackingId(@Param("trackingId") UUID trackingId);


    @Query("SELECT s FROM Student s WHERE s.email = :email")
    Optional<Student> findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.statutKYC = :statut")
    Long countByStatutKYC(@Param("statut") KycStatus statut);

    @Query("SELECT s FROM Student s " +
           "WHERE s.statutKYC = :statut " +
           "ORDER BY s.createdAt ASC")
    List<Student> findByStatutKYCOrderByCreatedAt(
            @Param("statut")  KycStatus statut
    );
}
