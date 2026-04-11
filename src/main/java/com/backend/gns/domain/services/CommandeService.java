package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.CommandeRequest;
import com.backend.gns.domain.dtos.responses.CommandeResponse;

import java.util.List;
import java.util.UUID;

public interface CommandeService {

    CommandeResponse create(CommandeRequest request);

    List<CommandeResponse> getAll();

    CommandeResponse getByTrackingId(UUID trackingId);

    CommandeResponse update(UUID trackingId, CommandeRequest request);

    void delete(UUID trackingId);
}
