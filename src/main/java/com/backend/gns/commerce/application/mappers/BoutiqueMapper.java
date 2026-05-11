package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.BoutiqueRequest;
import com.backend.gns.commerce.application.dtos.responses.BoutiqueResponse;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.Shared.wallet.domain.models.Wallet;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.Shared.wallet.infrastructure.repositories.WalletRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class BoutiqueMapper {

  private final MerchantRepository merchantRepository;
  private final WalletRepository walletRepository;

  public BoutiqueMapper(
      BoutiqueRepository boutiqueRepository,
      MerchantRepository merchantRepository,
      WalletRepository walletRepository) {
    this.merchantRepository = merchantRepository;
    this.walletRepository = walletRepository;
  }

  public Boutique toEntity(BoutiqueRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("La requête BoutiqueRequest ne peut pas être nulle");
    }

    Boutique boutique = new Boutique();
    boutique.setTrackingId(UUID.randomUUID());
    boutique.setNomBoutique(request.nomBoutique());
    boutique.setCategorieShop(request.categorieShop());
    boutique.setStatutKYC(request.statutKYC());
    boutique.setLatitude(request.latitude());
    boutique.setLongitude(request.longitude());
    boutique.setCheminCarteEDJ(request.cheminCarteEDJ());

    if (request.merchantTrackingId() != null) {
      Merchant merchant =
          merchantRepository
              .findByTrackingId(request.merchantTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Commerçant non trouvé avec l'ID: " + request.merchantTrackingId()));
      boutique.setMerchant(merchant);
    }

    if (request.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
      boutique.setWallet(wallet);
    }

    return boutique;
  }

  public BoutiqueResponse toResponse(Boutique boutique) {
    if (boutique == null) {
      throw new IllegalArgumentException("L'entité Boutique ne peut pas être nulle");
    }

    return BoutiqueResponse.builder()
        .trackingId(boutique.getTrackingId())
        .nomBoutique(boutique.getNomBoutique())
        .categorieShop(boutique.getCategorieShop())
        .cheminCarteEDJ(boutique.getCheminCarteEDJ())
        .statutKYC(boutique.getStatutKYC())
        .latitude(boutique.getLatitude())
        .longitude(boutique.getLongitude())
        .merchantTrackingId(
            boutique.getMerchant() != null ? boutique.getMerchant().getTrackingId() : null)
        .walletTrackingId(
            boutique.getWallet() != null ? boutique.getWallet().getTrackingId() : null)
        .build();
  }

  public Boutique toEntityFromResponse(BoutiqueResponse response) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse BoutiqueResponse ne peut pas être nulle");
    }

    Boutique boutique = new Boutique();
    boutique.setTrackingId(response.trackingId());
    boutique.setNomBoutique(response.nomBoutique());
    boutique.setCategorieShop(response.categorieShop());
    boutique.setStatutKYC(response.statutKYC());
    boutique.setLatitude(response.latitude());
    boutique.setLongitude(response.longitude());

    if (response.merchantTrackingId() != null) {
      Merchant merchant =
          merchantRepository
              .findByTrackingId(response.merchantTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Commerçant non trouvé avec l'ID: " + response.merchantTrackingId()));
      boutique.setMerchant(merchant);
    }

    if (response.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(response.walletTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Portefeuille non trouvé avec l'ID: " + response.walletTrackingId()));
      boutique.setWallet(wallet);
    }

    return boutique;
  }
}
