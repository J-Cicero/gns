package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.merchant.id = :merchantId")
    List<Product> findByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT p FROM Product p WHERE p.nom = :nom")
    Optional<Product> findByNom(@Param("nom") String nom);

    @Query("SELECT p FROM Product p WHERE p.estDisponible = :disponible")
    List<Product> findByEstDisponible(@Param("disponible") Boolean disponible);

    @Query("SELECT p FROM Product p WHERE p.trackingId = :trackingId")
    Optional<Product> findByTrackingId(@Param("trackingId") UUID trackingId);
}
