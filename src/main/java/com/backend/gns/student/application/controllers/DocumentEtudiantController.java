package com.backend.gns.student.application.controllers;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.application.dtos.responses.DocumentResponse;
import com.backend.gns.student.domain.services.DocumentEtudiantService;
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
@RequestMapping("/students/documents")
@Tag(name = "DOCUMENT_ETUDIANT", description = "Gestion des documents étudiants")
@RequiredArgsConstructor
public class DocumentEtudiantController {

    private final DocumentEtudiantService documentService;

    @PostMapping("/upload")
    @Operation(summary = "Uploader un document pour une inscription")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam("studentTrackingId") UUID studentTrackingId,
            @RequestParam("inscriptionTrackingId") UUID inscriptionTrackingId,
            @RequestParam("typeDocument") TypeDocument typeDocument) {
        try {
            DocumentResponse response = documentService.uploadDocument(fichier, studentTrackingId, inscriptionTrackingId, typeDocument);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "UPLOAD_FAILED", "message", e.getMessage()));
        }
    }

    @GetMapping("/inscription/{inscriptionId}")
    public ResponseEntity<?> findByInscriptionId(@PathVariable UUID inscriptionId, Pageable pageable) {
        return ResponseEntity.ok(documentService.findByInscriptionId(inscriptionId, pageable));
    }
}
