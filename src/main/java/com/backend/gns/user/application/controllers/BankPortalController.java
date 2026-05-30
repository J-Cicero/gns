package com.backend.gns.user.application.controllers;

import com.backend.gns.user.application.services.BankPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank-portal")
@Tag(name = "BANK_PORTAL", description = "Interface pour les banques partenaires")
@RequiredArgsConstructor
public class BankPortalController {

  private final BankPortalService bankPortalService;

  @GetMapping("/students")
  @Operation(
      summary = "Liste des étudiants pour la banque",
      description = "Récupère les étudiants affiliés à la banque de l'opérateur connecté")
  public ResponseEntity<?> getStudentsForBank(@RequestParam UUID bankOperatorTrackingId) {
    try {
      return ResponseEntity.ok(bankPortalService.getStudentsForBank(bankOperatorTrackingId));
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(Map.of("error", "FETCH_FAILED", "message", e.getMessage()));
    }
  }

  @PostMapping("/students/{studentTrackingId}/valider-mandat")
  @Operation(
      summary = "Valider la souche tamponnée",
      description =
          "La banque valide officiellement le mandat de prélèvement après vérification physique")
  public ResponseEntity<?> validerMandat(
      @PathVariable UUID studentTrackingId, @RequestParam boolean valide) {
    try {
      bankPortalService.validerMandat(studentTrackingId, valide);
      return ResponseEntity.ok(Map.of("success", true, "message", "Mandat mis à jour avec succès"));
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(Map.of("error", "VALIDATION_FAILED", "message", e.getMessage()));
    }
  }

  @PatchMapping("/students/{studentTrackingId}/marquer-traite")
  @Operation(
      summary = "Marquer le virement comme effectué",
      description = "La banque confirme que le virement a été fait dans son système interne")
  public ResponseEntity<?> marquerTraite(@PathVariable UUID studentTrackingId) {
    try {
      bankPortalService.marquerTraite(studentTrackingId);
      return ResponseEntity.ok(
          Map.of("success", true, "message", "Virement marqué comme effectué"));
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }
}
