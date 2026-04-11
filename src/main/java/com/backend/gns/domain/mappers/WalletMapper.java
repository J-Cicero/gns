package com.backend.gns.domain.mappers;

import com.backend.gns.domain.dtos.requests.WalletRequest;
import com.backend.gns.domain.dtos.responses.WalletResponse;
import com.backend.gns.domain.enums.WalletType;
import com.backend.gns.domain.models.Wallet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WalletMapper {

    public Wallet toEntity(WalletRequest request) {
        if (request == null) {
            return null;
        }

        Wallet wallet = new Wallet();
        wallet.setTrackingId(UUID.randomUUID());
        wallet.setStudentTrackingId(request.studentTrackingId());
        wallet.setTypeWallet(WalletType.valueOf(request.typeWallet()));
        wallet.setSolde(request.solde());
        wallet.setPlafond(request.plafond());
        wallet.setEstVerrouille(false);
        wallet.setDateCreation(LocalDate.now());

        return wallet;
    }

    public WalletResponse toResponse(Wallet entity) {
        if (entity == null) {
            return null;
        }

        return new WalletResponse(
                entity.getTrackingId(),
                entity.getStudentTrackingId(),
                entity.getTypeWallet().name(),
                entity.getSolde(),
                entity.getPlafond(),
                entity.getEstVerrouille(),
                entity.getDateCreation(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<WalletResponse> toResponseList(List<Wallet> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
