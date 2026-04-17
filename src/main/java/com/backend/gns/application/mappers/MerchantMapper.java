package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.MerchantRequest;
import com.backend.gns.application.dtos.responses.MerchantResponse;
import com.backend.gns.domain.models.Merchant;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MerchantMapper {


    public Merchant toEntity(MerchantRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête MerchantRequest ne peut pas être nulle");
        }

        Merchant merchant = new Merchant();
        merchant.setTrackingId(UUID.randomUUID());
        merchant.setEmail(request.email());
        merchant.setPassword(request.password());
        merchant.setNom(request.nom());
        merchant.setPrenom(request.prenom());
        merchant.setRole(request.role());
        merchant.setEstActif(request.estActif());
        merchant.setTelephone(request.telephone());
        merchant.setDateNaissance(request.dateNaissance());
   

        return merchant;
    }

    public MerchantResponse toResponse(Merchant merchant) {
        if (merchant == null) {
            throw new IllegalArgumentException("L'entité Merchant ne peut pas être nulle");
        }

        return MerchantResponse.builder()
                .trackingId(merchant.getTrackingId())
                .email(merchant.getEmail())
                .nom(merchant.getNom())
                .prenom(merchant.getPrenom())
                .role(merchant.getRole())
                .estActif(merchant.isEstActif())
                .telephone(merchant.getTelephone())
                .dateNaissance(merchant.getDateNaissance())
                .build();
    }

    public Merchant toEntityFromResponse(MerchantResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("La réponse MerchantResponse ne peut pas être nulle");
        }

        Merchant merchant = new Merchant();
        merchant.setTrackingId(response.trackingId());
        merchant.setEmail(response.email());
        merchant.setNom(response.nom());
        merchant.setPrenom(response.prenom());
        merchant.setRole(response.role());
        merchant.setEstActif(response.estActif());
        merchant.setTelephone(response.telephone());
        merchant.setDateNaissance(response.dateNaissance());
        return merchant;
    }

}
