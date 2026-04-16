package com.backend.gns.application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.backend.gns.application.dtos.requests.AdminRequest;
import com.backend.gns.application.dtos.responses.AdminResponse;
import com.backend.gns.domain.services.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admins")
@Tag(name = "ADMIN", description = "Gestion des administrateurs")
@CrossOrigin("*")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    @Operation(summary = "Créer un administrateur", description = "Crée un nouvel administrateur")
    @ApiResponse(responseCode = "201", description = "Administrateur créé avec succès")
    public ResponseEntity<?> create(@RequestBody AdminRequest request) {
        try {
            AdminResponse response = adminService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
        }
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un administrateur", description = "Récupère un administrateur par son ID")
    @ApiResponse(responseCode = "200", description = "Administrateur trouvé")
    @ApiResponse(responseCode = "404", description = "Administrateur non trouvé")
    public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
        try {
            return adminService.findByTrackingId(trackingId)
                    .map(response -> ResponseEntity.ok((Object) response))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "NOT_FOUND", "message", "Administrateur non trouvé")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
        }
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour un administrateur", description = "Mettre à jour les informations d'un administrateur")
    @ApiResponse(responseCode = "200", description = "Administrateur mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Administrateur non trouvé")
    public ResponseEntity<?> update(@PathVariable UUID trackingId, @RequestBody AdminRequest request) {
        try {
            AdminResponse response = adminService.update(trackingId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer un administrateur", description = "Supprime un administrateur par son ID")
    @ApiResponse(responseCode = "204", description = "Administrateur supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Administrateur non trouvé")
    public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
        try {
            adminService.delete(trackingId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les administrateurs", description = "Récupère la liste de tous les administrateurs")
    @ApiResponse(responseCode = "200", description = "Administrateurs récupérés avec succès")
    @ApiResponse(responseCode = "404", description = "Aucun administrateur trouvé")
    public ResponseEntity<?> findAll() {
        try {
            List<AdminResponse> responses = adminService.findAll();
            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "NOT_FOUND", "message", "Aucun administrateur trouvé"));
            }
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
        }
    }
}
