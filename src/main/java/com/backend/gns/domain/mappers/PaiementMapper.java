package com.backend.gns.domain.mappers;

import com.backend.gns.domain.dtos.requests.PaiementRequest;
import com.backend.gns.domain.dtos.responses.PaiementResponse;
import com.backend.gns.domain.enums.PaiementStatut;
import com.backend.gns.domain.enums.PaiementType;
import com.backend.gns.domain.models.Paiement;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PaiementMapper {

    public Paiement toEntity(PaiementRequest request) {
        if (request == null) {
            return null;
        }

        Paiement paiement = new Paiement();
        paiement.setTrackingId(UUID.randomUUID());
        paiement.setCommandeTrackingId(request.commandeTrackingId());
        paiement.setWalletTrackingId(request.walletTrackingId());
        paiement.setMontantProduit(request.montantProduit());
        paiement.setCommission(request.commission());
        paiement.setMontantDebite(request.montantDebite());
        paiement.setDateTimestamp(LocalDateTime.now());
        paiement.setTypePaiement(PaiementType.valueOf(request.typePaiement()));
        paiement.setStatutPaiement(PaiementStatut.EN_COURS);
        paiement.setEstSwitch(request.estSwitch());
        paiement.setCommandeRef(request.commandeTrackingId().toString());

        return paiement;
    }

    public PaiementResponse toResponse(Paiement entity) {
        if (entity == null) {
            return null;
        }

        return new PaiementResponse(
                entity.getTrackingId(),
                entity.getCommandeTrackingId(),
                entity.getWalletTrackingId(),
                entity.getMontantProduit(),
                entity.getCommission(),
                entity.getMontantDebite(),
                entity.getDateTimestamp(),
                entity.getTypePaiement().name(),
                entity.getStatutPaiement().name(),
                entity.getEstSwitch(),
                entity.getCommandeRef(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<PaiementResponse> toResponseList(List<Paiement> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
