package com.backend.gns.paiement.application.controllers;

import com.backend.gns.paiement.application.dtos.requests.CommandeLigneRequest;
import com.backend.gns.paiement.application.dtos.responses.CommandeLigneResponse;
import com.backend.gns.paiement.domain.services.CommandeLigneService;
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
@RequestMapping("/commande-lignes")
@Tag(name = "COMMANDE_LIGNE", description = "Gestion des lignes de commande")
public class CommandeLigneController {

  private final CommandeLigneService commandeLigneService;

  public CommandeLigneController(CommandeLigneService commandeLigneService) {
    this.commandeLigneService = commandeLigneService;
  }

  @PostMapping
  @Operation(
      summary = "Créer une ligne de commande",
      description = "Crée une nouvelle ligne de commande")
  @ApiResponse(responseCode = "201", description = "Ligne de commande créée avec succès")
  public ResponseEntity<?> create(@RequestBody CommandeLigneRequest request) {
    try {
      CommandeLigneResponse response = commandeLigneService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/{trackingId}")
  @Operation(
      summary = "Récupérer une ligne de commande",
      description = "Récupère une ligne de commande par son ID")
  @ApiResponse(responseCode = "200", description = "Ligne de commande trouvée")
  @ApiResponse(responseCode = "404", description = "Ligne de commande non trouvée")
  public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
    try {
      return commandeLigneService
          .findByTrackingId(trackingId)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Ligne de commande non trouvée")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PutMapping("/{trackingId}")
  @Operation(
      summary = "Mettre à jour une ligne de commande",
      description = "Mettre à jour les informations d'une ligne de commande")
  @ApiResponse(responseCode = "200", description = "Ligne de commande mise à jour avec succès")
  @ApiResponse(responseCode = "404", description = "Ligne de commande non trouvée")
  public ResponseEntity<?> update(
      @PathVariable UUID trackingId, @RequestBody CommandeLigneRequest request) {
    try {
      CommandeLigneResponse response = commandeLigneService.update(trackingId, request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @DeleteMapping("/{trackingId}")
  @Operation(
      summary = "Supprimer une ligne de commande",
      description = "Supprime une ligne de commande par son ID")
  @ApiResponse(responseCode = "204", description = "Ligne de commande supprimée avec succès")
  @ApiResponse(responseCode = "404", description = "Ligne de commande non trouvée")
  public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
    try {
      commandeLigneService.delete(trackingId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/commande/{commandeTrackingId}")
  @Operation(
      summary = "Récupérer les lignes d'une commande",
      description = "Récupère toutes les lignes d'une commande spécifique")
  @ApiResponse(responseCode = "200", description = "Lignes de commande trouvées")
  @ApiResponse(responseCode = "404", description = "Aucune ligne de commande trouvée")
  public ResponseEntity<?> findByCommandeTrackingId(
      @PathVariable UUID commandeTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = commandeLigneService.findByCommandeTrackingId(commandeTrackingId, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucune ligne pour cette commande"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/product/{productTrackingId}")
  @Operation(
      summary = "Récupérer les lignes d'un produit",
      description = "Récupère toutes les lignes contenant un produit spécifique")
  @ApiResponse(responseCode = "200", description = "Lignes de commande trouvées")
  @ApiResponse(responseCode = "404", description = "Aucune ligne de commande trouvée")
  public ResponseEntity<?> findByProductTrackingId(
      @PathVariable UUID productTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = commandeLigneService.findByProductTrackingId(productTrackingId, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucune ligne pour ce produit"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping
  @Operation(
      summary = "Récupérer toutes les lignes de commande",
      description = "Récupère la liste de toutes les lignes de commande")
  @ApiResponse(responseCode = "200", description = "Lignes de commande récupérées avec succès")
  @ApiResponse(responseCode = "404", description = "Aucune ligne de commande trouvée")
  public ResponseEntity<?> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = commandeLigneService.findAll(pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucune ligne de commande trouvée"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }
}
