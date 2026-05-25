package com.backend.gns.admin.application.controllers;

import com.backend.gns.admin.application.dtos.requests.UniversityAdminRequest;
import com.backend.gns.admin.application.dtos.responses.UniversityAdminResponse;
import com.backend.gns.admin.domain.services.UniversityAdminService;
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
@RequestMapping("/admin-university")
@Tag(name = "ADMIN_UNIVERSITY", description = "Gestion des administrateurs universitaires")
@AllArgsConstructor
public class UniversityAdminController {

    private final UniversityAdminService universityAdminService;

    @PostMapping
    @Operation(summary = "Créer un administrateur UL", description = "Crée un nouvel administrateur UL")
    @ApiResponse(responseCode = "201", description = "Administrateur UL créé avec succès")
    public ResponseEntity<UniversityAdminResponse> create(@RequestBody UniversityAdminRequest request) {
        UniversityAdminResponse response = universityAdminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un administrateur UL", description = "Récupère un administrateur UL par son ID")
    @ApiResponse(responseCode = "200", description = "Administrateur UL trouvé")
    @ApiResponse(responseCode = "404", description = "Administrateur UL non trouvé")
    public ResponseEntity<UniversityAdminResponse> findByTrackingId(@PathVariable UUID trackingId) {
        return universityAdminService.findByTrackingId(trackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour un administrateur UL", description = "Mettre à jour les informations d'un administrateur UL")
    @ApiResponse(responseCode = "200", description = "Administrateur UL mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Administrateur UL non trouvé")
    public ResponseEntity<UniversityAdminResponse> update(@PathVariable UUID trackingId, @RequestBody UniversityAdminRequest request) {
        UniversityAdminResponse response = universityAdminService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer un administrateur UL", description = "Supprime un administrateur UL par son ID")
    @ApiResponse(responseCode = "204", description = "Administrateur UL supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Administrateur UL non trouvé")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        universityAdminService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les administrateurs UL", description = "Récupère la liste de tous les administrateurs UL")
    @ApiResponse(responseCode = "200", description = "Administrateurs UL récupérés avec succès")
    public ResponseEntity<Page<UniversityAdminResponse>> findAll(Pageable pageable) {
        Page<UniversityAdminResponse> responses = universityAdminService.findAll(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/universite/{universiteTrackingId}")
    @Operation(summary = "Récupérer les administrateurs par université")
    public ResponseEntity<Page<UniversityAdminResponse>> findByUniversite(@PathVariable UUID universiteTrackingId, Pageable pageable) {
        return ResponseEntity.ok(universityAdminService.findByUniversiteTrackingId(universiteTrackingId, pageable));
    }
}
