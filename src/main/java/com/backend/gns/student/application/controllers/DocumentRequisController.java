package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.student.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.services.DocumentRequisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents-requis")
@Tag(name = "DOCUMENT_REQUIS", description = "Configuration des documents obligatoires par niveau")
@RequiredArgsConstructor
public class DocumentRequisController {

  private final DocumentRequisService documentRequisService;

  @PostMapping
  @Operation(summary = "Ajouter une règle de document requis")
  public ResponseEntity<DocumentRequisResponse> create(@RequestBody DocumentRequisRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(documentRequisService.create(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DocumentRequisResponse> getById(@PathVariable Long id) {
    return ResponseEntity.ok(documentRequisService.findById(id));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Mettre à jour une règle")
  public ResponseEntity<DocumentRequisResponse> update(
      @PathVariable Long id, @RequestBody DocumentRequisRequest request) {
    return ResponseEntity.ok(documentRequisService.update(id, request));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Supprimer une règle")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    documentRequisService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/niveau/{niveau}")
  @Operation(summary = "Lister les documents actifs requis pour un niveau")
  public ResponseEntity<List<DocumentRequisResponse>> getActiveByNiveau(
      @PathVariable StudentNiveau niveau) {
    return ResponseEntity.ok(documentRequisService.findActiveByNiveau(niveau));
  }

  @GetMapping
  @Operation(summary = "Lister toutes les règles")
  public ResponseEntity<Page<DocumentRequisResponse>> getAll(Pageable pageable) {
    return ResponseEntity.ok(documentRequisService.findAll(pageable));
  }
}
