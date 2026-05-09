package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.InscriptionAnnuelleRequest;
import com.backend.gns.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.domain.enums.StatutInscription;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InscriptionAnnuelleService {

  InscriptionAnnuelleResponse create(InscriptionAnnuelleRequest request);

  Optional<InscriptionAnnuelleResponse> findByTrackingId(UUID trackingId);

  InscriptionAnnuelleResponse update(UUID trackingId, InscriptionAnnuelleRequest request);

  void delete(UUID trackingId);

  Page<InscriptionAnnuelleResponse> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable);

  Optional<InscriptionAnnuelleResponse> findByStudentAndAnnee(
      UUID studentTrackingId, String anneeAcademique);

  Page<InscriptionAnnuelleResponse> findByStatut(StatutInscription statut, Pageable pageable);

  Page<InscriptionAnnuelleResponse> findAll(Pageable pageable);
}
