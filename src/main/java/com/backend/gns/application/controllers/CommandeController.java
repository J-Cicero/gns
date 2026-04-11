package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.CommandeRequest;
import com.backend.gns.domain.dtos.responses.CommandeResponse;
import com.backend.gns.domain.services.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @PostMapping
    public ResponseEntity<CommandeResponse> create(@RequestBody CommandeRequest request) {
        CommandeResponse response = commandeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommandeResponse>> getAll() {
        List<CommandeResponse> responses = commandeService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<CommandeResponse> getOne(@PathVariable UUID trackingId) {
        CommandeResponse response = commandeService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    public ResponseEntity<CommandeResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody CommandeRequest request) {
        CommandeResponse response = commandeService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        commandeService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }
}
