package com.backend.gns.commerce.infrastructure.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.gns.commerce.domain.models.CommandeLigne;

public interface CommandeLigneRepository extends JpaRepository<CommandeLigne, Long> {

  Optional<CommandeLigne> findByTrackingId(UUID trackingId);

  @Query("SELECT cl FROM CommandeLigne cl WHERE cl.commande.trackingId = :commandeTrackingId")
  Page<CommandeLigne> findByCommandeTrackingId(
      @Param("commandeTrackingId") UUID commandeTrackingId, Pageable pageable);

  @Query("SELECT cl FROM CommandeLigne cl WHERE cl.product.trackingId = :productTrackingId")
  Page<CommandeLigne> findByProductTrackingId(
      @Param("productTrackingId") UUID productTrackingId, Pageable pageable);
}
