package com.backend.gns.core.parametrage.infrastructure.repositories;

import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.models.ParametreGns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParametreGnsRepository extends JpaRepository<ParametreGns, Long> {
  Optional<ParametreGns> findByTrackingId(UUID trackingId);

  Optional<ParametreGns> findByNomParametre(TypeParametreGns nomParametre);
}
