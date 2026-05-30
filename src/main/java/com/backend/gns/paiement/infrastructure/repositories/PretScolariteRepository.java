package com.backend.gns.paiement.infrastructure.repositories;

import com.backend.gns.paiement.domain.models.PretScolarite;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PretScolariteRepository extends JpaRepository<PretScolarite, Long> {
  Optional<PretScolarite> findByTrackingId(UUID trackingId);

  List<PretScolarite> findByStudentTrackingIdAndEstRembourseFalse(UUID studentTrackingId);

  List<PretScolarite> findByUniversiteTrackingId(UUID universiteTrackingId);
}
