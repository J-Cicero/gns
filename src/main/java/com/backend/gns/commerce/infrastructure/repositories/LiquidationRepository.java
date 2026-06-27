package com.backend.gns.commerce.infrastructure.repositories;

import com.backend.gns.commerce.domain.models.Liquidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LiquidationRepository extends JpaRepository<Liquidation, Long> {
    Optional<Liquidation> findByTrackingId(UUID trackingId);
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT l FROM Transaction t JOIN t.liquidation l WHERE t.receiver.trackingId = :boutiqueTrackingId")
    List<Liquidation> findByBoutiqueTrackingId(@org.springframework.data.repository.query.Param("boutiqueTrackingId") UUID boutiqueTrackingId);
}
