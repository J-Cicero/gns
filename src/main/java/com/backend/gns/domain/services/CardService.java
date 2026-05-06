package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.CardRequest;
import com.backend.gns.application.dtos.responses.CardResponse;
import com.backend.gns.domain.enums.CardStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {

  CardResponse create(CardRequest request);

  Optional<CardResponse> findByTrackingId(UUID trackingId);

  CardResponse update(UUID trackingId, CardRequest request);

  void delete(UUID trackingId);

  Page<CardResponse> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable);

  Page<CardResponse> findByCardStatus(CardStatus cardStatus, Pageable pageable);

  CardResponse declareCardLost(UUID cardTrackingId);

  Page<CardResponse> findAll(Pageable pageable);
}
