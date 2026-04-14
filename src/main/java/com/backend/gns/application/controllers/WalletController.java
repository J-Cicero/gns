package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.WalletRequest;
import com.backend.gns.domain.dtos.responses.WalletResponse;
import com.backend.gns.domain.dtos.responses.PaiementResponse;
import com.backend.gns.domain.services.WalletService;
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
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Tag(name = "WalletController", description = "Gestion des portefeuilles et consultation de leurs transactions")
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    @Operation(summary = "Créer un nouveau portefeuille", description = "Crée un portefeuille (RELAIS ou HORIZON) pour un étudiant")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Portefeuille créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<WalletResponse> create(@RequestBody WalletRequest request) {
        WalletResponse response = walletService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les portefeuilles", description = "Récupère la liste de tous les portefeuilles du système")
    @ApiResponse(responseCode = "200", description = "Liste récupérée")
    @Transactional(readOnly = true)
    public ResponseEntity<List<WalletResponse>> getAll() {
        List<WalletResponse> responses = walletService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un portefeuille", description = "Récupère les détails d'un portefeuille par son identifiant unique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portefeuille trouvé"),
            @ApiResponse(responseCode = "404", description = "Portefeuille non trouvé")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<WalletResponse> getOne(@PathVariable UUID trackingId) {
        WalletResponse response = walletService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour un portefeuille", description = "Modifie les informations d'un portefeuille")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Portefeuille mis à jour"),
            @ApiResponse(responseCode = "404", description = "Portefeuille non trouvé")
    })
    public ResponseEntity<WalletResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody WalletRequest request) {
        WalletResponse response = walletService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer un portefeuille", description = "Supprime un portefeuille du système")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Portefeuille supprimé"),
            @ApiResponse(responseCode = "404", description = "Portefeuille non trouvé")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        walletService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{trackingId}/crediter-horizon")
    @Operation(summary = "Créditer un wallet HORIZON", description = "Crédite 14/15 du plafond sur le portefeuille HORIZON (F2)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Crédit effectué"),
            @ApiResponse(responseCode = "404", description = "Portefeuille non trouvé")
    })
    public ResponseEntity<WalletResponse> crediterHorizon(@PathVariable UUID trackingId) {
        WalletResponse response = walletService.crediterHorizon(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}/deverrouiller")
    @Operation(summary = "Deverrouiller un portefeuille", description = "Déverrouille un portefeuille (F6)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Déverrouillage effectué"),
            @ApiResponse(responseCode = "404", description = "Portefeuille non trouvé")
    })
    public ResponseEntity<WalletResponse> deverrouillerWallet(@PathVariable UUID trackingId) {
        WalletResponse response = walletService.deverrouillerWallet(trackingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{trackingId}/paiements")
    @Operation(summary = "Récupérer l'historique des paiements", description = "Retourne la liste historique de tous les paiements effectués avec ce portefeuille, triée par date décroissante (plus récents en premier)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique récupéré"),
            @ApiResponse(responseCode = "404", description = "Portefeuille non trouvé")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<List<PaiementResponse>> getPaiementsOfWallet(@PathVariable UUID trackingId) {
        List<PaiementResponse> paiements = walletService.getPaiementsOfWallet(trackingId);
        return ResponseEntity.ok(paiements);
    }
}
