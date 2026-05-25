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
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalBoutiques", boutiqueRepository.count());
        stats.put("totalTransactions", paiementRepository.count());
        
        // Simuler les bourses du jour
        stats.put("transactionsToday", 0); 
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getFluxMensuel() {
        List<Map<String, Object>> data = new ArrayList<>();
        String[] mois = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
        
        for (String m : mois) {
            Map<String, Object> map = new HashMap<>();
            map.put("mois", m);
            map.put("bourses", 0); 
            map.put("remboursements", 0);
            data.add(map);
        }
        return data;
    }
}
