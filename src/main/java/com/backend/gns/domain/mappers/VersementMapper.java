package com.backend.gns.domain.mappers;

import com.backend.gns.domain.dtos.requests.VersementRequest;
import com.backend.gns.domain.dtos.responses.VersementResponse;
import com.backend.gns.domain.models.Versement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class VersementMapper {

    public Versement toEntity(VersementRequest request) {
        if (request == null) {
            return null;
        }

        Versement versement = new Versement();
        versement.setTrackingId(UUID.randomUUID());
        versement.setWalletTrackingId(request.walletTrackingId());
        versement.setMontantVerse(request.montantVerse());
        versement.setTypeVersement(com.backend.gns.domain.enums.VersementType.valueOf(request.typeVersement()));
        versement.setDatePrevue(request.datePrevue());
        versement.setStatut(com.backend.gns.domain.enums.VersementStatut.PROGRAMME);

        return versement;
    }

    public VersementResponse toResponse(Versement entity) {
        if (entity == null) {
            return null;
        }

        return new VersementResponse(
                entity.getTrackingId(),
                entity.getWalletTrackingId(),
                entity.getMontantVerse(),
                entity.getTypeVersement() != null ? entity.getTypeVersement().name() : null,
                entity.getDatePrevue(),
                entity.getDateEffective(),
                entity.getStatut() != null ? entity.getStatut().name() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<VersementResponse> toResponseList(List<Versement> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
