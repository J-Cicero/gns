package com.backend.gns.commerce.application.controllers;

import com.backend.gns.commerce.application.dtos.requests.LiquidationRequest;
import com.backend.gns.commerce.domain.services.LiquidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/liquidations")
@RequiredArgsConstructor
public class LiquidationController {

    private final LiquidationService liquidationService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LiquidationRequest request) {
        return ResponseEntity.ok(liquidationService.create(request));
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
        return liquidationService.findByTrackingId(trackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/boutique/{boutiqueId}")
    public ResponseEntity<?> findByBoutiqueId(@PathVariable UUID boutiqueId) {
        return ResponseEntity.ok(liquidationService.findByBoutiqueId(boutiqueId));
    }

    @PatchMapping("/{trackingId}/valider")
    public ResponseEntity<?> validerLiquidation(
            @PathVariable UUID trackingId, 
            @RequestParam String referenceVirement) {
        return ResponseEntity.ok(liquidationService.validerLiquidation(trackingId, referenceVirement));
    }
}
