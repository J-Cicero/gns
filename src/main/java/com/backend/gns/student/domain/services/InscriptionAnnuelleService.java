package com.backend.gns.student.domain.services;

import com.backend.gns.student.application.dtos.requests.InscriptionAnnuelleRequest;
import com.backend.gns.student.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.student.domain.enums.StatutInscription;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InscriptionAnnuelleService {

  InscriptionAnnuelleResponse create(InscriptionAnnuelleRequest request);

  Optional<InscriptionAnnuelleResponse> findByTrackingId(UUID trackingId);

  InscriptionAnnuelleResponse update(UUID trackingId, InscriptionAnnuelleRequest request);

  InscriptionAnnuelleResponse updateStatus(UUID trackingId, StatutInscription statut);

  InscriptionAnnuelleResponse updateDefinitif(UUID trackingId, boolean estInscritDefinitif);

  void delete(UUID trackingId);

  Page<InscriptionAnnuelleResponse> findByStudentTrackingId(
      UUID studentTrackingId, Pageable pageable);

  Optional<InscriptionAnnuelleResponse> findByStudentAndAnnee(
      UUID studentTrackingId, String anneeAcademique);

  Page<InscriptionAnnuelleResponse> findByStatut(StatutInscription statut, Pageable pageable);

  Page<InscriptionAnnuelleResponse> findAll(Pageable pageable);

  Page<InscriptionAnnuelleResponse> findByUniversiteTrackingId(
      UUID universiteTrackingId, Pageable pageable);
}
