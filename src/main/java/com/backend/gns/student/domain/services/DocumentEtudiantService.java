package com.backend.gns.student.domain.services;

import com.backend.gns.student.application.dtos.requests.DocumentEtudiantRequest;
import com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.student.domain.enums.StatutDocument;
import com.backend.gns.core.domain.enums.TypeDocument;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentEtudiantService {

  DocumentEtudiantResponse create(DocumentEtudiantRequest request);

  Optional<DocumentEtudiantResponse> findByTrackingId(UUID trackingId);

  DocumentEtudiantResponse update(UUID trackingId, DocumentEtudiantRequest request);

  void delete(UUID trackingId);

  Page<DocumentEtudiantResponse> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable);

  Page<DocumentEtudiantResponse> findByInscriptionTrackingId(
      UUID inscriptionTrackingId, Pageable pageable);

  Page<DocumentEtudiantResponse> findByStudentAndStatut(
      UUID studentTrackingId, StatutDocument statut, Pageable pageable);

  Page<DocumentEtudiantResponse> findByStatut(StatutDocument statut, Pageable pageable);

  Page<DocumentEtudiantResponse> findAll(Pageable pageable);

  DocumentEtudiantResponse uploadDocument(
      MultipartFile fichier,
      UUID inscriptionTrackingId,
      TypeDocument typeDocument);
}
