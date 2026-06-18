package com.backend.gns.core.application.controllers;

import com.backend.gns.core.application.dtos.requests.CompteBancaireRequest;
import com.backend.gns.core.application.dtos.responses.CompteBancaireResponse;
import com.backend.gns.core.domain.services.CompteBancaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/banques/comptes-gns")
@RequiredArgsConstructor
public class CompteBancaireController {

    private final CompteBancaireService service;

    @PostMapping
    public ResponseEntity<CompteBancaireResponse> create(@RequestBody CompteBancaireRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CompteBancaireResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/proprietaire/{ownerTrackingId}")
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
