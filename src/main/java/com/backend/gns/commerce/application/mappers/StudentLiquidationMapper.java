package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.responses.StudentLiquidationResponse;
import com.backend.gns.commerce.domain.models.StudentLiquidation;
import com.backend.gns.core.parametrage.infrastructure.repositories.CompteBancaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentLiquidationMapper {

    private final CompteBancaireRepository compteBancaireRepository;

    public StudentLiquidationResponse toResponse(StudentLiquidation entity) {
        if (entity == null) {
            return null;
        }

        String studentName = "Inconnu";
        String studentNumber = null;
        String accountNumber = "Non renseigné";
        java.util.UUID studentTrackingId = null;
        if (entity.getStudent() != null) {
            studentName = (entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName()).trim();
            studentNumber = entity.getStudent().getStudenNumber();
            studentTrackingId = entity.getStudent().getTrackingId();
            if (compteBancaireRepository != null && studentTrackingId != null) {
                accountNumber = compteBancaireRepository.findByProprietaireTrackingId(studentTrackingId)
                        .map(cb -> cb.getAccountNumber())
                        .orElse("Non renseigné");
            }
        }

        return new StudentLiquidationResponse(
            entity.getTrackingId(),
            studentTrackingId,
            studentName,
            studentNumber,
            accountNumber,
            entity.getAmountDeducted(),
            entity.getCreatedAt(),
            entity.getValidatedAt(),
            entity.getStatus()
        );
    }
}
