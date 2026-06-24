package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.BoutiqueRequest;
import com.backend.gns.commerce.application.dtos.responses.BoutiqueResponse;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BoutiqueMapper {

  private final MerchantRepository merchantRepository;
  private final WalletRepository walletRepository;
  private final ParametreGnsService parametreGnsService;

  public Boutique toEntity(BoutiqueRequest request) {
    if (request == null) return null;

    Boutique boutique = new Boutique();
    boutique.setTrackingId(UUID.randomUUID());
    boutique.setName(request.name());
    boutique.setDescription(request.description());
    boutique.setKycStatus(request.kycStatus());
    boutique.setLatitude(request.latitude());
    boutique.setLongitude(request.longitude());

    if (request.merchantTrackingId() != null) {
      merchantRepository.findByTrackingId(request.merchantTrackingId())
          .ifPresent(boutique::setMerchant);
    }

    if (request.walletTrackingId() != null) {
      walletRepository.findByTrackingId(request.walletTrackingId())
          .ifPresent(boutique::setWallet);
    }

    return boutique;
  }

  public BoutiqueResponse toResponse(Boutique boutique) {
    if (boutique == null) return null;

    java.math.BigDecimal defaultLimit = boutique.getWallet() != null ? boutique.getWallet().getLimitAmount() : java.math.BigDecimal.ZERO;
    java.math.BigDecimal limitAmount = parametreGnsService.findByNomParametre(TypeParametreGns.MAJORATION_MONTANT_BOUTIQUE)
            .map(p -> new java.math.BigDecimal(p.valeurParametre()))
            .orElse(defaultLimit);

    return BoutiqueResponse.builder()
        .trackingId(boutique.getTrackingId())
        .name(boutique.getName())
        .description(boutique.getDescription())
        .kycStatus(boutique.getKycStatus())
        .latitude(boutique.getLatitude())
        .longitude(boutique.getLongitude())
        .merchantTrackingId(boutique.getMerchant() != null ? boutique.getMerchant().getTrackingId() : null)
        .walletTrackingId(boutique.getWallet() != null ? boutique.getWallet().getTrackingId() : null)
        .balance(boutique.getWallet() != null ? boutique.getWallet().getBalance() : java.math.BigDecimal.ZERO)
        .limitAmount(limitAmount)
        .walletStatus(boutique.getWallet() != null && boutique.getWallet().getStatus() != null ? boutique.getWallet().getStatus().name() : null)
        .build();
  }
}
