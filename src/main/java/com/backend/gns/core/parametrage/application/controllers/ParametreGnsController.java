package com.backend.gns.core.parametrage.application.controllers;

import com.backend.gns.core.parametrage.application.dtos.requests.ParametreGnsRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.ParametreGnsResponse;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/parametres-gns")
@Tag(name = "PARAMETRES GNS", description = "Gestion des paramètres système (Admin GNS)")
@RequiredArgsConstructor
public class ParametreGnsController {

  private final ParametreGnsService service;

  @PostMapping
  @Operation(summary = "Créer un paramètre")
  public ResponseEntity<ParametreGnsResponse> create(@RequestBody ParametreGnsRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
  }

  @PutMapping("/{trackingId}")
  @Operation(summary = "Mettre à jour un paramètre")
  public ResponseEntity<ParametreGnsResponse> update(
      @PathVariable UUID trackingId, @RequestBody ParametreGnsRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(service.update(trackingId, request));
  }

  @GetMapping("/{trackingId}")
  public ResponseEntity<ParametreGnsResponse> findByTrackingId(@PathVariable UUID trackingId) {
    return service
        .findByTrackingId(trackingId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<Page<ParametreGnsResponse>> findAll(Pageable pageable) {
    return ResponseEntity.ok(service.findAll(pageable));
  }

  @GetMapping("/type/{type}")
  @Operation(summary = "Rechercher par type de paramètre")
  public ResponseEntity<ParametreGnsResponse> findByNom(
      @PathVariable TypeParametreGns type) {
    return service
        .findByNomParametre(type)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
