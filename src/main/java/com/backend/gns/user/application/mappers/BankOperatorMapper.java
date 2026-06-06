package com.backend.gns.user.application.mappers;

import com.backend.gns.core.domain.models.Banque;
import com.backend.gns.core.infrastructure.repositories.BanqueRepository;
import com.backend.gns.user.application.dtos.requests.BankOperatorRequest;
import com.backend.gns.user.application.dtos.responses.BankOperatorResponse;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.user.domain.models.BankOperator;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BankOperatorMapper {

  private final WalletRepository walletRepository;
  private final BanqueRepository banqueRepository;

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
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
      bankOperator.setWallet(wallet);
    }

    if (request.banquePartenaireTrackingId() != null) {
      Banque banque =
          banqueRepository
              .findByTrackingId(request.banquePartenaireTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Banque non trouvée avec l'ID: " + request.banquePartenaireTrackingId()));
      bankOperator.setBanquePartenaire(banque);
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
        .walletTrackingId(
            bankOperator.getWallet() != null ? bankOperator.getWallet().getTrackingId() : null)
        .banquePartenaireTrackingId(
            bankOperator.getBanquePartenaire() != null
                ? bankOperator.getBanquePartenaire().getTrackingId()
                : null)
        .build();
  }
}
