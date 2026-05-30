package com.backend.gns.wallet.application.controllers;

import com.backend.gns.wallet.application.dtos.requests.VersementRequest;
import com.backend.gns.wallet.application.dtos.responses.VersementResponse;
import com.backend.gns.wallet.domain.enums.VersementStatut;
import com.backend.gns.wallet.domain.enums.VersementType;
import com.backend.gns.wallet.domain.services.VersementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/versements")
@Tag(name = "VERSEMENT", description = "Gestion des versements")
public class VersementController {

  private final VersementService versementService;

  public VersementController(VersementService versementService) {
    this.versementService = versementService;
  }

  @PostMapping
  @Operation(
      summary = "Créer un versement individuel",
      description = "Crée un nouveau versement manuel")
  @ApiResponse(responseCode = "201", description = "Versement créé avec succès")
  public ResponseEntity<?> create(@RequestBody VersementRequest request) {
    try {
      VersementResponse response = versementService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
    }
  }

  @PostMapping("/masse/etudiants")
  @Operation(
      summary = "Versement en masse étudiants",
      description =
          "Effectue les versements de bourse pour tous les étudiants éligibles d'une année")
  public ResponseEntity<?> effectuerVersementMasseEtudiants(
      @RequestParam UUID scolariteYearTrackingId, @RequestParam BigDecimal montantFixe) {
    try {
      versementService.effectuerVersementMasseEtudiants(scolariteYearTrackingId, montantFixe);
      return ResponseEntity.ok(
          Map.of("success", true, "message", "Versements en masse lancés avec succès"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "MASS_DISBURSEMENT_FAILED", "message", e.getMessage()));
    }
  }

  @PostMapping("/masse/boutiques")
  @Operation(
      summary = "Recharge quota boutiques en masse",
      description = "Recharge le quota des boutiques dont le solde est bas")
  public ResponseEntity<?> effectuerVersementMasseBoutiques(
      @RequestParam BigDecimal seuil, @RequestParam BigDecimal montantQuota) {
    try {
      versementService.effectuerVersementMasseBoutiques(seuil, montantQuota);
      return ResponseEntity.ok(
          Map.of("success", true, "message", "Recharge en masse des boutiques lancée avec succès"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "MASS_RECHARGE_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/{trackingId}")
  @Operation(summary = "Récupérer un versement", description = "Récupère un versement par son ID")
  @ApiResponse(responseCode = "200", description = "Versement trouvé")
  @ApiResponse(responseCode = "404", description = "Versement non trouvé")
  public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
    try {
      return versementService
          .findByTrackingId(trackingId)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Versement non trouvé")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PutMapping("/{trackingId}")
  @Operation(
      summary = "Mettre à jour un versement",
      description = "Mettre à jour les informations d'un versement")
  public ResponseEntity<?> update(
      @PathVariable UUID trackingId, @RequestBody VersementRequest request) {
    try {
      VersementResponse response = versementService.update(trackingId, request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer un versement", description = "Supprime un versement par son ID")
  public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
    try {
      versementService.delete(trackingId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/statut/{statut}")
  @Operation(
      summary = "Récupérer les versements par statut",
      description = "Récupère tous les versements avec un statut donné")
  public ResponseEntity<?> findByStatut(
      @PathVariable VersementStatut statut,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = versementService.findByStatut(statut, pageable);
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/type/{typeVersement}")
  @Operation(
      summary = "Récupérer les versements par type",
      description = "Récupère tous les versements d'un type donné")
  public ResponseEntity<?> findByTypeVersement(
      @PathVariable VersementType typeVersement,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = versementService.findByTypeVersement(typeVersement, pageable);
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/wallet/{walletTrackingId}")
  @Operation(
      summary = "Récupérer les versements d'un portefeuille",
      description = "Récupère tous les versements d'un portefeuille spécifique")
  public ResponseEntity<?> findByWalletTrackingId(
      @PathVariable UUID walletTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = versementService.findByWalletTrackingId(walletTrackingId, pageable);
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping
  @Operation(
      summary = "Récupérer tous les versements",
      description = "Récupère la liste de tous les versements")
  public ResponseEntity<?> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = versementService.findAll(pageable);
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }
}
