package com.backend.gns.student.domain.services;

import com.backend.gns.student.application.dtos.requests.CardRequest;
import com.backend.gns.student.application.dtos.responses.CardResponse;
import com.backend.gns.student.domain.enums.CardStatut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CardService {

  CardResponse create(CardRequest request);

  CardResponse demanderCarte(UUID studentTrackingId);

  Optional<CardResponse> findByTrackingId(UUID trackingId);

  CardResponse update(UUID trackingId, CardRequest request);

  void delete(UUID trackingId);

  Page<CardResponse> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable);

  Page<CardResponse> findByCardStatus(CardStatut cardStatus, Pageable pageable);

  CardResponse declareCardLost(UUID cardTrackingId);

  Page<CardResponse> findAll(Pageable pageable);
}
