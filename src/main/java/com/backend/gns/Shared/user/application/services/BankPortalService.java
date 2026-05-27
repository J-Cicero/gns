package com.backend.gns.Shared.user.application.services;

import com.backend.gns.student.domain.models.Student;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BankPortalService {
    
    record StudentLiquidationInfo(
        UUID studentTrackingId,
        String nom,
        String prenom,
        String numEtudiant,
        BigDecimal bourseTotale,
        BigDecimal depensesStudCash,
        BigDecimal resteAPayer,
        boolean virementEffectue
    ) {}

    List<StudentLiquidationInfo> getStudentsForBank(UUID bankOperatorTrackingId);
    
    void validerMandat(UUID studentTrackingId, boolean valide);
    
    void marquerTraite(UUID studentTrackingId);
}
