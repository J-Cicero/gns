package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.PaiementRequest;
import com.backend.gns.domain.dtos.responses.PaiementResponse;
import com.backend.gns.domain.services.PaiementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    public ResponseEntity<PaiementResponse> create(@RequestBody PaiementRequest request) {
        PaiementResponse response = paiementService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PaiementResponse>> getAll() {
        List<PaiementResponse> responses = paiementService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<PaiementResponse> getOne(@PathVariable UUID trackingId) {
        PaiementResponse response = paiementService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    public ResponseEntity<PaiementResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody PaiementRequest request) {
        PaiementResponse response = paiementService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        paiementService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }
}
