package com.backend.gns.core.parametrage.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/public/system-status")
@RequiredArgsConstructor
public class SystemStatusController {

    private final com.backend.gns.student.domain.services.ScolariteYearService scolariteYearService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        boolean hasActiveYear = scolariteYearService.findActiveYear().isPresent();
        String status = hasActiveYear ? "ACTIVE" : "INITIALISATION";
        return ResponseEntity.ok(Map.of(
            "currentStatus", status,
            "paymentEnabled", hasActiveYear
        ));
    }
}
