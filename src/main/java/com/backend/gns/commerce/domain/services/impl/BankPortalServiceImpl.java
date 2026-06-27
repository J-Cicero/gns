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
import com.backend.gns.commerce.application.dtos.responses.BoutiqueLiquidationInfoResponse;
import com.backend.gns.commerce.application.dtos.responses.StudentLiquidationInfoResponse;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class BankPortalServiceImpl implements BankPortalService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CompteBancaireRepository compteBancaireRepository;
    private final StudentRepository studentRepository;
    private final BoutiqueRepository boutiqueRepository;

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

        // Calculate monthly profits for the current year
        java.time.LocalDateTime startOfYear = java.time.LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        java.time.LocalDateTime endOfYear = startOfYear.plusYears(1).minusNanos(1);
        java.util.List<com.backend.gns.commerce.domain.models.Transaction> yearTxs = transactionRepository.findByCreatedAtBetween(startOfYear, endOfYear);
        
        java.util.List<BigDecimal> monthlyProfits = new java.util.ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            final int month = i;
            BigDecimal monthProfit = yearTxs.stream()
                .filter(t -> t.getStatus() == com.backend.gns.commerce.domain.enums.TransactionStatut.VALIDE)
                .filter(t -> t.getCreatedAt().getMonthValue() == month)
                .map(t -> t.getBankCommission() != null ? t.getBankCommission() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            monthlyProfits.add(monthProfit);
        }

        return BankFinancialSummaryResponse.builder()
                .totalNetCommercants(netCommercants)
                .totalCommissionsBanque(bankCommissions)
                .totalCommissionsAchats(gnsCommissions)
                .totalDepensesAchats(totalDepenses)
                .totalScolariteUniversites(totalScolarite)
                .monthlyProfits(monthlyProfits)
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

    @Override
    @Transactional(readOnly = true)
    public List<StudentLiquidationInfoResponse> getStudents(UUID bankOperatorTrackingId) {
        log.info("Fetching students for bank operator {}", bankOperatorTrackingId);
        AdminBanque admin = (AdminBanque) userRepository.findByTrackingId(bankOperatorTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank Operator not found"));
        Banque banque = admin.getBanque();
        if (banque == null) {
            throw new ResourceNotFoundException("Bank not associated with operator");
        }

        return studentRepository.findAll().stream().filter(s -> {
            java.util.Optional<com.backend.gns.core.parametrage.domain.models.CompteBancaire> cb = compteBancaireRepository.findByProprietaireTrackingId(s.getTrackingId());
            return cb.isPresent() && cb.get().getBank().getId().equals(banque.getId());
        }).map(s -> {
            String numeroCompte = compteBancaireRepository.findByProprietaireTrackingId(s.getTrackingId())
                    .map(cb -> cb.getAccountNumber())
                    .orElse("Non renseigné");

            return StudentLiquidationInfoResponse.builder()
                .studentTrackingId(s.getTrackingId())
                .nom(s.getLastName())
                .prenom(s.getFirstName())
                .numEtudiant(s.getStudenNumber())
                .bourseTotale(BigDecimal.ZERO)
                .depensesStudCash(BigDecimal.ZERO) // compute this if available
                .resteAPayer(s.getWallet() != null ? s.getWallet().getBalance() : BigDecimal.ZERO)
                .virementEffectue(false)
                .typeBourse("AUCUNE")
                .urlSoucheTamponnee(null)
                .inscritAnnuel(true)
                .inscritDefinitif(true)
                .walletTrackingId(s.getWallet() != null ? s.getWallet().getTrackingId() : null)
                .walletStatus(s.getWallet() != null && s.getWallet().getStatus() != null ? s.getWallet().getStatus().name() : "ACTIF")
                .numeroCompte(numeroCompte)
                .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoutiqueLiquidationInfoResponse> getBoutiques(UUID bankOperatorTrackingId) {
        log.info("Fetching boutiques for bank operator {}", bankOperatorTrackingId);
        AdminBanque admin = (AdminBanque) userRepository.findByTrackingId(bankOperatorTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank Operator not found"));
        Banque banque = admin.getBanque();
        if (banque == null) {
            throw new ResourceNotFoundException("Bank not associated with operator");
        }

        return boutiqueRepository.findAll().stream().filter(b -> {
            if (b.getMerchant() == null) return false;
            java.util.Optional<com.backend.gns.core.parametrage.domain.models.CompteBancaire> cb = compteBancaireRepository.findByProprietaireTrackingId(b.getMerchant().getTrackingId());
            return cb.isPresent() && cb.get().getBank().getId().equals(banque.getId());
        }).map(b -> {
            String proprietaireNom = b.getMerchant() != null ?
                b.getMerchant().getFirstName() + " " + b.getMerchant().getLastName() : "Inconnu";
            
            String numeroCompte = b.getMerchant() != null ? compteBancaireRepository.findByProprietaireTrackingId(b.getMerchant().getTrackingId())
                    .map(cb -> cb.getAccountNumber())
                    .orElse("Non renseigné") : "Non renseigné";

            return BoutiqueLiquidationInfoResponse.builder()
                .boutiqueTrackingId(b.getTrackingId())
                .nomBoutique(b.getName())
                .categorieShop("INCONNU")
                .numeroCompte(numeroCompte)
                .soldeWallet(b.getWallet() != null ? b.getWallet().getBalance() : BigDecimal.ZERO)
                .proprietaireNom(proprietaireNom)
                .merchantTrackingId(b.getMerchant() != null ? b.getMerchant().getTrackingId() : null)
                .walletTrackingId(b.getWallet() != null ? b.getWallet().getTrackingId() : null)
                .walletStatus(b.getWallet() != null && b.getWallet().getStatus() != null ? b.getWallet().getStatus().name() : "ACTIF")
                .build();
        }).collect(Collectors.toList());
    }
}
