package com.backend.gns.core.infrastructure.repositories;

import com.backend.gns.core.domain.models.CompteBancaire;
import com.backend.gns.core.domain.enums.ProprietaireType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteBancaireRepository extends JpaRepository<CompteBancaire, Long> {
  Optional<CompteBancaire> findByTrackingId(UUID trackingId);

  List<CompteBancaire> findByProprietaireTrackingId(UUID proprietaireTrackingId);

  Optional<CompteBancaire> findByProprietaireTrackingIdAndBanqueTrackingId(
      UUID proprietaireTrackingId, UUID banqueTrackingId);

  List<CompteBancaire> findByTypeProprietaire(ProprietaireType type);
}
