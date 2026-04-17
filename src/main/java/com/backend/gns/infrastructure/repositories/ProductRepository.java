package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("SELECT p FROM Product p WHERE p.boutique.trackingId = :boutiqueTrackingId")
  Page<Product> findByBoutiqueTrackingId(
      @Param("boutiqueTrackingId") UUID boutiqueTrackingId, Pageable pageable);

  Optional<Product> findByNom(String nom);

  Page<Product> findByEstDisponible(Boolean disponible, Pageable pageable);

  Optional<Product> findByTrackingId(UUID trackingId);
}
