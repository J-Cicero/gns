package com.backend.gns.core.infrastructure.repositories;

import com.backend.gns.core.domain.models.Banque;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanqueRepository extends JpaRepository<Banque, Long> {
  Optional<Banque> findByTrackingId(UUID trackingId);

  Optional<Banque> findByCode(String code);
}
