package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.models.CompteBancaireUniversite;
import com.backend.gns.student.domain.models.Universite;
import com.backend.gns.core.domain.models.Banque;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteBancaireUniversiteRepository extends JpaRepository<CompteBancaireUniversite, Long> {
  Optional<CompteBancaireUniversite> findByUniversiteAndBanque(Universite universite, Banque banque);
  Optional<CompteBancaireUniversite> findByTrackingId(UUID trackingId);
}
