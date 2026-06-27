package com.backend.gns.user.application.controllers;

import com.backend.gns.commerce.domain.services.DocumentMerchantService;
import com.backend.gns.core.parametrage.application.dtos.requests.DocumentValidationRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse;
import com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.student.domain.services.DocumentEtudiantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin-banque/documents")
@RequiredArgsConstructor
public class DocumentBankAdminController {

    private final DocumentEtudiantService documentEtudiantService;
    private final DocumentMerchantService documentMerchantService;

    // Validation des documents Étudiants
    @PutMapping("/students/{documentTrackingId}/status")
    @PreAuthorize("hasRole('ADMIN_BANQUE') or hasRole('ADMIN_GNS')")
    public ResponseEntity<DocumentEtudiantResponse> validateStudentDocument(
            @PathVariable UUID documentTrackingId,
            @Valid @RequestBody DocumentValidationRequest request) {
        return ResponseEntity.ok(documentEtudiantService.updateDocumentStatus(
                documentTrackingId, request.getStatus(), request.getRejectionReason()));
    }

    // Validation des documents Marchands
    @PutMapping("/merchants/{documentTrackingId}/status")
    @PreAuthorize("hasRole('ADMIN_BANQUE') or hasRole('ADMIN_GNS')")
    public ResponseEntity<DocumentResponse> validateMerchantDocument(
            @PathVariable UUID documentTrackingId,
            @Valid @RequestBody DocumentValidationRequest request) {
        return ResponseEntity.ok(documentMerchantService.updateDocumentStatus(
                documentTrackingId, request.getStatus(), request.getRejectionReason()));
    }
}
