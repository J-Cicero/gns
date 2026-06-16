package com.backend.gns.student.domain.services;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.application.dtos.responses.DocumentResponse;
import com.backend.gns.student.domain.enums.StatutDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import java.util.UUID;

public interface DocumentEtudiantService {
  DocumentResponse uploadDocument(MultipartFile fichier, UUID studentTrackingId, UUID inscriptionTrackingId, TypeDocument typeDocument);
  Optional<DocumentResponse> findByTrackingId(UUID trackingId);
  Page<DocumentResponse> findByUserTrackingId(UUID userTrackingId, Pageable pageable);
  Page<DocumentResponse> findByInscriptionId(UUID inscriptionId, Pageable pageable);
  void delete(UUID trackingId);
  java.util.List<com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse> getDocumentsByStudent(UUID studentTrackingId);
}
