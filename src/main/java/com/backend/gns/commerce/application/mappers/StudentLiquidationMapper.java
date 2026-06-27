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

        String studentName = "Unknown";
        if (entity.getTransactions() != null && !entity.getTransactions().isEmpty()) {
            com.backend.gns.student.domain.models.Student student = entity.getTransactions().get(0).getSender();
            if (student != null) {
                studentName = student.getFirstName() + " " + student.getLastName();
            }
        }

        return new StudentLiquidationResponse(
            entity.getTrackingId(),
            studentName,
            entity.getAmountDeducted(),
            entity.getCreatedAt(),
            entity.getValidatedAt(),
            entity.getStatus(),
            entity.getTransactionReference()
        );
    }
}
