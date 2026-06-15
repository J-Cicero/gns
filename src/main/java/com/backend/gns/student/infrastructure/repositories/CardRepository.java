package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.enums.CardStatut;
import com.backend.gns.student.domain.models.Card;
import com.backend.gns.wallet.domain.models.Wallet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

  Optional<Card> findByTrackingId(UUID trackingId);

  List<Card> findByWallet_TrackingId(UUID walletTrackingId);

  Optional<Card> findByQrCodeData(String qrCodeData);

  Optional<Card> findByWalletAndStatus(Wallet wallet, CardStatut status);

  Page<Card> findByWallet(Wallet wallet, Pageable pageable);

  Page<Card> findByStatus(CardStatut status, Pageable pageable);

  Long countByWalletAndStatus(Wallet wallet, CardStatut status);
}
