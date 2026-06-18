package com.backend.gns.core.parametrage.application.controllers;

import com.backend.gns.core.systemstatus.dtos.SystemStatusResponse;
import com.backend.gns.core.systemstatus.services.SystemStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/system-status")
@Tag(name = "SYSTEM STATUS", description = "Gestion de l'état global du système")
public class SystemStatusController {

  private final SystemStatusService systemStatusService;

  public SystemStatusController(SystemStatusService systemStatusService) {
    this.systemStatusService = systemStatusService;
  }

  @GetMapping
  @Operation(
      summary = "Récupérer le statut global du système",
      description =
          "Permet au frontend de connaître l'état actuel de l'année scolaire et la disponibilité des paiements")
  public ResponseEntity<SystemStatusResponse> getGlobalStatus() {
    return ResponseEntity.ok(systemStatusService.getGlobalStatus());
  }
}
