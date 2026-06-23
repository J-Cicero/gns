package com.backend.gns.wallet.application.controllers;

import lombok.extern.slf4j.Slf4j;

import com.backend.gns.wallet.application.dtos.requests.VersementRequest;
import com.backend.gns.wallet.application.dtos.responses.VersementResponse;
import com.backend.gns.wallet.domain.enums.VersementStatut;
import com.backend.gns.wallet.domain.enums.VersementType;
import com.backend.gns.wallet.domain.services.VersementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
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
      log.error("Erreur lors de la création du versement: {}", e.getMessage(), e);
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
      @RequestParam UUID scolariteYearTrackingId, 
      @RequestParam(required = false) com.backend.gns.wallet.domain.enums.WalletStatus statutCible,
      @RequestParam BigDecimal montantFixe) {
    try {
      versementService.effectuerVersementMasseEtudiants(scolariteYearTrackingId, statutCible, montantFixe);
      return ResponseEntity.ok(
          Map.of("success", true, "message", "Versements en masse lancés avec succès"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "MASS_DISBURSEMENT_FAILED", "message", e.getMessage()));
    }
  }

  @PostMapping("/masse/etudiants/specifiques")
  @Operation(
      summary = "Versement en masse pour des étudiants spécifiques",
      description = "Effectue les versements de bourse pour une liste d'étudiants (via wallet tracking IDs)")
  public ResponseEntity<?> effectuerVersementMasseEtudiantsSpecifiques(
      @RequestBody Map<String, Object> payload) {
    try {
      java.util.List<String> trackingIdsStr = (java.util.List<String>) payload.get("walletTrackingIds");
      java.util.List<UUID> trackingIds = trackingIdsStr.stream().map(UUID::fromString).collect(java.util.stream.Collectors.toList());
      
      Object montantFixeObj = payload.get("montantFixe");
      BigDecimal montantFixe = null;
      if (montantFixeObj != null) {
          montantFixe = new BigDecimal(montantFixeObj.toString());
      }
      
      versementService.effectuerVersementMasseEtudiantsSpecifiques(trackingIds, montantFixe);
      return ResponseEntity.ok(
          Map.of("success", true, "message", "Versements sélectifs lancés avec succès"));
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
      @RequestParam BigDecimal seuil, 
      @RequestParam(required = false) com.backend.gns.wallet.domain.enums.WalletStatus statutCible,
      @RequestParam BigDecimal montantQuota) {
    try {
      versementService.effectuerVersementMasseBoutiques(seuil, statutCible, montantQuota);
      return ResponseEntity.ok(
          Map.of("success", true, "message", "Recharge en masse des boutiques lancée avec succès"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "MASS_RECHARGE_FAILED", "message", e.getMessage()));
    }
  }

  @PostMapping("/masse/reset/etudiants")
  @Operation(
      summary = "Remise à zéro bourses étudiants",
      description = "Remet à zéro tous les portefeuilles des étudiants de l'année scolaire")
  public ResponseEntity<?> remiseAZeroMasseEtudiants(@RequestParam UUID scolariteYearTrackingId) {
    try {
      versementService.remiseAZeroMasseEtudiants(scolariteYearTrackingId);
      return ResponseEntity.ok(
          Map.of(
              "success", true, "message", "Remise à zéro lancée avec succès pour les étudiants"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "MASS_RESET_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/masse/preview/etudiants")
  @Operation(
      summary = "Prévisualiser les cibles pour étudiants",
      description = "Retourne la liste des étudiants concernés")
  public ResponseEntity<?> previewMasseEtudiants(@RequestParam UUID scolariteYearTrackingId) {
    try {
      java.util.List<String> names =
          versementService.previewMasseEtudiants(scolariteYearTrackingId);
      return ResponseEntity.ok(Map.of("count", names.size(), "names", names));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "PREVIEW_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/masse/preview/boutiques")
  @Operation(
      summary = "Prévisualiser les cibles pour boutiques",
      description = "Retourne la liste des boutiques concernées")
  public ResponseEntity<?> previewMasseBoutiques(@RequestParam BigDecimal seuil) {
    try {
      java.util.List<String> names = versementService.previewMasseBoutiques(seuil);
      return ResponseEntity.ok(Map.of("count", names.size(), "names", names));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "PREVIEW_FAILED", "message", e.getMessage()));
    }
  }

  @PostMapping("/masse/reset/boutiques")
  @Operation(
      summary = "Remise à zéro quotas boutiques",
      description = "Remet à zéro tous les quotas de toutes les boutiques")
  public ResponseEntity<?> remiseAZeroMasseBoutiques() {
    try {
      versementService.remiseAZeroMasseBoutiques();
      return ResponseEntity.ok(
          Map.of(
              "success", true, "message", "Remise à zéro lancée avec succès pour les boutiques"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "MASS_RESET_FAILED", "message", e.getMessage()));
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
