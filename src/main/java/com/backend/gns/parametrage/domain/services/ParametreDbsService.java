package com.backend.gns.parametrage.domain.services;

import com.backend.gns.parametrage.application.dtos.requests.ParametreDbsRequest;
import com.backend.gns.parametrage.application.dtos.responses.ParametreDbsResponse;
import com.backend.gns.parametrage.domain.enums.TypeParametreDbs;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParametreDbsService {
    ParametreDbsResponse saveOrUpdate(ParametreDbsRequest request);
    Optional<ParametreDbsResponse> findByTrackingId(UUID trackingId);
    Page<ParametreDbsResponse> findAll(Pageable pageable);
    Optional<ParametreDbsResponse> findByNomParametre(TypeParametreDbs nom);
    String getValeur(TypeParametreDbs type);
    BigDecimal getValeurAsBigDecimal(TypeParametreDbs type);
    Integer getValeurAsInteger(TypeParametreDbs type);
}
