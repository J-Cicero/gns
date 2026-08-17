package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.responses.StudentLiquidationResponse;
import com.backend.gns.commerce.domain.models.StudentLiquidation;
import org.springframework.stereotype.Component;

@Component
public class StudentLiquidationMapper {

    public StudentLiquidationResponse toResponse(StudentLiquidation entity) {
        if (entity == null) {
            return null;
        }

        String studentName = "Inconnu";
        java.util.UUID studentTrackingId = null;
        if (entity.getStudent() != null) {
            studentName = (entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName()).trim();
            studentTrackingId = entity.getStudent().getTrackingId();
        }

        return new StudentLiquidationResponse(
            entity.getTrackingId(),
            studentTrackingId,
            studentName,
            entity.getAmountDeducted(),
            entity.getCreatedAt(),
            entity.getValidatedAt(),
            entity.getStatus()
        );
    }
}
