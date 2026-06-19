package com.backend.gns.core.parametrage.application.controllers;

import com.backend.gns.core.parametrage.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.services.DocumentRequisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/document-requis")
@RequiredArgsConstructor
@Slf4j
public class DocumentRequisController {

    private final DocumentRequisService documentRequisService;

    @PostMapping
    public ResponseEntity<DocumentRequisResponse> saveOrUpdateDocumentRequis(@RequestBody DocumentRequisRequest request) {
        DocumentRequisResponse response = documentRequisService.saveOrUpdate(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<DocumentRequisResponse> getDocumentRequisByTrackingId(@PathVariable UUID trackingId) {
        return documentRequisService.findByTrackingId(trackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{typeDocument}")
    public ResponseEntity<DocumentRequisResponse> getDocumentRequisByType(@PathVariable TypeDocument typeDocument) {
        return documentRequisService.findByTypeDocument(typeDocument)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DocumentRequisResponse>> getAllDocumentRequis() {
        List<DocumentRequisResponse> response = documentRequisService.findAll();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> deleteDocumentRequis(@PathVariable UUID trackingId) {
        documentRequisService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }
}
