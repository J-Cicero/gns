package com.backend.gns.core.parametrage.application.controllers;

import com.backend.gns.core.parametrage.application.dtos.requests.CompteBancaireRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.CompteBancaireResponse;
import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.services.CompteBancaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comptes-bancaires")
@RequiredArgsConstructor
public class CompteBancaireController {

    private final CompteBancaireService service;

    @PostMapping("/owner/{ownerTrackingId}/type/{ownerType}")
    public ResponseEntity<CompteBancaireResponse> createAccount(
            @PathVariable UUID ownerTrackingId,
            @PathVariable ProprietaireType ownerType,
            @RequestBody CompteBancaireRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createAccount(ownerTrackingId, request));
    }

    @PostMapping(value = "/{compteTrackingId}/rib", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompteBancaireResponse> uploadRib(
            @PathVariable UUID compteTrackingId,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(service.uploadRib(compteTrackingId, file));
    }

    @PostMapping(value = "/{compteTrackingId}/mandat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadMandat(
            @PathVariable UUID compteTrackingId,
            @RequestParam("file") MultipartFile file) {

        service.uploadMandat(compteTrackingId, file);
        return ResponseEntity.ok().build();
    }

    // --- NOUVELLES MÉTHODES ---

    @GetMapping
    public ResponseEntity<List<CompteBancaireResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/owner/{ownerTrackingId}")
    public ResponseEntity<CompteBancaireResponse> findByOwnerTrackingId(@PathVariable UUID ownerTrackingId) {
        return service.findByOwnerTrackingId(ownerTrackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        service.delete(trackingId);
        return ResponseEntity.noContent().build();
    }
}