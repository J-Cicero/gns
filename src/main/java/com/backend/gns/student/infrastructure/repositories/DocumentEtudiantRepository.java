package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.enums.StatutDocument;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentEtudiantRepository extends JpaRepository<DocumentEtudiant, Long> {

  Optional<DocumentEtudiant> findByTrackingId(UUID trackingId);

    @Query("SELECT d FROM DocumentEtudiant d WHERE d.student.trackingId = :studentTrackingId")
    List<DocumentEtudiant> findByStudentTrackingId(@Param("studentTrackingId") UUID studentTrackingId);

  @Query("SELECT d FROM DocumentEtudiant d WHERE d.inscription.student.trackingId = :studentTrackingId")
  Page<DocumentEtudiant> findByStudentTrackingId(
      @Param("studentTrackingId") UUID studentTrackingId, Pageable pageable);

  @Query(
      "SELECT d FROM DocumentEtudiant d WHERE d.inscription.trackingId = :inscriptionTrackingId")
  Page<DocumentEtudiant> findByInscriptionTrackingId(
      @Param("inscriptionTrackingId") UUID inscriptionTrackingId, Pageable pageable);

  @Query(
      "SELECT d FROM DocumentEtudiant d WHERE d.inscription.student.trackingId = :studentTrackingId "
          + "AND d.statut = :statut")
  Page<DocumentEtudiant> findByStudentAndStatut(
      @Param("studentTrackingId") UUID studentTrackingId,
      @Param("statut") StatutDocument statut,
      Pageable pageable);

  Page<DocumentEtudiant> findByStatut(StatutDocument statut, Pageable pageable);
}
