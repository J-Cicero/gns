package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.CardRequest;
import com.backend.gns.student.application.dtos.responses.CardResponse;
import com.backend.gns.student.domain.enums.CardStatut;
import com.backend.gns.student.domain.services.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cards")
@Tag(name = "CARD", description = "Gestion des cartes physiques PVC")
public class CardController {

  private final CardService cardService;

  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

  @PostMapping
  @Operation(
      summary = "Créer une carte",
      description = "Crée une nouvelle carte physique pour un étudiant")
  @ApiResponse(responseCode = "201", description = "Carte créée avec succès")
  @ApiResponse(
      responseCode = "400",
      description = "Données invalides ou étudiant a déjà une carte active")
  public ResponseEntity<?> create(@RequestBody CardRequest request) {
    try {
      CardResponse response = cardService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "DUPLICATE_ACTIVE_CARD", "message", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/{trackingId}")
  @Operation(summary = "Récupérer une carte", description = "Récupère une carte par son trackingId")
  @ApiResponse(responseCode = "200", description = "Carte trouvée")
  @ApiResponse(responseCode = "404", description = "Carte non trouvée")
  public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
    try {
      return cardService
          .findByTrackingId(trackingId)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Carte non trouvée")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PutMapping("/{trackingId}")
  @Operation(
      summary = "Mettre à jour une carte",
      description = "Mettre à jour les informations d'une carte")
  @ApiResponse(responseCode = "200", description = "Carte mise à jour avec succès")
  @ApiResponse(responseCode = "404", description = "Carte non trouvée")
  public ResponseEntity<?> update(@PathVariable UUID trackingId, @RequestBody CardRequest request) {
    try {
      CardResponse response = cardService.update(trackingId, request);
      return ResponseEntity.ok(response);
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "DUPLICATE_ACTIVE_CARD", "message", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer une carte", description = "Supprime une carte par son trackingId")
  @ApiResponse(responseCode = "204", description = "Carte supprimée avec succès")
  @ApiResponse(responseCode = "404", description = "Carte non trouvée")
  public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
    try {
      cardService.delete(trackingId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/student/{studentTrackingId}")
  @Operation(
      summary = "Récupérer les cartes d'un étudiant",
      description = "Récupère toutes les cartes (actives ou non) d'un étudiant spécifique")
  @ApiResponse(responseCode = "200", description = "Cartes trouvées")
  @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
  public ResponseEntity<?> findByStudentTrackingId(
      @PathVariable UUID studentTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = cardService.findByStudentTrackingId(studentTrackingId, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                Map.of("error", "NOT_FOUND", "message", "Aucune carte trouvée pour cet étudiant"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/statut/{cardStatus}")
  @Operation(
      summary = "Récupérer les cartes par statut",
      description = "Récupère toutes les cartes avec un statut donné (ACTIVE, PERDUE, REMPLACEE)")
  @ApiResponse(responseCode = "200", description = "Cartes trouvées")
  @ApiResponse(responseCode = "404", description = "Aucune carte trouvée")
  public ResponseEntity<?> findByCardStatus(
      @PathVariable CardStatut cardStatus,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = cardService.findByCardStatus(cardStatus, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucune carte trouvée avec ce statut"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PostMapping("/{trackingId}/declare-lost")
  @Operation(
      summary = "Déclarer une carte comme perdue",
      description =
          "Marque une carte comme PERDUE et permet la création d'une nouvelle carte active")
  @ApiResponse(responseCode = "200", description = "Carte marquée comme perdue")
  @ApiResponse(responseCode = "404", description = "Carte non trouvée")
  public ResponseEntity<?> declareCardLost(@PathVariable UUID trackingId) {
    try {
      CardResponse response = cardService.declareCardLost(trackingId);
      return ResponseEntity.ok(
          Map.of("success", true, "message", "Carte déclarée comme perdue", "card", response));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "DECLARE_LOST_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping
  @Operation(
      summary = "Récupérer toutes les cartes",
      description = "Récupère la liste paginée de toutes les cartes")
  @ApiResponse(responseCode = "200", description = "Cartes récupérées avec succès")
  @ApiResponse(responseCode = "404", description = "Aucune carte trouvée")
  public ResponseEntity<?> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = cardService.findAll(pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucune carte trouvée"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }
}
