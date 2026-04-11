package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.BudgetVirtuelRequest;
import com.backend.gns.domain.dtos.responses.BudgetVirtuelResponse;
import com.backend.gns.domain.services.BudgetVirtuelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetVirtuelController {

    private final BudgetVirtuelService budgetVirtuelService;

    @PostMapping
    public ResponseEntity<BudgetVirtuelResponse> create(@RequestBody BudgetVirtuelRequest request) {
        BudgetVirtuelResponse response = budgetVirtuelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetVirtuelResponse>> getAll() {
        List<BudgetVirtuelResponse> responses = budgetVirtuelService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<BudgetVirtuelResponse> getOne(@PathVariable UUID trackingId) {
        BudgetVirtuelResponse response = budgetVirtuelService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    public ResponseEntity<BudgetVirtuelResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody BudgetVirtuelRequest request) {
        BudgetVirtuelResponse response = budgetVirtuelService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        budgetVirtuelService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }
}
