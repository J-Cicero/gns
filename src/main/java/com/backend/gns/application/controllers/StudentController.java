package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.StudentRequest;
import com.backend.gns.domain.dtos.responses.StudentResponse;
import com.backend.gns.domain.dtos.responses.WalletResponse;
import com.backend.gns.domain.dtos.responses.CommandeHistoriqueResponse;
import com.backend.gns.domain.services.StudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "StudentController", description = "Gestion des étudiants et consultation de leurs données")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Créer un nouvel étudiant", description = "Enregistre un nouvel étudiant dans le système")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Étudiant créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<StudentResponse> create(@RequestBody StudentRequest request) {
        StudentResponse response = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les étudiants", description = "Récupère la liste complète des étudiants")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @Transactional(readOnly = true)
    public ResponseEntity<List<StudentResponse>> getAll() {
        List<StudentResponse> responses = studentService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    @Operation(summary = "Récupérer un étudiant", description = "Récupère les détails d'un étudiant par son identifiant unique")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<StudentResponse> getOne(@PathVariable UUID trackingId) {
        StudentResponse response = studentService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    @Operation(summary = "Mettre à jour un étudiant", description = "Modifie les informations d'un étudiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant mis à jour"),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    public ResponseEntity<StudentResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody StudentRequest request) {
        StudentResponse response = studentService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    @Operation(summary = "Supprimer un étudiant", description = "Supprime un étudiant du système")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Étudiant supprimé"),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        studentService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{trackingId}/valider-kyc")
    @Operation(summary = "Valider le KYC et créer les wallets", description = "Valide l'identité de l'étudiant et crée ses portefeuilles (RELAIS, HORIZON si éligible)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "KYC validé et wallets créés"),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    public ResponseEntity<StudentResponse> validerKYC(@PathVariable UUID trackingId) {
        StudentResponse response = studentService.validerKYC(trackingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{trackingId}/wallet")
    @Operation(summary = "Récupérer le wallet HORIZON d'un étudiant", description = "Retourne le wallet HORIZON unique de l'étudiant avec son solde, plafond et statut de verrouillage")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wallet récupéré"),
            @ApiResponse(responseCode = "404", description = "Étudiant ou wallet non trouvé")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<WalletResponse> getWalletOfStudent(@PathVariable UUID trackingId) {
        WalletResponse wallet = studentService.getWalletOfStudent(trackingId);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/{trackingId}/commandes")
    @Operation(summary = "Récupérer l'historique des commandes d'un étudiant", description = "Retourne la liste des commandes triée par date décroissante (plus récentes en premier) avec le nom de la boutique du commerçant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historique des commandes récupéré"),
            @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
    })
    @Transactional(readOnly = true)
    public ResponseEntity<List<CommandeHistoriqueResponse>> getCommandesOfStudent(@PathVariable UUID trackingId) {
        List<CommandeHistoriqueResponse> commandes = studentService.getCommandesOfStudent(trackingId);
        return ResponseEntity.ok(commandes);
    }
}
