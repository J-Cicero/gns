package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.CommandeLigne;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CommandeLigneRepository extends JpaRepository<CommandeLigne, Long> {

    Optional<CommandeLigne> findByTrackingId(UUID trackingId);

    @Query("SELECT cl FROM CommandeLigne cl WHERE cl.commande.trackingId = :commandeTrackingId")
    Page<CommandeLigne> findByCommandeTrackingId(@Param("commandeTrackingId") UUID commandeTrackingId, Pageable pageable);

    @Query("SELECT cl FROM CommandeLigne cl WHERE cl.product.trackingId = :productTrackingId")
    Page<CommandeLigne> findByProductTrackingId(@Param("productTrackingId") UUID productTrackingId, Pageable pageable);
}
