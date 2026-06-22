package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.TransactionRequest;
import com.backend.gns.commerce.application.dtos.responses.TransactionResponse;
import com.backend.gns.commerce.domain.models.Transaction;
import com.backend.gns.commerce.domain.services.BoutiqueService;
import com.backend.gns.student.domain.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final StudentService studentService; // To get senderName
    private final BoutiqueService boutiqueService; // To get receiverName

    public Transaction toEntity(TransactionRequest request) {
        if (request == null) return null;
        Transaction transaction = new Transaction();
        // The sender and receiver entities will be set in the service layer
        // via their tracking IDs.
        transaction.setAmount(request.amount());
        transaction.setIsCommissionPaid(request.isCommissionPaid() != null ? request.isCommissionPaid() : false);
        transaction.setIsRetry(request.isRetry() != null ? request.isRetry() : false);
        return transaction;
    }

    public TransactionResponse toResponse(Transaction entity) {
        if (entity == null) return null;

        String senderName = "Unknown";
        if (entity.getSender() != null) {
            senderName = entity.getSender().getFirstName() + " " + entity.getSender().getLastName();
        }

        String receiverName = "Unknown";
        if (entity.getReceiver() != null) {
            receiverName = entity.getReceiver().getName();
        }

        return new TransactionResponse(
            entity.getTrackingId(),
            entity.getSender() != null ? entity.getSender().getTrackingId() : null,
            entity.getReceiver() != null ? entity.getReceiver().getTrackingId() : null,
            senderName,
            receiverName,
            entity.getAmount(),
            entity.getAmountDebited(),
            entity.getAmountCredited(),
            entity.getTotalCommission(),
            entity.getGnsCommission(),
            entity.getBankCommission(),
            entity.getIsCommissionPaid(),
            entity.getIsRetry(),
            entity.getRetrievedByBoutique(),
            entity.getDeductedFromStudentBourse(),
            entity.getStatus(),
            entity.getCreatedAt()
        );
    }
}
