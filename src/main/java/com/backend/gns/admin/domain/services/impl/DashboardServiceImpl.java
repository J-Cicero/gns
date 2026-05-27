package com.backend.gns.admin.domain.services.impl;

import com.backend.gns.Shared.wallet.infrastructure.repositories.VersementRepository;
import com.backend.gns.admin.domain.services.DashboardService;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.paiement.infrastructure.repositories.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final VersementRepository versementRepository;
    private final StudentRepository studentRepository;
    private final BoutiqueRepository boutiqueRepository;
    private final PaiementRepository paiementRepository;

    @Override
    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();
        
        BigDecimal totalBourses = versementRepository.sumAllMontants();
        stats.put("totalBourses", totalBourses != null ? totalBourses : BigDecimal.ZERO);
        long totalStudents = studentRepository.count();
        stats.put("totalStudents", totalStudents);
        stats.put("totalBoutiques", boutiqueRepository.count());
        long totalTx = paiementRepository.count();
        stats.put("totalTransactions", totalTx);
        
        long totalEligibles = studentRepository.countByStatutKYC(com.backend.gns.Shared.domain.enums.KycStatus.VALIDEE);
        long totalPending = studentRepository.countByStatutKYC(com.backend.gns.Shared.domain.enums.KycStatus.EN_ATTENTE);
        long totalRejected = studentRepository.countByStatutKYC(com.backend.gns.Shared.domain.enums.KycStatus.REJETE);
        stats.put("totalEligibles", totalEligibles);
        stats.put("totalPending", totalPending);
        
        double verificationRate = totalStudents > 0 ? ((totalEligibles + totalRejected) * 100.0 / totalStudents) : 0.0;
        stats.put("verificationRate", Double.parseDouble(String.format(java.util.Locale.US, "%.1f", verificationRate)));
        
        java.time.LocalDateTime startOfToday = java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        java.time.LocalDateTime endOfToday = java.time.LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        BigDecimal volumeToday = paiementRepository.sumMontantDebiteBetweenDatesAndStatut(startOfToday, endOfToday, com.backend.gns.paiement.domain.enums.PaiementStatut.VALIDE);
        stats.put("volumeToday", volumeToday != null ? volumeToday : BigDecimal.ZERO);
        
        Long failedTxns = paiementRepository.countByStatutPaiement(com.backend.gns.paiement.domain.enums.PaiementStatut.ANNULEE);
        stats.put("failedTxns", failedTxns != null ? failedTxns : 0L);
        
        Long successTxns = paiementRepository.countByStatutPaiement(com.backend.gns.paiement.domain.enums.PaiementStatut.VALIDE);
        double successRate = totalTx > 0 ? (successTxns * 100.0 / totalTx) : 0.0;
        stats.put("successRate", Double.parseDouble(String.format(java.util.Locale.US, "%.2f", successRate)));

        return stats;
    }

    @Override
    public List<Map<String, Object>> getFluxMensuel() {
        List<Map<String, Object>> data = new ArrayList<>();
        String[] mois = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
        
        java.time.LocalDateTime startOfYear = java.time.LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        java.time.LocalDateTime endOfYear = startOfYear.plusYears(1).minusNanos(1);

        List<com.backend.gns.Shared.wallet.domain.models.Versement> versements = versementRepository.findByDateVersementBetween(startOfYear, endOfYear);
        List<com.backend.gns.paiement.domain.models.Paiement> paiements = paiementRepository.findByDateBetween(startOfYear, endOfYear);

        BigDecimal[] boursesParMois = new BigDecimal[12];
        BigDecimal[] paiementsParMois = new BigDecimal[12];
        
        for (int i = 0; i < 12; i++) {
            boursesParMois[i] = BigDecimal.ZERO;
            paiementsParMois[i] = BigDecimal.ZERO;
        }

        for (com.backend.gns.Shared.wallet.domain.models.Versement v : versements) {
            if (v.getDateVersement() != null && v.getMontantVerse() != null) {
                int month = v.getDateVersement().getMonthValue() - 1;
                boursesParMois[month] = boursesParMois[month].add(v.getMontantVerse());
            }
        }

        for (com.backend.gns.paiement.domain.models.Paiement p : paiements) {
            if (p.getDate() != null && p.getMontantDebite() != null) {
                int month = p.getDate().getMonthValue() - 1;
                paiementsParMois[month] = paiementsParMois[month].add(p.getMontantDebite());
            }
        }
        
        for (int i = 0; i < 12; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("mois", mois[i]);
            map.put("bourses", boursesParMois[i]); 
            map.put("remboursements", paiementsParMois[i]);
            data.add(map);
        }
        return data;
    }
}
