package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.MerchantRequest;
import com.backend.gns.domain.dtos.responses.MerchantResponse;
import com.backend.gns.domain.services.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping
    public ResponseEntity<MerchantResponse> create(@RequestBody MerchantRequest request) {
        MerchantResponse response = merchantService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MerchantResponse>> getAll() {
        List<MerchantResponse> responses = merchantService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<MerchantResponse> getOne(@PathVariable UUID trackingId) {
        MerchantResponse response = merchantService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    public ResponseEntity<MerchantResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody MerchantRequest request) {
        MerchantResponse response = merchantService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        merchantService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }
}
