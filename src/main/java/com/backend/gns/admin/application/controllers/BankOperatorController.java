package com.backend.gns.application.controllers;

import com.backend.gns.application.dtos.requests.BankOperatorRequest;
import com.backend.gns.application.dtos.responses.BankOperatorResponse;
import com.backend.gns.application.dtos.responses.StudentResponse;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.domain.services.BankOperatorService;
import com.backend.gns.domain.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank-operator")
@Tag(name = "BANK_OPERATOR", description = "Gestion des opérateurs bancaires")
@CrossOrigin("*")
@AllArgsConstructor
public class BankOperatorController {

    private final BankOperatorService bankOperatorService;
    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Créer un opérateur bancaire", description = "Crée un nouvel opérateur bancaire")
    @ApiResponse(responseCode = "201", description = "Opérateur bancaire créé avec succès")
    public ResponseEntity<BankOperatorResponse> create(@RequestBody BankOperatorRequest request) {
        BankOperatorResponse response = bankOperatorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un opérateur bancaire", description = "Récupère un opérateur bancaire par son ID")
    @ApiResponse(responseCode = "200", description = "Opérateur bancaire trouvé")
    @ApiResponse(responseCode = "404", description = "Opérateur bancaire non trouvé")
    public ResponseEntity<BankOperatorResponse> findByTrackingId(@PathVariable UUID trackingId) {
        return bankOperatorService.findByTrackingId(trackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour un opérateur bancaire", description = "Mettre à jour les informations d'un opérateur bancaire")
    @ApiResponse(responseCode = "200", description = "Opérateur bancaire mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Opérateur bancaire non trouvé")
    public ResponseEntity<BankOperatorResponse> update(@PathVariable UUID trackingId, @RequestBody BankOperatorRequest request) {
        BankOperatorResponse response = bankOperatorService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer un opérateur bancaire", description = "Supprime un opérateur bancaire par son ID")
    @ApiResponse(responseCode = "204", description = "Opérateur bancaire supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Opérateur bancaire non trouvé")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        bankOperatorService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les opérateurs bancaires", description = "Récupère la liste de tous les opérateurs bancaires")
    @ApiResponse(responseCode = "200", description = "Opérateurs bancaires récupérés avec succès")
    public ResponseEntity<Page<BankOperatorResponse>> findAll(Pageable pageable) {
        Page<BankOperatorResponse> responses = bankOperatorService.findAll(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/students/mandats")
    @Operation(
        summary = "Lister les étudiants en attente de validation de mandat",
        description =
            "Retourne la liste des étudiants avec le statut KYC 'EN_ATTENTE' "
                + "(en attente de validation de leur mandat de prélèvement papier).")
    @ApiResponse(responseCode = "200", description = "Liste des mandats en cours")
    @ApiResponse(responseCode = "404", description = "Aucun mandat en cours")
    public ResponseEntity<?> listPendingMandates(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<StudentResponse> mandatEnCours =
                studentService.findByStatutKYC(KycStatus.EN_ATTENTE, pageable);

            if (!mandatEnCours.hasContent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        Map.of(
                            "error",
                            "NO_PENDING_MANDATES",
                            "message",
                            "Aucun mandat en attente de validation"));
            }

            return ResponseEntity.ok(mandatEnCours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "FETCH_FAILED", "message", e.getMessage()));
        }
    }
}
