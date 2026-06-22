package com.backend.gns.commerce.application.controllers;

import com.backend.gns.commerce.application.dtos.requests.StudentLiquidationRequest;
import com.backend.gns.commerce.application.dtos.responses.StudentLiquidationResponse;
import com.backend.gns.commerce.domain.services.StudentLiquidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/student-liquidations")
@RequiredArgsConstructor
public class StudentLiquidationController {

    private final StudentLiquidationService studentLiquidationService;

    @PostMapping
    public ResponseEntity<StudentLiquidationResponse> create(@RequestBody StudentLiquidationRequest request) {
        return ResponseEntity.ok(studentLiquidationService.create(request));
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<StudentLiquidationResponse> getByTrackingId(@PathVariable UUID trackingId) {
        return studentLiquidationService.findByTrackingId(trackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{trackingId}/valider")
    public ResponseEntity<StudentLiquidationResponse> valider(
            @PathVariable UUID trackingId,
            @RequestParam String referenceVirement) {
        return ResponseEntity.ok(studentLiquidationService.validerLiquidation(trackingId, referenceVirement));
    }
}
