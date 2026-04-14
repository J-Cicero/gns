package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.MerchantRequest;
import com.backend.gns.domain.dtos.responses.MerchantResponse;
import com.backend.gns.domain.dtos.responses.BudgetVirtuelResponse;
import com.backend.gns.domain.dtos.responses.CommandeHistoriqueResponse;
import com.backend.gns.domain.services.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
@Tag(name = "MerchantController", description = "Gestion des commerçants et consultation de leurs données")
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping
    @Operation(summary = "Créer un nouveau commerçant", description = "Enregistre un nouveau commerçant dans le système")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Commerçant créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<MerchantResponse> create(@RequestBody MerchantRequest request) {
        MerchantResponse response = merchantService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les commerçants", description = "Récupère la liste complète des commerçants")
    @ApiResponse(responseCode = "200", description = "Liste récupérée")
    @Transactional(readOnly = true)
    public ResponseEntity<List<MerchantResponse>> getAll() {
        List<MerchantResponse> responses = merchantService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un commerçant", description = "Récupère les détails d'un commerçant par son identifiant unique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Commerçant trouvé"),
            @ApiResponse(responseCode = "404", description = "Commerçant non trouvé")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<MerchantResponse> getOne(@PathVariable UUID trackingId) {
        MerchantResponse response = merchantService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour un commerçant", description = "Modifie les informations d'un commerçant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Commerçant mis à jour"),
            @ApiResponse(responseCode = "404", description = "Commerçant non trouvé")
    })
    public ResponseEntity<MerchantResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody MerchantRequest request) {
        MerchantResponse response = merchantService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer un commerçant", description = "Supprime un commerçant du système")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Commerçant supprimé"),
            @ApiResponse(responseCode = "404", description = "Commerçant non trouvé")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        merchantService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{trackingId}/budget-actif")
    @Operation(summary = "Récupérer le budget actif du mois", description = "Retourne le budget virtuel du commerçant pour le mois courant avec montant alloué, montant restant et statut d'épuisement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget récupéré"),
            @ApiResponse(responseCode = "404", description = "Budget ou commerçant non trouvé")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<BudgetVirtuelResponse> getBudgetActif(@PathVariable UUID trackingId) {
        BudgetVirtuelResponse budget = merchantService.getBudgetActif(trackingId);
        return ResponseEntity.ok(budget);
    }

    @GetMapping("/{trackingId}/commandes")
    @Operation(summary = "Récupérer les commandes reçues", description = "Retourne la liste des commandes reçues par le commerçant, triée par date décroissante (plus récentes en premier), avec le nom et prénom de l'étudiant acheteur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Commandes récupérées"),
            @ApiResponse(responseCode = "404", description = "Commerçant non trouvé")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<List<CommandeHistoriqueResponse>> getCommandesRecues(@PathVariable UUID trackingId) {
        List<CommandeHistoriqueResponse> commandes = merchantService.getCommandesRecues(trackingId);
        return ResponseEntity.ok(commandes);
    }
}
