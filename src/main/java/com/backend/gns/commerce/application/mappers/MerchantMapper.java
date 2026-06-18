package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.MerchantRequest;
import com.backend.gns.commerce.application.dtos.responses.MerchantResponse;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.user.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MerchantMapper {

  public Merchant toEntity(MerchantRequest request) {
    if (request == null) return null;

    Merchant merchant = new Merchant();
    merchant.setTrackingId(UUID.randomUUID());
    merchant.setEmail(request.email());
    merchant.setLastName(request.lastName());
    merchant.setFirstName(request.firstName());
    merchant.setRole(UserRole.COMMERCANT);
    merchant.setActive(request.isActive() != null ? request.isActive() : true);
    merchant.setBusinessName(request.businessName());
    merchant.setPhoneNumber(request.phoneNumber());
    merchant.setBirthDate(request.birthDate());
    return merchant;
  }

  public MerchantResponse toResponse(Merchant merchant) {
    if (merchant == null) return null;

    return MerchantResponse.builder()
        .trackingId(merchant.getTrackingId())
        .email(merchant.getEmail())
        .lastName(merchant.getLastName())
        .firstName(merchant.getFirstName())
        .isActive(merchant.isActive())
        .phoneNumber(merchant.getPhoneNumber())
        .birthDate(merchant.getBirthDate())
        .businessName(merchant.getBusinessName())
        .build();
  }
}
