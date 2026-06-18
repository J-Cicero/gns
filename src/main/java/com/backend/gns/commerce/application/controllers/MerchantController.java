package com.backend.gns.commerce.application.controllers;

import com.backend.gns.commerce.application.dtos.requests.MerchantRequest;
import com.backend.gns.commerce.application.dtos.responses.MerchantResponse;
import com.backend.gns.commerce.domain.services.MerchantService;
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
@RequestMapping("/merchants")
@Tag(name = "MERCHANT", description = "Gestion des marchands")
public class MerchantController {

  private final MerchantService merchantService;

  public MerchantController(MerchantService merchantService) {
    this.merchantService = merchantService;
  }

  @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Créer un marchand avec RIB", description = "Crée un nouveau marchand et sa boutique")
  @ApiResponse(responseCode = "201", description = "Marchand créé avec succès")
  public ResponseEntity<?> create(
      @RequestPart("merchant") MerchantRequest request,
      @RequestPart(value = "rib", required = false) org.springframework.web.multipart.MultipartFile rib) {
    try {
      MerchantResponse response = merchantService.create(request, rib);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/{trackingId}")
  @Operation(summary = "Récupérer un marchand", description = "Récupère un marchand par son ID")
  @ApiResponse(responseCode = "200", description = "Marchand trouvé")
  @ApiResponse(responseCode = "404", description = "Marchand non trouvé")
  public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
    try {
      return merchantService
          .findByTrackingId(trackingId)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Marchand non trouvé")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PutMapping("/{trackingId}")
  @Operation(
      summary = "Mettre à jour un marchand",
      description = "Mettre à jour les informations d'un marchand")
  @ApiResponse(responseCode = "200", description = "Marchand mis à jour avec succès")
  @ApiResponse(responseCode = "404", description = "Marchand non trouvé")
  public ResponseEntity<?> update(
      @PathVariable UUID trackingId, @RequestBody MerchantRequest request) {
    try {
      MerchantResponse response = merchantService.update(trackingId, request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer un marchand", description = "Supprime un marchand par son ID")
  @ApiResponse(responseCode = "204", description = "Marchand supprimé avec succès")
  @ApiResponse(responseCode = "404", description = "Marchand non trouvé")
  public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
    try {
      merchantService.delete(trackingId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping
  @Operation(
      summary = "Récupérer tous les marchands",
      description = "Récupère la liste de tous les marchands")
  @ApiResponse(responseCode = "200", description = "Marchands récupérés avec succès")
  @ApiResponse(responseCode = "404", description = "Aucun marchand trouvé")
  public ResponseEntity<?> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = merchantService.findAll(pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun marchand trouvé"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }
}
