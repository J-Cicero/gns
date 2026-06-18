package com.backend.gns.core.parametrage.infrastructure.repositories;

import com.backend.gns.core.parametrage.domain.models.Banque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BanqueRepository extends JpaRepository<Banque, Long> {
  Optional<Banque> findByTrackingId(UUID trackingId);

  Optional<Banque> findByCode(String code);
}
