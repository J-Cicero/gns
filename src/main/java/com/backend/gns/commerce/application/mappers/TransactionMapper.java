package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.TransactionRequest;
import com.backend.gns.commerce.application.dtos.responses.TransactionResponse;
import com.backend.gns.commerce.domain.models.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    public Transaction toEntity(TransactionRequest request) {
        if (request == null)
            return null;
        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        return transaction;
    }

    public TransactionResponse toResponse(Transaction entity) {
        if (entity == null)
            return null;

        // Résoudre senderName depuis la relation JPA (chargée grâce au JOIN FETCH dans le repository)
        String senderName = "Inconnu";
        if (entity.getSender() != null) {
            String fn = entity.getSender().getFirstName() != null ? entity.getSender().getFirstName() : "";
            String ln = entity.getSender().getLastName() != null ? entity.getSender().getLastName() : "";
            senderName = (fn + " " + ln).trim();
            if (senderName.isBlank()) senderName = "Étudiant";
        }

        // Résoudre receiverName depuis la relation JPA
        String receiverName = "Boutique inconnue";
        if (entity.getReceiver() != null && entity.getReceiver().getName() != null) {
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
                entity.getRetrievedByBoutique(),
                entity.getDeductedFromStudentBourse(),
                entity.getStatus(),
                entity.getCreatedAt());
    }
}
