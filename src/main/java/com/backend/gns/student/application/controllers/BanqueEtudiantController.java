package com.backend.gns.application.controllers;

import com.backend.gns.application.dtos.requests.BanqueEtudiantRequest;
import com.backend.gns.application.dtos.responses.BanqueEtudiantResponse;
import com.backend.gns.domain.services.BanqueEtudiantService;
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
@RequestMapping("/api/banque-etudiants")
@Tag(name = "BANQUE_ETUDIANT", description = "Gestion des informations bancaires des étudiants")
@CrossOrigin("*")
@AllArgsConstructor
public class BanqueEtudiantController {

    private final BanqueEtudiantService banqueEtudiantService;

    @PostMapping
    @Operation(summary = "Créer une information bancaire", description = "Crée une nouvelle information bancaire pour un étudiant")
    @ApiResponse(responseCode = "201", description = "Information bancaire créée avec succès")
    public ResponseEntity<BanqueEtudiantResponse> create(@RequestBody BanqueEtudiantRequest request) {
        BanqueEtudiantResponse response = banqueEtudiantService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer une information bancaire", description = "Récupère une information bancaire par son ID")
    @ApiResponse(responseCode = "200", description = "Information bancaire trouvée")
    @ApiResponse(responseCode = "404", description = "Information bancaire non trouvée")
    public ResponseEntity<BanqueEtudiantResponse> findByTrackingId(@PathVariable UUID trackingId) {
        return banqueEtudiantService.findByTrackingId(trackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour une information bancaire", description = "Mettre à jour les informations d'une information bancaire")
    @ApiResponse(responseCode = "200", description = "Information bancaire mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Information bancaire non trouvée")
    public ResponseEntity<BanqueEtudiantResponse> update(@PathVariable UUID trackingId, @RequestBody BanqueEtudiantRequest request) {
        BanqueEtudiantResponse response = banqueEtudiantService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer une information bancaire", description = "Supprime une information bancaire par son ID")
    @ApiResponse(responseCode = "204", description = "Information bancaire supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Information bancaire non trouvée")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        banqueEtudiantService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les informations bancaires", description = "Récupère la liste de toutes les informations bancaires")
    @ApiResponse(responseCode = "200", description = "Informations bancaires récupérées avec succès")
    public ResponseEntity<Page<BanqueEtudiantResponse>> findAll(Pageable pageable) {
        Page<BanqueEtudiantResponse> responses = banqueEtudiantService.findAll(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student/{studentTrackingId}")
    @Operation(summary = "Récupérer les informations bancaires d'un étudiant", description = "Récupère les informations bancaires d'un étudiant spécifique")
    @ApiResponse(responseCode = "200", description = "Informations bancaires trouvées")
    public ResponseEntity<Page<BanqueEtudiantResponse>> findByStudentTrackingId(@PathVariable UUID studentTrackingId, Pageable pageable) {
        Page<BanqueEtudiantResponse> responses = banqueEtudiantService.findByStudentTrackingId(studentTrackingId, pageable);
        return ResponseEntity.ok(responses);
    }
}
