package com.backend.gns.core.parametrage.domain.services;

import com.backend.gns.core.parametrage.application.dtos.requests.ParametreGnsRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.ParametreGnsResponse;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ParametreGnsService {
  ParametreGnsResponse create(ParametreGnsRequest request);
  ParametreGnsResponse update(UUID trackingId, ParametreGnsRequest request);
  Optional<ParametreGnsResponse> findByTrackingId(UUID trackingId);
  Page<ParametreGnsResponse> findAll(Pageable pageable);
  Optional<ParametreGnsResponse> findByNomParametre(TypeParametreGns nom);
  String getValeur(TypeParametreGns type);
  BigDecimal getValeurAsBigDecimal(TypeParametreGns type);
  Integer getValeurAsInteger(TypeParametreGns type);
}
