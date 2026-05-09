package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.CommandeRequest;
import com.backend.gns.application.dtos.responses.CommandeResponse;
import com.backend.gns.domain.enums.CommandeStatut;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommandeService {

  CommandeResponse create(CommandeRequest request);

  Optional<CommandeResponse> findByTrackingId(UUID trackingId);

  CommandeResponse update(UUID trackingId, CommandeRequest request);

  void delete(UUID trackingId);

  Page<CommandeResponse> findByStatut(CommandeStatut statut, Pageable pageable);

  Page<CommandeResponse> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable);

  Page<CommandeResponse> findByMerchantTrackingId(UUID merchantTrackingId, Pageable pageable);

  Page<CommandeResponse> findByCommandeStatut(CommandeStatut commandeStatut, Pageable pageable);

  Page<CommandeResponse> findAll(Pageable pageable);

  void payerCommande(UUID commandeTrackingId);
}
