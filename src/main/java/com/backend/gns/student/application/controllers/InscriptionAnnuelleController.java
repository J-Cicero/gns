package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.InscriptionAnnuelleRequest;
import com.backend.gns.student.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.services.InscriptionAnnuelleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscriptions")
@Tag(name = "INSCRIPTION_ANNUELLE", description = "Gestion des inscriptions annuelles")
@CrossOrigin("*")
public class InscriptionAnnuelleController {

  private final InscriptionAnnuelleService inscriptionService;

  public InscriptionAnnuelleController(InscriptionAnnuelleService inscriptionService) {
    this.inscriptionService = inscriptionService;
  }

  @PostMapping
  @Operation(summary = "Créer une inscription", description = "Crée une nouvelle inscription annuelle")
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
  @Operation(summary = "Récupérer une inscription", description = "Récupère une inscription par son ID")
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

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer une inscription", description = "Supprime une inscription par son ID")
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
                    "error", "NOT_FOUND", "message",
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

  @GetMapping("/statut/{statut}")
  @Operation(
      summary = "Récupérer les inscriptions par statut",
      description = "Récupère toutes les inscriptions avec un statut donné")
  @ApiResponse(responseCode = "200", description = "Inscriptions trouvées")
  @ApiResponse(responseCode = "404", description = "Aucune inscription trouvée")
  public ResponseEntity<?> findByStatut(
      @PathVariable StatutInscription statut,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = inscriptionService.findByStatut(statut, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                Map.of(
                    "error", "NOT_FOUND", "message",
                    "Aucune inscription avec ce statut"));
      }
      return ResponseEntity.ok(responses);
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
}
