package com.backend.gns.parametrage.infrastructure.repositories;

import com.backend.gns.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.parametrage.domain.models.ParametreGns;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametreGnsRepository extends JpaRepository<ParametreGns, Long> {
    Optional<ParametreGns> findByTrackingId(UUID trackingId);
    Optional<ParametreGns> findByNomParametreAndEstActifTrue(TypeParametreGns nomParametre);
}
