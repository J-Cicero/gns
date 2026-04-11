package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.AdminRequest;
import com.backend.gns.domain.dtos.responses.AdminResponse;
import com.backend.gns.domain.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<AdminResponse> create(@RequestBody AdminRequest request) {
        AdminResponse response = adminService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AdminResponse>> getAll() {
        List<AdminResponse> responses = adminService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<AdminResponse> getOne(@PathVariable UUID trackingId) {
        AdminResponse response = adminService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    public ResponseEntity<AdminResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody AdminRequest request) {
        AdminResponse response = adminService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        adminService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }
}
