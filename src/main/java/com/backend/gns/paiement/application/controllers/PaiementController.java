package com.backend.gns.paiement.application.controllers;

import com.backend.gns.paiement.application.dtos.requests.PaiementRequest;
import com.backend.gns.paiement.application.dtos.responses.PaiementResponse;
import com.backend.gns.paiement.domain.enums.PaiementStatut;
import com.backend.gns.paiement.domain.enums.PaiementType;
import com.backend.gns.paiement.domain.services.PaiementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paiements")
@Tag(name = "PAIEMENT", description = "Gestion des paiements")
public class PaiementController {

  private final PaiementService paiementService;

  public PaiementController(PaiementService paiementService) {
    this.paiementService = paiementService;
  }

  @PostMapping
  @Operation(summary = "Créer un paiement", description = "Crée un nouveau paiement")
  @ApiResponse(responseCode = "201", description = "Paiement créé avec succès")
  public ResponseEntity<?> create(@RequestBody PaiementRequest request) {
    try {
      PaiementResponse response = paiementService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/{trackingId}")
  @Operation(summary = "Récupérer un paiement", description = "Récupère un paiement par son ID")
  @ApiResponse(responseCode = "200", description = "Paiement trouvé")
  @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
  public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
    try {
      return paiementService
          .findByTrackingId(trackingId)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Paiement non trouvé")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PutMapping("/{trackingId}")
  @Operation(
      summary = "Mettre à jour un paiement",
      description = "Mettre à jour les informations d'un paiement")
  @ApiResponse(responseCode = "200", description = "Paiement mis à jour avec succès")
  @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
  public ResponseEntity<?> update(
      @PathVariable UUID trackingId, @RequestBody PaiementRequest request) {
    try {
      PaiementResponse response = paiementService.update(trackingId, request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer un paiement", description = "Supprime un paiement par son ID")
  @ApiResponse(responseCode = "204", description = "Paiement supprimé avec succès")
  @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
  public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
    try {
      paiementService.delete(trackingId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/statut/{statutPaiement}")
  @Operation(
      summary = "Récupérer les paiements par statut",
      description = "Récupère tous les paiements avec un statut donné")
  @ApiResponse(responseCode = "200", description = "Paiements trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun paiement trouvé")
  public ResponseEntity<?> findByStatutPaiement(
      @PathVariable PaiementStatut statutPaiement,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = paiementService.findByStatutPaiement(statutPaiement, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun paiement avec ce statut"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/type/{typePaiement}")
  @Operation(
      summary = "Récupérer les paiements par type",
      description = "Récupère tous les paiements d'un type donné")
  @ApiResponse(responseCode = "200", description = "Paiements trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun paiement trouvé")
  public ResponseEntity<?> findByTypePaiement(
      @PathVariable PaiementType typePaiement,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = paiementService.findByTypePaiement(typePaiement, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun paiement avec ce type"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/commande/{commandeTrackingId}")
  @Operation(
      summary = "Récupérer les paiements d'une commande",
      description = "Récupère tous les paiements d'une commande spécifique")
  @ApiResponse(responseCode = "200", description = "Paiements trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun paiement trouvé")
  public ResponseEntity<?> findByCommandeTrackingId(
      @PathVariable UUID commandeTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = paiementService.findByCommandeTrackingId(commandeTrackingId, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun paiement pour cette commande"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/wallet/{walletTrackingId}")
  @Operation(
      summary = "Récupérer les paiements d'un portefeuille",
      description = "Récupère tous les paiements d'un portefeuille spécifique")
  @ApiResponse(responseCode = "200", description = "Paiements trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun paiement trouvé")
  public ResponseEntity<?> findByWalletTrackingId(
      @PathVariable UUID walletTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = paiementService.findByWalletTrackingId(walletTrackingId, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun paiement pour ce portefeuille"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping
  @Operation(
      summary = "Récupérer tous les paiements",
      description = "Récupère la liste de tous les paiements")
  @ApiResponse(responseCode = "200", description = "Paiements récupérés avec succès")
  @ApiResponse(responseCode = "404", description = "Aucun paiement trouvé")
  public ResponseEntity<?> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = paiementService.findAll(pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun paiement trouvé"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/universite/{universiteTrackingId}")
  @Operation(summary = "Récupérer les paiements d'une université")
  public ResponseEntity<Page<PaiementResponse>> findByUniversite(
      @PathVariable UUID universiteTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(paiementService.findByUniversiteTrackingId(universiteTrackingId, pageable));
  }
}
