package com.backend.gns.user.application.controllers;

import com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.student.domain.services.DocumentEtudiantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
public class DocumentAdminController {

    private final DocumentEtudiantService documentEtudiantService;

    @GetMapping("/{studentTrackingId}/documents")
    @PreAuthorize("hasRole('ADMIN_GNS')")
    public ResponseEntity<List<DocumentEtudiantResponse>> getDocumentsByStudent(@PathVariable UUID studentTrackingId) {
        return ResponseEntity.ok(documentEtudiantService.getDocumentsByStudent(studentTrackingId));
    }
}
