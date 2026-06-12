package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.ScolariteYearRequest;
import com.backend.gns.student.application.dtos.responses.ScolariteYearResponse;
import com.backend.gns.student.domain.services.ScolariteYearService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scolarite-years")
@Tag(name = "SCOLARITE_YEAR", description = "Gestion des années scolaires")
@RequiredArgsConstructor
public class ScolariteYearController {

  private final ScolariteYearService scolariteYearService;

  @PostMapping
  @Operation(summary = "Créer une année scolaire")
  public ResponseEntity<ScolariteYearResponse> create(@RequestBody ScolariteYearRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(scolariteYearService.create(request));
  }

  @GetMapping("/{trackingId}")
  public ResponseEntity<ScolariteYearResponse> getByTrackingId(@PathVariable UUID trackingId) {
    return scolariteYearService
        .findByTrackingId(trackingId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/active")
  @Operation(summary = "Récupérer l'année scolaire active")
  public ResponseEntity<ScolariteYearResponse> getActiveYear() {
    return scolariteYearService
        .findActiveYear()
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/{trackingId}/cloturer")
  @Operation(summary = "Clôturer une année scolaire")
  public ResponseEntity<Void> cloturer(@PathVariable UUID trackingId) {
    scolariteYearService.cloturer(trackingId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(summary = "Lister toutes les années scolaires")
  public ResponseEntity<Page<ScolariteYearResponse>> getAll(Pageable pageable) {
    return ResponseEntity.ok(scolariteYearService.findAll(pageable));
  }
}
