package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Merchant;
import com.backend.gns.domain.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    @Query("SELECT m FROM Merchant m WHERE m.email = :email")
    Optional<Merchant> findByEmail(@Param("email") String email);

    @Query("SELECT m FROM Merchant m WHERE m.nomBoutique = :nomBoutique")
    Optional<Merchant> findByNomBoutique(@Param("nomBoutique") String nomBoutique);

    @Query("SELECT m FROM Merchant m WHERE m.trackingId = :trackingId")
    Optional<Merchant> findByTrackingId(@Param("trackingId") UUID trackingId);

    @Query("SELECT m FROM Merchant m WHERE m.statutKYC = :statut ORDER BY m.createdAt ASC")
    List<Merchant> findByStatutKYC(@Param("statut") KycStatus statut);
}
