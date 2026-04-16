package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.CommandeLigne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommandeLigneRepository extends JpaRepository<CommandeLigne, Long> {

    @Query("SELECT cl FROM CommandeLigne cl WHERE cl.trackingId = :trackingId")
    Optional<CommandeLigne> findByTrackingId(@Param("trackingId") UUID trackingId);



    @Query("SELECT cl FROM CommandeLigne cl WHERE cl.commande.trackingId = :commandeTrackingId")
    List<CommandeLigne> findByCommandeTrackingId(@Param("commandeTrackingId") UUID commandeTrackingId);

    @Query("SELECT cl FROM CommandeLigne cl WHERE cl.product.trackingId = :productTrackingId")
    List<CommandeLigne> findByProductTrackingId(@Param("productId") UUID produitTrackingId);
}
