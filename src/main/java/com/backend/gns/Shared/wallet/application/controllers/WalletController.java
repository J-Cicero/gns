package com.backend.gns.Shared.wallet.application.controllers;

import com.backend.gns.Shared.wallet.application.dtos.requests.WalletRequest;
import com.backend.gns.Shared.wallet.application.dtos.responses.WalletResponse;
import com.backend.gns.Shared.wallet.domain.enums.WalletType;
import com.backend.gns.Shared.wallet.domain.services.WalletService;
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

@RestController
@RequestMapping("/api/wallets")
@Tag(name = "WALLET", description = "Gestion des portefeuilles")
public class WalletController {

  private final WalletService walletService;

  public WalletController(WalletService walletService) {
    this.walletService = walletService;
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ETUDIANT', 'COMMERCANT', 'ADMIN_GNS', 'ADMIN_UL')")
  @Operation(summary = "Créer un portefeuille", description = "Crée un nouveau portefeuille")
  @ApiResponse(responseCode = "201", description = "Portefeuille créé avec succès")
  public ResponseEntity<?> create(@RequestBody WalletRequest request) {
      try {
          WalletResponse response = walletService.create(request);
          return ResponseEntity.status(HttpStatus.CREATED).body(response);
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
      }
  }

  @PutMapping("/{trackingId}")
  @PreAuthorize("hasAnyRole('ETUDIANT', 'COMMERCANT', 'ADMIN_GNS', 'ADMIN_UL')")
  @Operation(summary = "Mettre à jour un portefeuille", description = "Mettre à jour les informations d'un portefeuille")
  @ApiResponse(responseCode = "200", description = "Portefeuille mis à jour avec succès")
  @ApiResponse(responseCode = "404", description = "Portefeuille non trouvé")
  public ResponseEntity<?> update(@PathVariable UUID trackingId, @RequestBody WalletRequest request) {
      try {
          WalletResponse response = walletService.update(trackingId, request);
          return ResponseEntity.ok(response);
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
      }
  }

  @DeleteMapping("/{trackingId}")
  @PreAuthorize("hasAnyRole('ADMIN_GNS', 'ADMIN_UL')")
  @Operation(summary = "Supprimer un portefeuille", description = "Supprime un portefeuille par son ID")
  @ApiResponse(responseCode = "204", description = "Portefeuille supprimé avec succès")
  @ApiResponse(responseCode = "404", description = "Portefeuille non trouvé")
  public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
      try {
          walletService.delete(trackingId);
          return ResponseEntity.noContent().build();
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
      }
  }
  
  @GetMapping("/{trackingId}")
  @Operation(
      summary = "Récupérer un portefeuille",
      description = "Récupère un portefeuille par son ID")
  @ApiResponse(responseCode = "200", description = "Portefeuille trouvé")
  @ApiResponse(responseCode = "404", description = "Portefeuille non trouvé")
  public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
    try {
      return walletService
          .findByTrackingId(trackingId)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Portefeuille non trouvé")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/type/{typeWallet}")
  @Operation(
      summary = "Récupérer les portefeuilles par type",
      description = "Récupère tous les portefeuilles d'un type donné")
  @ApiResponse(responseCode = "200", description = "Portefeuilles trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun portefeuille trouvé")
  public ResponseEntity<?> findByTypeWallet(
      @PathVariable WalletType typeWallet,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = walletService.findByTypeWallet(typeWallet, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun portefeuille avec ce type"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping
  @Operation(
      summary = "Récupérer tous les portefeuilles",
      description = "Récupère la liste de tous les portefeuilles")
  @ApiResponse(responseCode = "200", description = "Portefeuilles récupérés avec succès")
  @ApiResponse(responseCode = "404", description = "Aucun portefeuille trouvé")
  public ResponseEntity<?> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = walletService.findAll(pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun portefeuille trouvé"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }
}
