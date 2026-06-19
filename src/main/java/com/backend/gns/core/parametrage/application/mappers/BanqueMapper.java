package com.backend.gns.core.parametrage.application.mappers;

import com.backend.gns.core.parametrage.application.dtos.requests.BanqueRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.BanqueResponse;
import com.backend.gns.core.parametrage.domain.models.Banque;
import org.springframework.stereotype.Component;

@Component
public class BanqueMapper {

  public Banque toEntity(BanqueRequest request){
    if(request == null) return  null;

    Banque banque = new Banque();
    banque.setName(request.name());
    banque.setCode(request.code());
    return banque;
  }
  public BanqueResponse toResponse(Banque banque) {
    if (banque == null) return null;
    return BanqueResponse.builder()
        .trackingId(banque.getTrackingId())
        .code(banque.getCode())
        .name(banque.getName())
        .build();
  }
}
