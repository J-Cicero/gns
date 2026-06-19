package com.backend.gns.wallet.application.mappers;

import com.backend.gns.wallet.application.dtos.requests.WalletRequest;
import com.backend.gns.wallet.application.dtos.responses.WalletResponse;
import com.backend.gns.wallet.domain.models.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class WalletMapper {

  public Wallet toEntity(WalletRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("WalletRequest cannot be null");
    }

    Wallet wallet = new Wallet();
    wallet.setTrackingId(UUID.randomUUID());
    wallet.setWalletType(request.walletType());
    wallet.setStatus(request.status());
    wallet.setBalance(request.balance());
    wallet.setLimitAmount(BigDecimal.valueOf(1000));

    return wallet;
  }

  public WalletResponse toResponse(Wallet wallet) {
    if (wallet == null) {
      return null;
    }

    String ownerName = "Inconnu";
    if (wallet.getStudent() != null) {
        ownerName = wallet.getStudent().getFirstName() + " " + wallet.getStudent().getLastName();
    }

    return WalletResponse.builder()
        .trackingId(wallet.getTrackingId())
        .walletType(wallet.getWalletType())
        .status(wallet.getStatus())
        .fundingLevel(wallet.getFundingLevel())
        .balance(wallet.getBalance())
        .limitAmount(wallet.getLimitAmount())
        .studentTrackingId(wallet.getStudent() != null ? wallet.getStudent().getTrackingId() : null)
        .build();
  }
}
