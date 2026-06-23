package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.domain.services.BankPortalService;
import com.backend.gns.commerce.infrastructure.repositories.TransactionRepository;
import com.backend.gns.user.application.dtos.responses.BankFinancialSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import com.backend.gns.user.infrastructure.repositories.UserRepository;
import com.backend.gns.user.domain.models.AdminBanque;
import com.backend.gns.core.parametrage.domain.models.Banque;
import com.backend.gns.user.application.dtos.responses.BanqueInfoResponse;
import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.infrastructure.repositories.CompteBancaireRepository;
import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankPortalServiceImpl implements BankPortalService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CompteBancaireRepository compteBancaireRepository;

    @Override
    @Transactional(readOnly = true)
    public BankFinancialSummaryResponse getFinancialSummary(UUID bankOperatorTrackingId) {
        log.info("Fetching financial summary for bank operator {}", bankOperatorTrackingId);

        BigDecimal netCommercants = transactionRepository.sumNetCommercants();
        BigDecimal bankCommissions = transactionRepository.sumBankCommissions();
        BigDecimal gnsCommissions = transactionRepository.sumGnsCommissions();
        BigDecimal totalDepenses = transactionRepository.sumTotalDepenses();
        // Assuming scolarite universites will be handled later. For now we set 0.
        BigDecimal totalScolarite = BigDecimal.ZERO;

        return BankFinancialSummaryResponse.builder()
                .totalNetCommercants(netCommercants)
                .totalCommissionsBanque(bankCommissions)
                .totalCommissionsAchats(gnsCommissions)
                .totalDepensesAchats(totalDepenses)
                .totalScolariteUniversites(totalScolarite)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BanqueInfoResponse getBanqueInfo(UUID bankOperatorTrackingId) {
        log.info("Fetching bank info for operator {}", bankOperatorTrackingId);
        
        AdminBanque admin = (AdminBanque) userRepository.findByTrackingId(bankOperatorTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank Operator not found"));
                
        Banque banque = admin.getBanque();
        if (banque == null) {
            throw new ResourceNotFoundException("Bank not associated with operator");
        }
        
        String compteCentralGns = compteBancaireRepository.findByOwnerTypeAndBank(ProprietaireType.GNS, banque)
                .map(compte -> compte.getAccountNumber())
                .orElse(null);
        
        return BanqueInfoResponse.builder()
                .trackingId(banque.getTrackingId())
                .code(banque.getCode())
                .nom(banque.getName())
                // .logoUrl(banque.getLogoUrl()) // assuming no logo yet
                .compteCentralGns(compteCentralGns)
                .build();
    }
}
