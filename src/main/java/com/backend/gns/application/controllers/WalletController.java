package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.WalletRequest;
import com.backend.gns.domain.dtos.responses.WalletResponse;
import com.backend.gns.domain.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletResponse> create(@RequestBody WalletRequest request) {
        WalletResponse response = walletService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WalletResponse>> getAll() {
        List<WalletResponse> responses = walletService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<WalletResponse> getOne(@PathVariable UUID trackingId) {
        WalletResponse response = walletService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    public ResponseEntity<WalletResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody WalletRequest request) {
        WalletResponse response = walletService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        walletService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }
}
