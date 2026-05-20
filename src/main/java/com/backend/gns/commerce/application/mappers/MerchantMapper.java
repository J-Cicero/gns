package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.MerchantRequest;
import com.backend.gns.commerce.application.dtos.responses.MerchantResponse;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.Shared.user.domain.enums.UserRole;
import java.util.UUID;
import org.springframework.stereotype.Component;

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
    merchant.setRole(UserRole.COMMERCANT);
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
    merchant.setRole(UserRole.COMMERCANT);
    merchant.setEstActif(response.estActif());
    merchant.setTelephone(response.telephone());
    merchant.setDateNaissance(response.dateNaissance());
    return merchant;
  }
}
