package com.backend.gns.core.systemstatus.controllers;

import com.backend.gns.core.systemstatus.models.CampagneVersement;
import com.backend.gns.core.systemstatus.services.CampagneVersementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campagnes")
@Tag(name = "CAMPAGNE VERSEMENT", description = "Gestion des campagnes de versement")
public class CampagneVersementController {

  private final CampagneVersementService campagneService;

  public CampagneVersementController(CampagneVersementService campagneService) {
    this.campagneService = campagneService;
  }

  // Endpoint pour Admin DBS (Initiateur)
  @PostMapping
  @Operation(summary = "Créer une campagne de versement")
  public ResponseEntity<CampagneVersement> createCampagne(@RequestBody String nomVague) {
    return ResponseEntity.ok(campagneService.createCampagne(nomVague));
  }

  // Endpoint pour Admin GNS (Exécuteur)
  @PostMapping("/{trackingId}/process")
  @Operation(summary = "Exécuter une campagne de versement")
  public ResponseEntity<Void> processCampagne(@PathVariable UUID trackingId) {
    campagneService.processCampagne(trackingId);
    return ResponseEntity.ok().build();
  }
}
