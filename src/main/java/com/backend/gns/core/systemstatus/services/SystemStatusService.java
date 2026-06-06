package com.backend.gns.core.systemstatus.services;

import com.backend.gns.core.systemstatus.dtos.SystemStatusResponse;
import com.backend.gns.core.systemstatus.enums.SystemStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemStatusService {

  private final ApplicationEventPublisher eventPublisher;

  // Injecter votre repository qui gère les paramètres système
  // private final ParametreGnsRepository parametreRepo;

  public SystemStatusService(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public SystemStatusResponse getGlobalStatus() {
    // Logique pour lire l'état en base
    // Pour l'instant, valeur par défaut pour test
    return new SystemStatusResponse(SystemStatus.ACTIVE, true);
  }

  @Transactional
  public void initiateClosure() {
    // 1. Logique pour mettre à jour l'état en base à CLOSURE_PENDING

    // 2. Publication de l'événement pour les autres modules
    // eventPublisher.publishEvent(new YearClosureInitiatedEvent());
  }
}
