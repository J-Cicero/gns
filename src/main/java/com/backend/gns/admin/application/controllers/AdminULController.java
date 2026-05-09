package com.backend.gns.application.controllers;

import com.backend.gns.application.dtos.requests.AdminULRequest;
import com.backend.gns.application.dtos.responses.AdminULResponse;
import com.backend.gns.domain.services.AdminULService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin-ul")
@Tag(name = "ADMIN_UL", description = "Gestion des administrateurs UL")
@CrossOrigin("*")
@AllArgsConstructor
public class AdminULController {

    private final AdminULService adminULService;

    @PostMapping
    @Operation(summary = "Créer un administrateur UL", description = "Crée un nouvel administrateur UL")
    @ApiResponse(responseCode = "201", description = "Administrateur UL créé avec succès")
    public ResponseEntity<AdminULResponse> create(@RequestBody AdminULRequest request) {
        AdminULResponse response = adminULService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un administrateur UL", description = "Récupère un administrateur UL par son ID")
    @ApiResponse(responseCode = "200", description = "Administrateur UL trouvé")
    @ApiResponse(responseCode = "404", description = "Administrateur UL non trouvé")
    public ResponseEntity<AdminULResponse> findByTrackingId(@PathVariable UUID trackingId) {
        return adminULService.findByTrackingId(trackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour un administrateur UL", description = "Mettre à jour les informations d'un administrateur UL")
    @ApiResponse(responseCode = "200", description = "Administrateur UL mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Administrateur UL non trouvé")
    public ResponseEntity<AdminULResponse> update(@PathVariable UUID trackingId, @RequestBody AdminULRequest request) {
        AdminULResponse response = adminULService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer un administrateur UL", description = "Supprime un administrateur UL par son ID")
    @ApiResponse(responseCode = "204", description = "Administrateur UL supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Administrateur UL non trouvé")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        adminULService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les administrateurs UL", description = "Récupère la liste de tous les administrateurs UL")
    @ApiResponse(responseCode = "200", description = "Administrateurs UL récupérés avec succès")
    public ResponseEntity<Page<AdminULResponse>> findAll(Pageable pageable) {
        Page<AdminULResponse> responses = adminULService.findAll(pageable);
        return ResponseEntity.ok(responses);
    }
}
