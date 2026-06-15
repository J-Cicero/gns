package com.backend.gns.commerce.infrastructure.repositories;

import com.backend.gns.commerce.domain.models.Product;
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

  Optional<Product> findByName(String name);

  Page<Product> findByIsAvailable(Boolean isAvailable, Pageable pageable);

  Optional<Product> findByTrackingId(UUID trackingId);
}
