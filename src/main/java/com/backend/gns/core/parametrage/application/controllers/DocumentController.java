package com.backend.gns.core.parametrage.application.controllers;

import com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse;
import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.services.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ownerTrackingId") UUID ownerTrackingId,
            @RequestParam("ownerType") ProprietaireType ownerType,
            @RequestParam("documentType") TypeDocument documentType) {
        log.info("Received request to upload document for owner: {}, type: {}", ownerTrackingId, documentType);
        DocumentResponse response = documentService.uploadDocument(file, ownerTrackingId, ownerType, documentType);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/owner/{ownerTrackingId}")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByOwner(@PathVariable UUID ownerTrackingId) {
        List<DocumentResponse> documents = documentService.getDocumentsByOwner(ownerTrackingId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<DocumentResponse> getDocumentByTrackingId(@PathVariable UUID trackingId) {
        DocumentResponse document = documentService.getDocumentByTrackingId(trackingId);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID trackingId) {
        documentService.deleteDocument(trackingId);
        return ResponseEntity.noContent().build();
    }
}
