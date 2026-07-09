package com.backend.gns.commerce.application.controllers;

import com.backend.gns.commerce.application.dtos.requests.TransactionRequest;
import com.backend.gns.commerce.domain.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createPayment(request));
    }

    @GetMapping("/stats/global")
    public ResponseEntity<com.backend.gns.commerce.application.dtos.responses.TransactionStatsResponse> getGlobalStats() {
        return ResponseEntity.ok(transactionService.getGlobalStats());
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
        return transactionService.findByTrackingId(trackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> findAll(Pageable pageable) {
        return ResponseEntity.ok(transactionService.findAll(pageable));
    }

    @GetMapping("/boutique/{boutiqueId}")
    public ResponseEntity<?> findByBoutiqueId(@PathVariable UUID boutiqueId, Pageable pageable) {
        return ResponseEntity.ok(transactionService.findByBoutiqueId(boutiqueId, pageable));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> findByStudentId(@PathVariable UUID studentId, Pageable pageable) {
        return ResponseEntity.ok(transactionService.findByStudentId(studentId, pageable));
    }

    @GetMapping("/stats/chart")
    public ResponseEntity<?> getChartStats() {
        return ResponseEntity.ok(transactionService.getChartStats());
    }
}
