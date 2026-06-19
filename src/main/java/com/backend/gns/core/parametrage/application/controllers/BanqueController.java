package com.backend.gns.core.parametrage.application.controllers;

import com.backend.gns.core.parametrage.application.dtos.requests.BanqueRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.BanqueResponse;
import com.backend.gns.core.parametrage.domain.services.BanqueService;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.services.impl.CloudinaryStorageService;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/banques")
@RequiredArgsConstructor
public class BanqueController {

  private final BanqueService banqueService;
  private final CloudinaryStorageService cloudinaryService;
  private final DocumentEtudiantRepository documentRepository;

  @GetMapping
  public ResponseEntity<List<BanqueResponse>> getAllBanques() {
    return ResponseEntity.ok(banqueService.getAllBanques());
  }

  @PostMapping
  public ResponseEntity<BanqueResponse> createBanque(@RequestBody BanqueRequest request) {
    return ResponseEntity.ok(banqueService.createBanque(request));
  }

  /**
   * Endpoint permettant à un admin GNS d'uploader un document RIB
   * sans nécessiter d'inscription étudiante associée.
   */
  @PostMapping("/upload-rib")
  public ResponseEntity<?> uploadRibDocument(@RequestParam("fichier") MultipartFile fichier) {
    try {
      String refId = UUID.randomUUID().toString();
      Map<String, String> uploadResult = cloudinaryService.upload(fichier, refId);

      DocumentEtudiant document = new DocumentEtudiant();
      document.setTrackingId(UUID.randomUUID());
      document.setDocumentType(TypeDocument.RIB);
      document.setFileUrl(uploadResult.get("url"));
      document.setProviderPublicId(uploadResult.get("publicId"));
      document.setStatus(StatutDocument.EN_ATTENTE);
      document.setUploadedAt(LocalDateTime.now());

      DocumentEtudiant saved = documentRepository.save(document);
      return ResponseEntity.status(HttpStatus.CREATED).body(
          Map.of(
              "trackingId", saved.getTrackingId().toString(),
              "urlFichier", saved.getFileUrl(),
              "message", "Document RIB uploadé avec succès"
          )
      );
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPLOAD_FAILED", "message", e.getMessage()));
    }
  }
}
