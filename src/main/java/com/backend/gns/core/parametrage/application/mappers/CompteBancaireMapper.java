package com.backend.gns.core.parametrage.application.mappers;

import com.backend.gns.core.parametrage.application.dtos.requests.CompteBancaireRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.CompteBancaireResponse;
import com.backend.gns.core.parametrage.domain.models.Banque;
import com.backend.gns.core.parametrage.domain.models.CompteBancaire;
import com.backend.gns.core.parametrage.domain.models.DocumentBanque; // Nouvel import important !
import com.backend.gns.core.parametrage.infrastructure.repositories.BanqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompteBancaireMapper {

    private final BanqueRepository banqueRepository;

    public CompteBancaire toEntity(CompteBancaireRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CompteBancaireRequest cannot be null");
        }

        CompteBancaire compte = new CompteBancaire();
        compte.setTrackingId(UUID.randomUUID());
        compte.setAccountNumber(request.accountNumber());
        compte.setTransferCompleted(false);

        // Résolution de la banque
        if (request.bankTrackingId() != null) {
            Banque banque = banqueRepository.findByTrackingId(request.bankTrackingId())
                    .orElseThrow(() -> new IllegalArgumentException("Banque introuvable avec trackingId: " + request.bankTrackingId()));
            compte.setBank(banque);
        }

        return compte;
    }

    public CompteBancaireResponse toResponse(CompteBancaire entity, DocumentBanque ribDocument) {
        if (entity == null) {
            return null;
        }

        return CompteBancaireResponse.builder()
                .trackingId(entity.getTrackingId())
                .accountNumber(entity.getAccountNumber())
                .bankName(entity.getBank() != null ? entity.getBank().getName() : null)
                .ownerType(entity.getOwnerType())

                .ribUrl(ribDocument != null ? ribDocument.getFileUrl() : null)
                .ribDocumentTrackingId(ribDocument != null ? ribDocument.getTrackingId() : null)
                .build();
    }
}