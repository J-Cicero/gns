package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.DocumentEtudiantRequest;
import com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.student.domain.enums.StatutDocument;
import com.backend.gns.Shared.domain.enums.TypeDocument;
import com.backend.gns.student.domain.services.DocumentEtudiantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "DOCUMENT_ETUDIANT", description = "Gestion des documents étudiants")
public class DocumentEtudiantController {

  private final DocumentEtudiantService documentService;

  public DocumentEtudiantController(DocumentEtudiantService documentService) {
    this.documentService = documentService;
  }

  @PostMapping
  @Operation(summary = "Créer un document", description = "Crée un nouveau document étudiant")
  @ApiResponse(responseCode = "201", description = "Document créé avec succès")
  public ResponseEntity<Object> create(@RequestBody DocumentEtudiantRequest request) {
    try {
      DocumentEtudiantResponse response = documentService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/{trackingId}")
  @Operation(summary = "Récupérer un document", description = "Récupère un document par son ID")
  @ApiResponse(responseCode = "200", description = "Document trouvé")
  @ApiResponse(responseCode = "404", description = "Document non trouvé")
  public ResponseEntity<Object> findByTrackingId(@PathVariable UUID trackingId) {
    try {
      return documentService
          .findByTrackingId(trackingId)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Document non trouvé")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PutMapping("/{trackingId}")
  @Operation(
      summary = "Mettre à jour un document",
      description = "Mettre à jour les informations d'un document")
  @ApiResponse(responseCode = "200", description = "Document mis à jour avec succès")
  @ApiResponse(responseCode = "404", description = "Document non trouvé")
  public ResponseEntity<Object> update(
      @PathVariable UUID trackingId, @RequestBody DocumentEtudiantRequest request) {
    try {
      DocumentEtudiantResponse response = documentService.update(trackingId, request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer un document", description = "Supprime un document par son ID")
  @ApiResponse(responseCode = "204", description = "Document supprimé avec succès")
  @ApiResponse(responseCode = "404", description = "Document non trouvé")
  public ResponseEntity<Object> delete(@PathVariable UUID trackingId) {
    try {
      documentService.delete(trackingId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/student/{studentTrackingId}")
  @Operation(
      summary = "Récupérer les documents d'un étudiant",
      description = "Récupère tous les documents d'un étudiant spécifique")
  @ApiResponse(responseCode = "200", description = "Documents trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun document trouvé")
  public ResponseEntity<Object> findByStudentTrackingId(
      @PathVariable UUID studentTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = documentService.findByStudentTrackingId(studentTrackingId, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                Map.of(
                    "error", "NOT_FOUND", "message",
                    "Aucun document trouvé pour cet étudiant"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/inscription/{inscriptionTrackingId}")
  @Operation(
      summary = "Récupérer les documents d'une inscription",
      description = "Récupère tous les documents d'une inscription spécifique")
  @ApiResponse(responseCode = "200", description = "Documents trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun document trouvé")
  public ResponseEntity<Object> findByInscriptionTrackingId(
      @PathVariable UUID inscriptionTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = documentService.findByInscriptionTrackingId(inscriptionTrackingId, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                Map.of(
                    "error", "NOT_FOUND", "message",
                    "Aucun document trouvé pour cette inscription"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/student/{studentTrackingId}/statut/{statut}")
  @Operation(
      summary = "Récupérer les documents d'un étudiant par statut",
      description = "Récupère les documents d'un étudiant avec un statut spécifique")
  @ApiResponse(responseCode = "200", description = "Documents trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun document trouvé")
  public ResponseEntity<Object> findByStudentAndStatut(
      @PathVariable UUID studentTrackingId,
      @PathVariable StatutDocument statut,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses =
          documentService.findByStudentAndStatut(studentTrackingId, statut, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                Map.of(
                    "error", "NOT_FOUND", "message",
                    "Aucun document trouvé avec ce statut"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/statut/{statut}")
  @Operation(
      summary = "Récupérer les documents par statut",
      description = "Récupère tous les documents avec un statut donné")
  @ApiResponse(responseCode = "200", description = "Documents trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun document trouvé")
  public ResponseEntity<Object> findByStatut(
      @PathVariable StatutDocument statut,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = documentService.findByStatut(statut, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                Map.of(
                    "error", "NOT_FOUND", "message",
                    "Aucun document avec ce statut"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping
  @Operation(
      summary = "Récupérer tous les documents",
      description = "Récupère la liste de tous les documents")
  @ApiResponse(responseCode = "200", description = "Documents récupérés avec succès")
  @ApiResponse(responseCode = "404", description = "Aucun document trouvé")
  public ResponseEntity<Object> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = documentService.findAll(pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun document trouvé"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PostMapping("/upload")
  @PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN_GNS')")
  @Operation(
      summary = "Upload document avec extraction IA",
      description =
          "Upload un document, l'envoie à Cloudinary, puis extrait les données avec Gemini")
  @ApiResponse(responseCode = "201", description = "Document uploadé et données extraites")
  @ApiResponse(responseCode = "400", description = "Fichier ou paramètres invalides")
  @ApiResponse(responseCode = "404", description = "Étudiant ou inscription non trouvé")
  public ResponseEntity<Object> uploadDocument(
      @RequestParam("fichier") MultipartFile fichier,
      @RequestParam(value = "inscriptionTrackingId", required = false) UUID inscriptionTrackingId,
      @RequestParam("typeDocument") TypeDocument typeDocument) {
    try {
      if (fichier.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", "INVALID_FILE", "message", "Le fichier ne peut pas être vide"));
      }

      DocumentEtudiantResponse response =
          documentService.uploadDocument(
              fichier, inscriptionTrackingId, typeDocument);

      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "INVALID_REQUEST", "message", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPLOAD_FAILED", "message", e.getMessage()));
    }
  }
}
