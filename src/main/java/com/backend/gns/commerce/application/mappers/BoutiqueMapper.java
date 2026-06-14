package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.BoutiqueRequest;
import com.backend.gns.commerce.application.dtos.responses.BoutiqueResponse;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoutiqueMapper {

  private final MerchantRepository merchantRepository;
  private final WalletRepository walletRepository;

  public Boutique toEntity(BoutiqueRequest request) {
    if (request == null) return null;

    Boutique boutique = new Boutique();
    boutique.setTrackingId(UUID.randomUUID());
    boutique.setNomBoutique(request.nomBoutique());
    boutique.setCategorieShop(request.categorieShop());
    boutique.setStatutKYC(request.statutKYC());
    boutique.setLatitude(request.latitude());
    boutique.setLongitude(request.longitude());
    boutique.setCheminCarteEDJ(request.cheminCarteEDJ());

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

    return BoutiqueResponse.builder()
        .trackingId(boutique.getTrackingId())
        .nomBoutique(boutique.getNomBoutique())
        .categorieShop(boutique.getCategorieShop())
        .cheminCarteEDJ(boutique.getCheminCarteEDJ())
        .statutKYC(boutique.getStatutKYC())
        .latitude(boutique.getLatitude())
        .longitude(boutique.getLongitude())
        .merchantTrackingId(boutique.getMerchant() != null ? boutique.getMerchant().getTrackingId() : null)
        .walletTrackingId(boutique.getWallet() != null ? boutique.getWallet().getTrackingId() : null)
        .solde(boutique.getWallet() != null ? boutique.getWallet().getSolde() : java.math.BigDecimal.ZERO)
        .plafond(boutique.getWallet() != null ? boutique.getWallet().getPlafond() : java.math.BigDecimal.ZERO)
        .build();
  }
}
