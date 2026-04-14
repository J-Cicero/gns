package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.trackingId = :trackingId")
    Optional<Student> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT s FROM Student s WHERE s.matriculeUL = :matricule")
    Optional<Student> findByMatriculeUL(@Param("matricule") String matricule);

    @Query("SELECT s FROM Student s WHERE s.email = :email")
    Optional<Student> findByEmail(@Param("email") String email);

    @Query("SELECT s FROM Student s WHERE s.niveau = :niveau")
    List<Student> findByNiveau(@Param("niveau") com.backend.gns.domain.enums.StudentNiveau niveau);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.statutKYC = :statut")
    Long countByStatutKYC(@Param("statut") com.backend.gns.domain.enums.KycStatus statut);

    @Query("SELECT s FROM Student s " +
           "WHERE s.statutKYC = :statut " +
           "ORDER BY s.createdAt ASC")
    List<Student> findByStatutKYCOrderByCreatedAt(
            @Param("statut") com.backend.gns.domain.enums.KycStatus statut
    );
}
