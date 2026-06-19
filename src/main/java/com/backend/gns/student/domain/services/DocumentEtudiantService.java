package com.backend.gns.student.domain.services;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentEtudiantService {
  DocumentEtudiantResponse uploadDocument(MultipartFile fichier, UUID studentTrackingId, UUID inscriptionTrackingId, TypeDocument typeDocument);
  Optional<DocumentEtudiantResponse> findByTrackingId(UUID trackingId);
  Page<DocumentEtudiantResponse> findByUserTrackingId(UUID userTrackingId, Pageable pageable);
  Page<DocumentEtudiantResponse> findByInscriptionId(UUID inscriptionId, Pageable pageable);
  void delete(UUID trackingId);
  List<DocumentEtudiantResponse> getDocumentsByStudent(UUID studentTrackingId);
}