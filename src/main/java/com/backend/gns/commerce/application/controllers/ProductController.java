package com.backend.gns.commerce.application.controllers;

import com.backend.gns.commerce.application.dtos.requests.ProductRequest;
import com.backend.gns.commerce.application.dtos.responses.ProductResponse;
import com.backend.gns.commerce.domain.services.ProductService;
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
@RequestMapping("/api/products")
@Tag(name = "PRODUCT", description = "Gestion des produits")
@CrossOrigin("*")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @Operation(summary = "Créer un produit", description = "Crée un nouveau produit")
  @ApiResponse(responseCode = "201", description = "Produit créé avec succès")
  public ResponseEntity<?> create(@RequestBody ProductRequest request) {
    try {
      ProductResponse response = productService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/{trackingId}")
  @Operation(summary = "Récupérer un produit", description = "Récupère un produit par son ID")
  @ApiResponse(responseCode = "200", description = "Produit trouvé")
  @ApiResponse(responseCode = "404", description = "Produit non trouvé")
  public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
    try {
      return productService
          .findByTrackingId(trackingId)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Produit non trouvé")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PutMapping("/{trackingId}")
  @Operation(
      summary = "Mettre à jour un produit",
      description = "Mettre à jour les informations d'un produit")
  @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès")
  @ApiResponse(responseCode = "404", description = "Produit non trouvé")
  public ResponseEntity<?> update(
      @PathVariable UUID trackingId, @RequestBody ProductRequest request) {
    try {
      ProductResponse response = productService.update(trackingId, request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer un produit", description = "Supprime un produit par son ID")
  @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès")
  @ApiResponse(responseCode = "404", description = "Produit non trouvé")
  public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
    try {
      productService.delete(trackingId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/boutique/{boutiqueTrackingId}")
  @Operation(
      summary = "Récupérer les produits d'une boutique",
      description = "Récupère tous les produits d'une boutique spécifique")
  @ApiResponse(responseCode = "200", description = "Produits trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun produit trouvé")
  public ResponseEntity<?> findByBoutiqueTrackingId(
      @PathVariable UUID boutiqueTrackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = productService.findByBoutiqueTrackingId(boutiqueTrackingId, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun produit pour cette boutique"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/disponible/{estDisponible}")
  @Operation(
      summary = "Récupérer les produits par disponibilité",
      description = "Récupère tous les produits disponibles ou non disponibles")
  @ApiResponse(responseCode = "200", description = "Produits trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun produit trouvé")
  public ResponseEntity<?> findByEstDisponible(
      @PathVariable Boolean estDisponible,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = productService.findByEstDisponible(estDisponible, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                Map.of("error", "NOT_FOUND", "message", "Aucun produit avec cette disponibilité"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping
  @Operation(
      summary = "Récupérer tous les produits",
      description = "Récupère la liste de tous les produits")
  @ApiResponse(responseCode = "200", description = "Produits récupérés avec succès")
  @ApiResponse(responseCode = "404", description = "Aucun produit trouvé")
  public ResponseEntity<?> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = productService.findAll(pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun produit trouvé"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }
}
