package com.backend.gns.commerce.domain.services;

import com.backend.gns.commerce.application.dtos.requests.CommandeLigneRequest;
import com.backend.gns.commerce.application.dtos.responses.CommandeLigneResponse;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommandeLigneService {

  CommandeLigneResponse create(CommandeLigneRequest request);

  Optional<CommandeLigneResponse> findByTrackingId(UUID trackingId);

  CommandeLigneResponse update(UUID trackingId, CommandeLigneRequest request);

  void delete(UUID trackingId);

  Page<CommandeLigneResponse> findByCommandeTrackingId(UUID commandeTrackingId, Pageable pageable);

  Page<CommandeLigneResponse> findByProductTrackingId(UUID productTrackingId, Pageable pageable);

  Page<CommandeLigneResponse> findAll(Pageable pageable);
}
