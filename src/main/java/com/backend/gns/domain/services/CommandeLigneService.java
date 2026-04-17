package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.CommandeLigneRequest;
import com.backend.gns.application.dtos.responses.CommandeLigneResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CommandeLigneService {

    CommandeLigneResponse create(CommandeLigneRequest request);

    Optional<CommandeLigneResponse> findByTrackingId(UUID trackingId);

    CommandeLigneResponse update(UUID trackingId, CommandeLigneRequest request);

    void delete(UUID trackingId);

    Page<CommandeLigneResponse> findByCommandeTrackingId(UUID commandeTrackingId, Pageable pageable);

    Page<CommandeLigneResponse> findByProductTrackingId(UUID productTrackingId, Pageable pageable);

    Page<CommandeLigneResponse> findAll(Pageable pageable);
}
