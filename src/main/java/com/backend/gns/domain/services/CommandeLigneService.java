package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.CommandeLigneRequest;
import com.backend.gns.application.dtos.responses.CommandeLigneResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommandeLigneService {

    CommandeLigneResponse create(CommandeLigneRequest request);

    Optional<CommandeLigneResponse> findByTrackingId(UUID trackingId);

    CommandeLigneResponse update(UUID trackingId, CommandeLigneRequest request);

    void delete(UUID trackingId);

    List<CommandeLigneResponse> findByCommandeTrackingId(UUID commandeTrackingId);

    List<CommandeLigneResponse> findByProductTrackingId(UUID productTrackingId);

    List<CommandeLigneResponse> findAll();
}
