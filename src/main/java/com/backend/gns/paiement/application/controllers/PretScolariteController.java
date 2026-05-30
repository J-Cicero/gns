package com.backend.gns.paiement.application.controllers;

import com.backend.gns.paiement.application.dtos.requests.PretScolariteRequest;
import com.backend.gns.paiement.application.dtos.responses.PretScolariteResponse;
import com.backend.gns.paiement.domain.services.PretScolariteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pret-scolarite")
@Tag(name = "PRET_SCOLARITE", description = "Gestion des frais de scolarité et prêts anticipés")
@RequiredArgsConstructor
public class PretScolariteController {

  private final PretScolariteService pretScolariteService;

  @PostMapping("/prets")
  @Operation(
      summary = "Demander un prêt scolarité",
      description = "L'étudiant demande à payer sa scolarité via StudCash sans avoir de solde")
  @ApiResponse(responseCode = "201", description = "Prêt accordé et Université créditée")
  public ResponseEntity<?> demanderPret(@RequestBody PretScolariteRequest request) {
    try {
      PretScolariteResponse response =
          pretScolariteService.demanderPretScolarite(
              request.studentTrackingId(), request.universiteTrackingId(), request.montant());
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", "PRECONDITION_FAILED", "message", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "LOAN_REQUEST_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/universite/{universiteTrackingId}")
  @Operation(summary = "Récupérer les prêts scolarité d'une université")
  public ResponseEntity<List<PretScolariteResponse>> findByUniversite(
      @PathVariable UUID universiteTrackingId) {
    return ResponseEntity.ok(pretScolariteService.findByUniversite(universiteTrackingId));
  }
}
