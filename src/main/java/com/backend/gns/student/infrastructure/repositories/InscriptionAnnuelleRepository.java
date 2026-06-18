package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InscriptionAnnuelleRepository extends JpaRepository<InscriptionAnnuelle, Long> {

  Optional<InscriptionAnnuelle> findByTrackingId(UUID trackingId);

  List<InscriptionAnnuelle> findByStudent_TrackingId(UUID studentTrackingId);

  @Query("SELECT i FROM InscriptionAnnuelle i WHERE i.student.trackingId = :studentTrackingId")
  Page<InscriptionAnnuelle> findByStudentTrackingId(
      @Param("studentTrackingId") UUID studentTrackingId, Pageable pageable);

  @Query(
      "SELECT i FROM InscriptionAnnuelle i WHERE i.student.trackingId = :studentTrackingId AND i.scolariteYear.label = :label")
  Optional<InscriptionAnnuelle> findByStudentTrackingIdAndLabel(
      @Param("studentTrackingId") UUID studentTrackingId, @Param("label") String label);

  Optional<InscriptionAnnuelle> findByStudentAndScolariteYear(
      Student student, ScolariteYear scolariteYear);

  Optional<InscriptionAnnuelle> findByStudentAndScolariteYear_IsOpenTrue(Student student);

  List<InscriptionAnnuelle> findAllByScolariteYear(ScolariteYear scolariteYear);

  Page<InscriptionAnnuelle> findByStudentUniversiteTrackingId(
      UUID universiteTrackingId, Pageable pageable);
}
