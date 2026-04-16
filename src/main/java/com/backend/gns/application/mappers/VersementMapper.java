package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.VersementRequest;
import com.backend.gns.application.dtos.responses.VersementResponse;
import com.backend.gns.domain.models.Versement;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VersementMapper {

    private final WalletRepository walletRepository;

    public VersementMapper(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Versement toEntity(VersementRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête VersementRequest ne peut pas être nulle");
        }

        Versement versement = new Versement();
        versement.setTrackingId(UUID.randomUUID());
        versement.setMontantVerse(request.montantVerse());
        versement.setTypeVersement(request.typeVersement());
        versement.setDateVersement(request.dateVersement() != null ? request.dateVersement().toLocalDate() : null);
        versement.setStatut(request.statut());

        if (request.trackingWalletId() != null) {
            Wallet wallet = walletRepository.findByTrackingId(request.trackingWalletId())
                .orElseThrow(() -> new IllegalArgumentException("Portefeuille non trouvé avec l'ID: " + request.trackingWalletId()));
            versement.setWallet(wallet);
        }

        return versement;
    }

    public VersementResponse toResponse(Versement versement) {
        if (versement == null) {
            throw new IllegalArgumentException("L'entité Versement ne peut pas être nulle");
        }

        return VersementResponse.builder()
                .trackingId(versement.getTrackingId())
                .montantVerse(versement.getMontantVerse())
                .typeVersement(versement.getTypeVersement())
                .dateVersement(versement.getDateVersement() != null ? versement.getDateVersement().atStartOfDay() : null)
                .statut(versement.getStatut())
                .trackingWalletId(versement.getWallet() != null ? versement.getWallet().getTrackingId() : null)
                .build();
    }

    public Versement toEntityFromResponse(VersementResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("La réponse VersementResponse ne peut pas être nulle");
        }

        Versement versement = new Versement();
        versement.setTrackingId(response.trackingId());
        versement.setMontantVerse(response.montantVerse());
        versement.setTypeVersement(response.typeVersement());
        versement.setDateVersement(response.dateVersement() != null ? response.dateVersement().toLocalDate() : null);
        versement.setStatut(response.statut());

        if (response.trackingWalletId() != null) {
            Wallet wallet = walletRepository.findByTrackingId(response.trackingWalletId())
                .orElseThrow(() -> new IllegalArgumentException("Portefeuille non trouvé avec l'ID: " + response.trackingWalletId()));
            versement.setWallet(wallet);
        }

        return versement;
    }

}
