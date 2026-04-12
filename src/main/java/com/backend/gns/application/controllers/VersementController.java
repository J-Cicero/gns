package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.VersementRequest;
import com.backend.gns.domain.dtos.responses.VersementResponse;
import com.backend.gns.domain.services.VersementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/versements")
@RequiredArgsConstructor
public class VersementController {

    private final VersementService versementService;

    @PostMapping
    public ResponseEntity<VersementResponse> create(@RequestBody VersementRequest request) {
        VersementResponse response = versementService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<VersementResponse>> getAll() {
        List<VersementResponse> responses = versementService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<VersementResponse> getOne(@PathVariable UUID trackingId) {
        VersementResponse response = versementService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    public ResponseEntity<VersementResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody VersementRequest request) {
        VersementResponse response = versementService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        versementService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{trackingId}/executer-remboursement")
    public ResponseEntity<VersementResponse> executerRemboursementDBS(@PathVariable UUID trackingId) {
        VersementResponse response = versementService.executerRemboursementDBS(trackingId);
        return ResponseEntity.ok(response);
    }
}
