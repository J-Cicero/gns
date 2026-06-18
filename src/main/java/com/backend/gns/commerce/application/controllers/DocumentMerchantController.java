package com.backend.gns.commerce.application.controllers;

import com.backend.gns.commerce.domain.services.DocumentMerchantService;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.student.application.dtos.responses.DocumentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/merchants/documents")
@Tag(name = "DOCUMENT_MERCHANT", description = "Gestion des documents marchands")
@RequiredArgsConstructor
public class DocumentMerchantController {

    private final DocumentMerchantService documentService;

    @PostMapping("/upload")
    @Operation(summary = "Uploader un document pour un marchand")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam("merchantTrackingId") UUID merchantTrackingId,
            @RequestParam("typeDocument") TypeDocument typeDocument) {
        try {
            DocumentResponse response = documentService.uploadDocument(fichier, merchantTrackingId, typeDocument);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "UPLOAD_FAILED", "message", e.getMessage()));
        }
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<?> findByMerchantId(@PathVariable UUID merchantId, Pageable pageable) {
        return ResponseEntity.ok(documentService.findByMerchantTrackingId(merchantId, pageable));
    }
}
