package com.backend.gns.Shared.domain.services;

import com.backend.gns.Shared.application.dtos.requests.ParametreGnsRequest;
import com.backend.gns.Shared.application.dtos.responses.ParametreGnsResponse;
import com.backend.gns.Shared.domain.enums.TypeParametreGns;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParametreGnsService {
    ParametreGnsResponse saveOrUpdate(ParametreGnsRequest request);
    Optional<ParametreGnsResponse> findByTrackingId(UUID trackingId);
    Page<ParametreGnsResponse> findAll(Pageable pageable);
    Optional<ParametreGnsResponse> findByNomParametre(TypeParametreGns nom);
    String getValeur(TypeParametreGns type);
    BigDecimal getValeurAsBigDecimal(TypeParametreGns type);
    Integer getValeurAsInteger(TypeParametreGns type);
}
