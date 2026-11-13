package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.TransactionRequest;
import com.backend.gns.commerce.application.dtos.responses.TransactionResponse;
import com.backend.gns.commerce.domain.models.Transaction;
import com.backend.gns.core.parametrage.domain.models.CompteBancaire;
import com.backend.gns.core.parametrage.infrastructure.repositories.CompteBancaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final CompteBancaireRepository compteBancaireRepository;

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

        // Résoudre senderName, matricule et banque depuis la relation JPA
        String senderName = "Inconnu";
        String studentMatricule = null;
        UUID bankTrackingId = null;
        String bankName = null;
        String bankCode = null;

        if (entity.getSender() != null) {
            String fn = entity.getSender().getFirstName() != null ? entity.getSender().getFirstName() : "";
            String ln = entity.getSender().getLastName() != null ? entity.getSender().getLastName() : "";
            senderName = (fn + " " + ln).trim();
            if (senderName.isBlank()) senderName = "Étudiant";
            studentMatricule = entity.getSender().getStudenNumber();

            if (entity.getSender().getTrackingId() != null) {
                CompteBancaire compte = compteBancaireRepository
                        .findByProprietaireTrackingId(entity.getSender().getTrackingId())
                        .orElse(null);
                if (compte != null && compte.getBank() != null) {
                    bankTrackingId = compte.getBank().getTrackingId();
                    bankName = compte.getBank().getName();
                    bankCode = compte.getBank().getCode();
                }
            }
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
                studentMatricule,
                bankTrackingId,
                bankName,
                bankCode,
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
