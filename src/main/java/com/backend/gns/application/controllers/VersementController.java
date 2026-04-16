package com.backend.gns.application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.backend.gns.application.dtos.requests.VersementRequest;
import com.backend.gns.application.dtos.responses.VersementResponse;
import com.backend.gns.domain.enums.VersementStatut;
import com.backend.gns.domain.enums.VersementType;
import com.backend.gns.domain.services.VersementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/versements")
@Tag(name = "VERSEMENT", description = "Gestion des versements")
@CrossOrigin("*")
public class VersementController {

    private final VersementService versementService;

    public VersementController(VersementService versementService) {
        this.versementService = versementService;
    }

    @PostMapping
    @Operation(summary = "Créer un versement", description = "Crée un nouveau versement")
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

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un versement", description = "Récupère un versement par son ID")
    @ApiResponse(responseCode = "200", description = "Versement trouvé")
    @ApiResponse(responseCode = "404", description = "Versement non trouvé")
    public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
        try {
            return versementService.findByTrackingId(trackingId)
                    .map(response -> ResponseEntity.ok((Object) response))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "NOT_FOUND", "message", "Versement non trouvé")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
        }
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour un versement", description = "Mettre à jour les informations d'un versement")
    @ApiResponse(responseCode = "200", description = "Versement mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Versement non trouvé")
    public ResponseEntity<?> update(@PathVariable UUID trackingId, @RequestBody VersementRequest request) {
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
    @ApiResponse(responseCode = "204", description = "Versement supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Versement non trouvé")
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
    @Operation(summary = "Récupérer les versements par statut", description = "Récupère tous les versements avec un statut donné")
    @ApiResponse(responseCode = "200", description = "Versements trouvés")
    @ApiResponse(responseCode = "404", description = "Aucun versement trouvé")
    public ResponseEntity<?> findByStatut(@PathVariable VersementStatut statut) {
        try {
            List<VersementResponse> responses = versementService.findByStatut(statut);
            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "NOT_FOUND", "message", "Aucun versement avec ce statut"));
            }
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
        }
    }

    @GetMapping("/type/{typeVersement}")
    @Operation(summary = "Récupérer les versements par type", description = "Récupère tous les versements d'un type donné")
    @ApiResponse(responseCode = "200", description = "Versements trouvés")
    @ApiResponse(responseCode = "404", description = "Aucun versement trouvé")
    public ResponseEntity<?> findByTypeVersement(@PathVariable VersementType typeVersement) {
        try {
            List<VersementResponse> responses = versementService.findByTypeVersement(typeVersement);
            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "NOT_FOUND", "message", "Aucun versement avec ce type"));
            }
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
        }
    }

    @GetMapping("/wallet/{walletTrackingId}")
    @Operation(summary = "Récupérer les versements d'un portefeuille", description = "Récupère tous les versements d'un portefeuille spécifique")
    @ApiResponse(responseCode = "200", description = "Versements trouvés")
    @ApiResponse(responseCode = "404", description = "Aucun versement trouvé")
    public ResponseEntity<?> findByWalletTrackingId(@PathVariable UUID walletTrackingId) {
        try {
            List<VersementResponse> responses = versementService.findByWalletTrackingId(walletTrackingId);
            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "NOT_FOUND", "message", "Aucun versement pour ce portefeuille"));
            }
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les versements", description = "Récupère la liste de tous les versements")
    @ApiResponse(responseCode = "200", description = "Versements récupérés avec succès")
    @ApiResponse(responseCode = "404", description = "Aucun versement trouvé")
    public ResponseEntity<?> findAll() {
        try {
            List<VersementResponse> responses = versementService.findAll();
            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "NOT_FOUND", "message", "Aucun versement trouvé"));
            }
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
        }
    }
}
