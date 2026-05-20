package com.backend.gns.admin.application.mappers;

import com.backend.gns.admin.application.dtos.requests.BankOperatorRequest;
import com.backend.gns.admin.application.dtos.responses.BankOperatorResponse;
import com.backend.gns.admin.domain.models.BankOperator;
import com.backend.gns.Shared.wallet.domain.models.Wallet;
import com.backend.gns.Shared.user.domain.enums.UserRole;
import com.backend.gns.Shared.wallet.infrastructure.repositories.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class BankOperatorMapper {

    private final WalletRepository walletRepository;

    public BankOperator toEntity(BankOperatorRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête BankOperatorRequest ne peut pas être nulle");
        }

        BankOperator bankOperator = new BankOperator();
        bankOperator.setTrackingId(UUID.randomUUID());
        bankOperator.setEmail(request.email());
        bankOperator.setPassword(request.password());
        bankOperator.setNom(request.nom());
        bankOperator.setPrenom(request.prenom());
        bankOperator.setRole(UserRole.ADMIN_BANQUE);
        bankOperator.setEstActif(request.estActif());
        bankOperator.setTelephone(request.telephone());


        if (request.walletTrackingId() != null) {
            Wallet wallet = walletRepository.findByTrackingId(request.walletTrackingId())
                    .orElseThrow(() -> new IllegalArgumentException("Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
            bankOperator.setWallet(wallet);
        }

        return bankOperator;
    }

    public BankOperatorResponse toResponse(BankOperator bankOperator) {
        if (bankOperator == null) {
            throw new IllegalArgumentException("L'entité BankOperator ne peut pas être nulle");
        }

        return BankOperatorResponse.builder()
                .trackingId(bankOperator.getTrackingId())
                .email(bankOperator.getEmail())
                .nom(bankOperator.getNom())
                .prenom(bankOperator.getPrenom())
                .estActif(bankOperator.isEstActif())
                .telephone(bankOperator.getTelephone())
                .walletTrackingId(bankOperator.getWallet() != null ? bankOperator.getWallet().getTrackingId() : null)
                .build();
    }
}
