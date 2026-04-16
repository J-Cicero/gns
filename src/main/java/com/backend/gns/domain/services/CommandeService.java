package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.CommandeRequest;
import com.backend.gns.application.dtos.responses.CommandeResponse;
import com.backend.gns.domain.enums.CommandeStatut;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommandeService {

    CommandeResponse create(CommandeRequest request);

    Optional<CommandeResponse> findByTrackingId(UUID trackingId);

    CommandeResponse update(UUID trackingId, CommandeRequest request);

    void delete(UUID trackingId);

    List<CommandeResponse> findByStatut(CommandeStatut statut);

    List<CommandeResponse> findAll();
}
