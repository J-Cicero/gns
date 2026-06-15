package com.backend.gns.core.application.controllers;

import com.backend.gns.core.domain.services.DocumentRequisService;
import com.backend.gns.core.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.core.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.student.domain.enums.TargetType;
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
@RequestMapping("/documents-requis")
@Tag(name = "DOCUMENT_REQUIS", description = "Configuration globale des documents obligatoires")
@RequiredArgsConstructor
public class DocumentRequisController {

  private final DocumentRequisService documentRequisService;

  @PostMapping
  @Operation(summary = "Ajouter une règle de document requis")
  public ResponseEntity<DocumentRequisResponse> create(@RequestBody DocumentRequisRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(documentRequisService.create(request));
  }

  @GetMapping("/{trackingId}")
  public ResponseEntity<DocumentRequisResponse> getByTrackingId(@PathVariable UUID trackingId) {
    return documentRequisService.findByTrackingId(trackingId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer une règle")
  public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
    documentRequisService.delete(trackingId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/target/{targetType}")
  @Operation(summary = "Lister les documents pour une cible (STUDENT/MERCHANT)")
  public ResponseEntity<Page<DocumentRequisResponse>> getByTargetType(
      @PathVariable TargetType targetType, Pageable pageable) {
    return ResponseEntity.ok(documentRequisService.findByTargetType(targetType, pageable));
  }

  @GetMapping
  public ResponseEntity<Page<DocumentRequisResponse>> getAll(Pageable pageable) {
    return ResponseEntity.ok(documentRequisService.findAll(pageable));
  }
}
