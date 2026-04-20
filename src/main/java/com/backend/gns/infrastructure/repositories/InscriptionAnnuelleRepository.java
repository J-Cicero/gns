package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.enums.StatutInscription;
import com.backend.gns.domain.models.InscriptionAnnuelle;
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

  @Query(
      "SELECT i FROM InscriptionAnnuelle i WHERE i.student.trackingId = :studentTrackingId "
          + "AND i.anneeAcademique = :anneeAcademique")
  Optional<InscriptionAnnuelle> findByStudentAndAnnee(
      @Param("studentTrackingId") UUID studentTrackingId,
      @Param("anneeAcademique") String anneeAcademique);

  Page<InscriptionAnnuelle> findByStatut(StatutInscription statut, Pageable pageable);

  @Query(
      "SELECT i FROM InscriptionAnnuelle i WHERE i.estBoursier = true "
          + "AND i.statut = :statut")
  Page<InscriptionAnnuelle> findBoursiersByStatut(
      @Param("statut") StatutInscription statut, Pageable pageable);
}
