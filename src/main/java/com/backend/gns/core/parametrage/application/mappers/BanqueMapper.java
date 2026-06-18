package com.backend.gns.core.application.mappers;

import com.backend.gns.core.application.dtos.responses.BanqueResponse;
import com.backend.gns.core.parametrage.domain.models.Banque;
import org.springframework.stereotype.Component;

@Component
public class BanqueMapper {

  public BanqueResponse toResponse(Banque banque) {
    if (banque == null) return null;
    return BanqueResponse.builder()
        .trackingId(banque.getTrackingId())
        .code(banque.getCode())
        .name(banque.getName())
        .build();
  }
}
