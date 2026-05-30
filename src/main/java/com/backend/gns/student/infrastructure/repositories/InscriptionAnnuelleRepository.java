package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.Student;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InscriptionAnnuelleRepository extends JpaRepository<InscriptionAnnuelle, Long> {

  Optional<InscriptionAnnuelle> findByTrackingId(UUID trackingId);

  @Query("SELECT i FROM InscriptionAnnuelle i WHERE i.student.trackingId = :studentTrackingId")
  Page<InscriptionAnnuelle> findByStudentTrackingId(
      @Param("studentTrackingId") UUID studentTrackingId, Pageable pageable);

  @Query("SELECT i FROM InscriptionAnnuelle i WHERE i.student.trackingId = :studentTrackingId AND i.scolariteYear.libelle = :annee")
  Optional<InscriptionAnnuelle> findByStudentTrackingIdAndAnnee(
      @Param("studentTrackingId") UUID studentTrackingId, @Param("annee") String annee);

  Optional<InscriptionAnnuelle> findByStudentAndScolariteYear(
      Student student,
      ScolariteYear scolariteYear);

  List<InscriptionAnnuelle> findAllByScolariteYear(
      ScolariteYear scolariteYear);

  Page<InscriptionAnnuelle> findByStatut(StatutInscription statut, Pageable pageable);

  Page<InscriptionAnnuelle> findByStudentUniversiteTrackingId(
      UUID universiteTrackingId, Pageable pageable);

  @Query(
      "SELECT i FROM InscriptionAnnuelle i WHERE i.estBoursier = true " + "AND i.statut = :statut")
  Page<InscriptionAnnuelle> findBoursiersByStatut(
      @Param("statut") StatutInscription statut, Pageable pageable);
}
