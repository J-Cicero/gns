package com.backend.gns.application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.backend.gns.application.dtos.requests.CommandeRequest;
import com.backend.gns.application.dtos.responses.CommandeResponse;
import com.backend.gns.domain.enums.CommandeStatut;
import com.backend.gns.domain.services.CommandeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/commandes")
@Tag(name = "COMMANDE", description = "Gestion des commandes")
@CrossOrigin("*")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @PostMapping
    @Operation(summary = "Créer une commande", description = "Crée une nouvelle commande")
    @ApiResponse(responseCode = "201", description = "Commande créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<?> create(@RequestBody CommandeRequest request) {
        try {
            CommandeResponse response = commandeService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
        }
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer une commande", description = "Récupère une commande par son ID")
    @ApiResponse(responseCode = "200", description = "Commande trouvée")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
        try {
            return commandeService.findByTrackingId(trackingId)
                    .map(response -> ResponseEntity.ok((Object) response))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "NOT_FOUND", "message", "Commande non trouvée")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
        }
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour une commande", description = "Mettre à jour les informations d'une commande")
    @ApiResponse(responseCode = "200", description = "Commande mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<?> update(@PathVariable UUID trackingId, @RequestBody CommandeRequest request) {
        try {
            CommandeResponse response = commandeService.update(trackingId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer une commande", description = "Supprime une commande par son ID")
    @ApiResponse(responseCode = "204", description = "Commande supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
        try {
            commandeService.delete(trackingId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
        }
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les commandes par statut", description = "Récupère toutes les commandes avec un statut donné")
    @ApiResponse(responseCode = "200", description = "Commandes trouvées")
    @ApiResponse(responseCode = "404", description = "Aucune commande trouvée")
    public ResponseEntity<?> findByStatut(@PathVariable CommandeStatut statut) {
        try {
            List<CommandeResponse> responses = commandeService.findByStatut(statut);
            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "NOT_FOUND", "message", "Aucune commande avec ce statut"));
            }
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les commandes", description = "Récupère la liste de toutes les commandes")
    @ApiResponse(responseCode = "200", description = "Commandes récupérées avec succès")
    @ApiResponse(responseCode = "404", description = "Aucune commande trouvée")
    public ResponseEntity<?> findAll() {
        try {
            List<CommandeResponse> responses = commandeService.findAll();
            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "NOT_FOUND", "message", "Aucune commande trouvée"));
            }
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
        }
    }
}
