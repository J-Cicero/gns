package com.backend.gns.student.infrastructure.repositories;

import com.backend.gns.student.domain.enums.TypeParametreDbs;
import com.backend.gns.student.domain.models.ParametreDbs;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametreDbsRepository extends JpaRepository<ParametreDbs, Long> {
    Optional<ParametreDbs> findByTrackingId(UUID trackingId);
    Optional<ParametreDbs> findByNomParametre(TypeParametreDbs nomParametre);
}
