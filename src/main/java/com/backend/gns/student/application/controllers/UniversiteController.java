package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.UniversiteRequest;
import com.backend.gns.student.application.dtos.responses.UniversiteResponse;
import com.backend.gns.student.domain.services.UniversiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/universites")
@Tag(name = "UNIVERSITES", description = "Gestion des universités")
@RequiredArgsConstructor
public class UniversiteController {

  private final UniversiteService service;

  @PostMapping
  @Operation(summary = "Créer une université")
  public ResponseEntity<UniversiteResponse> create(@RequestBody UniversiteRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
  }

  @GetMapping("/{trackingId}")
  public ResponseEntity<UniversiteResponse> findByTrackingId(@PathVariable UUID trackingId) {
    return service
        .findByTrackingId(trackingId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<Page<UniversiteResponse>> findAll(Pageable pageable) {
    return ResponseEntity.ok(service.findAll(pageable));
  }

  @PatchMapping("/etat/{trackingId}")
  public ResponseEntity<UniversiteResponse> updateEtat(
      @PathVariable UUID trackingId, @RequestParam boolean etat) {
    return ResponseEntity.ok(service.updateEtat(trackingId, etat));
  }

  @GetMapping("/summary-stats")
  public ResponseEntity<List<Map<String, Object>>> getSummaryStats() {
    return ResponseEntity.ok(service.getSummaryStats());
  }

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer une université")
  public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
    service.delete(trackingId);
    return ResponseEntity.noContent().build();
  }
}
