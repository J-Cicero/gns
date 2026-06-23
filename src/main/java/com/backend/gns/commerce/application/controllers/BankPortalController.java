package com.backend.gns.commerce.application.controllers;

import com.backend.gns.commerce.domain.services.BankPortalService;
import com.backend.gns.user.application.dtos.responses.BankFinancialSummaryResponse;
import com.backend.gns.user.application.dtos.responses.BanqueInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bank-portal")
@RequiredArgsConstructor
public class BankPortalController {

    private final BankPortalService bankPortalService;

    @GetMapping("/summary")
    public ResponseEntity<BankFinancialSummaryResponse> getFinancialSummary(
            @RequestParam UUID bankOperatorTrackingId) {
        return ResponseEntity.ok(bankPortalService.getFinancialSummary(bankOperatorTrackingId));
    }

    @GetMapping("/info")
    public ResponseEntity<BanqueInfoResponse> getBanqueInfo(
            @RequestParam UUID bankOperatorTrackingId) {
        return ResponseEntity.ok(bankPortalService.getBanqueInfo(bankOperatorTrackingId));
    }
}
