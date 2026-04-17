package com.backend.gns.infrastructure.repositories;

import com.backend.gns.domain.models.Merchant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

  Optional<Merchant> findByEmail(String email);

  Optional<Merchant> findByTrackingId(UUID trackingId);

  Page<Merchant> findAll(Pageable pageable);
}
