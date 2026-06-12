package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.MerchantRequest;
import com.backend.gns.commerce.application.dtos.responses.MerchantResponse;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.user.domain.enums.UserRole;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MerchantMapper {

  public Merchant toEntity(MerchantRequest request) {
    if (request == null) return null;

    Merchant merchant = new Merchant();
    merchant.setTrackingId(UUID.randomUUID());
    merchant.setEmail(request.email());
    merchant.setNom(request.nom());
    merchant.setPrenom(request.prenom());
    merchant.setRole(UserRole.COMMERCANT);
    merchant.setEstActif(true);
    return merchant;
  }

  public MerchantResponse toResponse(Merchant merchant) {
    if (merchant == null) return null;

    return MerchantResponse.builder()
        .trackingId(merchant.getTrackingId())
        .email(merchant.getEmail())
        .nom(merchant.getNom())
        .prenom(merchant.getPrenom())
        .estActif(merchant.isEstActif())
        .build();
  }
}
