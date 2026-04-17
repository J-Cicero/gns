package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Merchant;
import com.backend.gns.domain.enums.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByEmail(String email);

    Optional<Merchant> findByNomBoutique(String nomBoutique);

    Optional<Merchant> findByTrackingId(UUID trackingId);

    Page<Merchant> findByStatutKYCOrderByCreatedAtAsc(KycStatus statutKYC, Pageable pageable);
}
