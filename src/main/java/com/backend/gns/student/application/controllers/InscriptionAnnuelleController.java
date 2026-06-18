package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.InscriptionAnnuelleRequest;
import com.backend.gns.student.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.services.InscriptionAnnuelleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/inscriptions")
@Tag(name = "INSCRIPTION_ANNUELLE", description = "Gestion des inscriptions annuelles")
public class InscriptionAnnuelleController {

  private final InscriptionAnnuelleService inscriptionService;

  public InscriptionAnnuelleController(InscriptionAnnuelleService inscriptionService) {
    this.inscriptionService = inscriptionService;
  }

  @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "Créer une inscription avec carte",
      description = "Crée une nouvelle inscription annuelle avec upload de la carte étudiant")
  public ResponseEntity<?> createWithFile(
      @RequestPart("request") String requestJson,
      @RequestPart("carte") org.springframework.web.multipart.MultipartFile carte) throws Exception {
    
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    InscriptionAnnuelleRequest request = mapper.readValue(requestJson, InscriptionAnnuelleRequest.class);
    
    return ResponseEntity.status(HttpStatus.CREATED).body(inscriptionService.createWithCarte(request, carte));
  }

  @PostMapping("/simple")
  @Operation(
      summary = "Créer une inscription simple",
      description = "Crée une nouvelle inscription annuelle sans fichier")
  @ApiResponse(responseCode = "201", description = "Inscription créée avec succès")
  public ResponseEntity<?> create(@RequestBody InscriptionAnnuelleRequest request) {
    try {
      InscriptionAnnuelleResponse response = inscriptionService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/{trackingId}")
  @Operation(
      summary = "Récupérer une inscription",
      description = "Récupère une inscription par son ID")
  @ApiResponse(responseCode = "200", description = "Inscription trouvée")
  @ApiResponse(responseCode = "404", description = "Inscription non trouvée")
  public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
    try {
      return inscriptionService
          .findByTrackingId(trackingId)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Inscription non trouvée")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PutMapping("/{trackingId}")
  @Operation(
      summary = "Mettre à jour une inscription",
      description = "Mettre à jour les informations d'une inscription")
  @ApiResponse(responseCode = "200", description = "Inscription mise à jour avec succès")
  @ApiResponse(responseCode = "404", description = "Inscription non trouvée")
  public ResponseEntity<?> update(
      @PathVariable UUID trackingId, @RequestBody InscriptionAnnuelleRequest request) {
    try {
      InscriptionAnnuelleResponse response = inscriptionService.update(trackingId, request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @PatchMapping("/{trackingId}/statut")
  @Operation(
      summary = "Mettre à jour le statut",
      description = "Mettre à jour uniquement le statut d'une inscription")
  public ResponseEntity<?> updateStatus(
      @PathVariable UUID trackingId, @RequestParam StatutInscription statut) {
    try {
      InscriptionAnnuelleResponse response = inscriptionService.updateStatus(trackingId, statut);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @PatchMapping("/{trackingId}/definitif")
  @Operation(
      summary = "Mettre à jour l'inscription définitive",
      description = "Mettre à jour le marqueur estInscritDefinitif d'une inscription")
  public ResponseEntity<?> updateDefinitif(
      @PathVariable UUID trackingId, @RequestParam boolean estInscritDefinitif) {
    try {
      InscriptionAnnuelleResponse response =
          inscriptionService.updateDefinitif(trackingId, estInscritDefinitif);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @PostMapping("/{trackingId}/synchroniser")
  @Operation(summary = "Synchroniser avec l'université", description = "Lance la vérification d'éligibilité via l'API de l'université")
  public ResponseEntity<?> synchroniser(@PathVariable UUID trackingId) {
    try {
      return ResponseEntity.ok(inscriptionService.synchroniserAvecUniversite(trackingId));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SYNC_FAILED", "message", e.getMessage()));
    }
  }

  @DeleteMapping("/{trackingId}")
  @Operation(
      summary = "Supprimer une inscription",
      description = "Supprime une inscription par son ID")
  @ApiResponse(responseCode = "204", description = "Inscription supprimée avec succès")
  @ApiResponse(responseCode = "404", description = "Inscription non trouvée")
  public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
    try {
      inscriptionService.delete(trackingId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/student/{studentTrackingId}")
  @Operation(
      summary = "Récupérer les inscriptions d'un étudiant",
      description = "Récupère toutes les inscriptions d'un étudiant spécifique")
  @ApiResponse(responseCode = "200", description = "Inscriptions trouvées")
  @ApiResponse(responseCode = "404", description = "Aucune inscription trouvée")
  public ResponseEntity<?> findByStudentTrackingId(
      @PathVariable UUID studentTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = inscriptionService.findByStudentTrackingId(studentTrackingId, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                Map.of(
                    "error",
                    "NOT_FOUND",
                    "message",
                    "Aucune inscription trouvée pour cet étudiant"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/student/{studentTrackingId}/annee/{anneeAcademique}")
  @Operation(
      summary = "Récupérer l'inscription d'un étudiant pour une année",
      description = "Récupère l'inscription d'un étudiant pour une année académique spécifique")
  @ApiResponse(responseCode = "200", description = "Inscription trouvée")
  @ApiResponse(responseCode = "404", description = "Inscription non trouvée")
  public ResponseEntity<?> findByStudentAndAnnee(
      @PathVariable UUID studentTrackingId, @PathVariable String anneeAcademique) {
    try {
      return inscriptionService
          .findByStudentAndAnnee(studentTrackingId, anneeAcademique)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Inscription non trouvée")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping
  @Operation(
      summary = "Récupérer toutes les inscriptions",
      description = "Récupère la liste de toutes les inscriptions")
  @ApiResponse(responseCode = "200", description = "Inscriptions récupérées avec succès")
  @ApiResponse(responseCode = "404", description = "Aucune inscription trouvée")
  public ResponseEntity<?> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = inscriptionService.findAll(pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucune inscription trouvée"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/universite/{universiteTrackingId}")
  @Operation(summary = "Récupérer les inscriptions d'une université")
  public ResponseEntity<Page<InscriptionAnnuelleResponse>> findByUniversite(
      @PathVariable UUID universiteTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(
        inscriptionService.findByUniversiteTrackingId(universiteTrackingId, pageable));
  }
}
