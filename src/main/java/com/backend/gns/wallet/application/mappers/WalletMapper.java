package com.backend.gns.wallet.application.mappers;

import com.backend.gns.wallet.application.dtos.requests.WalletRequest;
import com.backend.gns.wallet.application.dtos.responses.WalletResponse;
import com.backend.gns.wallet.domain.models.Wallet;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

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
    wallet.setLimitAmount(request.limitAmount());
    wallet.setCurrency(request.currency() != null ? request.currency() : "XAF");
    wallet.setCreatedAt(
        request.createdAt() != null ? request.createdAt() : LocalDateTime.now());

    return wallet;
  }

  public WalletResponse toResponse(Wallet wallet) {
    if (wallet == null) {
      return null;
    }

    return WalletResponse.builder()
        .trackingId(wallet.getTrackingId())
        .walletType(wallet.getWalletType())
        .status(wallet.getStatus())
        .fundingLevel(wallet.getFundingLevel())
        .balance(wallet.getBalance())
        .limitAmount(wallet.getLimitAmount())
        .currency(wallet.getCurrency())
        .createdAt(wallet.getCreatedAt())
        .studentTrackingId(wallet.getStudent() != null ? wallet.getStudent().getTrackingId() : null)
        .build();
  }
}
