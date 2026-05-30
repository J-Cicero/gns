package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.UniversityAdminRequest;
import com.backend.gns.student.application.dtos.responses.UniversityAdminResponse;
import com.backend.gns.student.domain.models.UniversityAdmin;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.student.domain.models.Universite;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UniversityAdminMapper {

    private final WalletRepository walletRepository;
    private final UniversiteRepository universiteRepository;

    public UniversityAdmin toEntity(UniversityAdminRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête UniversityAdminRequest ne peut pas être nulle");
        }

        UniversityAdmin universityAdmin = new UniversityAdmin();
        universityAdmin.setTrackingId(UUID.randomUUID());
        universityAdmin.setEmail(request.email());
        universityAdmin.setPassword(request.password());
        universityAdmin.setNom(request.nom());
        universityAdmin.setPrenom(request.prenom());
        universityAdmin.setRole(UserRole.UNIVERSITY_ADMIN);
        universityAdmin.setEstActif(request.estActif());
        universityAdmin.setTelephone(request.telephone());
        universityAdmin.setNumeroCompte(request.numeroCompte());
        
        if (request.walletTrackingId() != null) {
            Wallet wallet = walletRepository.findByTrackingId(request.walletTrackingId())
                    .orElseThrow(() -> new IllegalArgumentException("Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
            universityAdmin.setWallet(wallet);
        }
        
        if (request.universiteTrackingId() != null) {
            Universite universite = universiteRepository.findByTrackingId(request.universiteTrackingId())
                    .orElseThrow(() -> new IllegalArgumentException("Université non trouvée avec l'ID: " + request.universiteTrackingId()));
            universityAdmin.setUniversite(universite);
        }

        return universityAdmin;
    }

    public UniversityAdminResponse toResponse(UniversityAdmin universityAdmin) {
        if (universityAdmin == null) {
            throw new IllegalArgumentException("L'entité UniversityAdmin ne peut pas être nulle");
        }

        return UniversityAdminResponse.builder()
                .trackingId(universityAdmin.getTrackingId())
                .email(universityAdmin.getEmail())
                .nom(universityAdmin.getNom())
                .prenom(universityAdmin.getPrenom())
                .estActif(universityAdmin.isEstActif())
                .telephone(universityAdmin.getTelephone())
                .numeroCompte(universityAdmin.getNumeroCompte())
                .walletTrackingId(universityAdmin.getWallet() != null ? universityAdmin.getWallet().getTrackingId() : null)
                .universiteTrackingId(universityAdmin.getUniversite() != null ? universityAdmin.getUniversite().getTrackingId() : null)
                .build();
    }
}
