package com.backend.gns.admin.application.mappers;

import com.backend.gns.admin.application.dtos.requests.AdminULRequest;
import com.backend.gns.admin.application.dtos.responses.AdminULResponse;
import com.backend.gns.admin.domain.models.AdminUL;
import com.backend.gns.Shared.wallet.domain.models.Wallet;
import com.backend.gns.Shared.wallet.infrastructure.repositories.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class AdminULMapper {

    private final WalletRepository walletRepository;

    public AdminUL toEntity(AdminULRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête AdminULRequest ne peut pas être nulle");
        }

        AdminUL adminUL = new AdminUL();
        adminUL.setTrackingId(UUID.randomUUID());
        adminUL.setEmail(request.email());
        adminUL.setPassword(request.password());
        adminUL.setNom(request.nom());
        adminUL.setPrenom(request.prenom());
        adminUL.setRole(request.role());
        adminUL.setEstActif(request.estActif());
        adminUL.setTelephone(request.telephone());
        adminUL.setNumeroCompte(request.numeroCompte());
        if (request.walletTrackingId() != null) {
            Wallet wallet = walletRepository.findByTrackingId(request.walletTrackingId())
                    .orElseThrow(() -> new IllegalArgumentException("Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
            adminUL.setWallet(wallet);
        }

        return adminUL;
    }

    public AdminULResponse toResponse(AdminUL adminUL) {
        if (adminUL == null) {
            throw new IllegalArgumentException("L'entité AdminUL ne peut pas être nulle");
        }

        return AdminULResponse.builder()
                .trackingId(adminUL.getTrackingId())
                .email(adminUL.getEmail())
                .nom(adminUL.getNom())
                .prenom(adminUL.getPrenom())
                .role(adminUL.getRole())
                .estActif(adminUL.isEstActif())
                .telephone(adminUL.getTelephone())
                .numeroCompte(adminUL.getNumeroCompte())
                .walletTrackingId(adminUL.getWallet() != null ? adminUL.getWallet().getTrackingId() : null)
                .build();
    }
}
