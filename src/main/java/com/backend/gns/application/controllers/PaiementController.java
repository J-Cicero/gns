package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.PaiementRequest;
import com.backend.gns.domain.dtos.requests.PaiementScolariteRequest;
import com.backend.gns.domain.dtos.requests.PaiementSimpleRequest;
import com.backend.gns.domain.dtos.responses.PaiementResponse;
import com.backend.gns.domain.services.PaiementService;
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
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@Tag(name = "PaiementController", description = "Gestion des paiements et consultation de l'historique")
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    @Operation(summary = "Créer un paiement", description = "Crée un nouveau paiement")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paiement créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<PaiementResponse> create(@RequestBody PaiementRequest request) {
        PaiementResponse response = paiementService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les paiements", description = "Récupère la liste de tous les paiements")
    @ApiResponse(responseCode = "200", description = "Liste récupérée")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PaiementResponse>> getAll() {
        List<PaiementResponse> responses = paiementService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un paiement", description = "Récupère les détails d'un paiement par son identifiant unique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement trouvé"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<PaiementResponse> getOne(@PathVariable UUID trackingId) {
        PaiementResponse response = paiementService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour un paiement", description = "Modifie les informations d'un paiement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement mis à jour"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<PaiementResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody PaiementRequest request) {
        PaiementResponse response = paiementService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer un paiement", description = "Supprime un paiement du système")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paiement supprimé"),
            @ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        paiementService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/scolarite")
    @Operation(summary = "Paiement de scolarité", description = "Effectue un paiement de scolarité (F7)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paiement effectué"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou solde insuffisant")
    })
    public ResponseEntity<PaiementResponse> effectuerPaiementScolarite(@RequestBody PaiementScolariteRequest request) {
        PaiementResponse response = paiementService.effectuerPaiementScolarite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/effectuer")
    @Operation(summary = "Paiement simple", description = "Effectue un paiement simple chez un commerçant avec commission (F4)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paiement effectué"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou solde insuffisant")
    })
    public ResponseEntity<PaiementResponse> effectuerPaiement(@RequestBody PaiementSimpleRequest request) {
        PaiementResponse response = paiementService.effectuerPaiement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/commande/{commandeRef}")
    @Operation(summary = "Récupérer les paiements d'une commande", description = "Retourne les 1 ou 2 paiements associés à une référence de commande")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiements récupérés"),
            @ApiResponse(responseCode = "404", description = "Aucun paiement trouvé")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<List<PaiementResponse>> getPaiementsByCommandeRef(@PathVariable String commandeRef) {
        List<PaiementResponse> paiements = paiementService.getPaiementsByCommandeRef(commandeRef);
        return ResponseEntity.ok(paiements);
    }
}
