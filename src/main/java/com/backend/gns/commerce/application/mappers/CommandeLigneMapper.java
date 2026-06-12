package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.CommandeLigneRequest;
import com.backend.gns.commerce.application.dtos.responses.CommandeLigneResponse;
import com.backend.gns.commerce.domain.models.CommandeLigne;
import com.backend.gns.commerce.infrastructure.repositories.CommandeRepository;
import com.backend.gns.commerce.infrastructure.repositories.ProductRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandeLigneMapper {

  private final CommandeRepository commandeRepository;
  private final ProductRepository productRepository;

  public CommandeLigne toEntity(CommandeLigneRequest request) {
    if (request == null) return null;

    CommandeLigne commandeLigne = new CommandeLigne();
    commandeLigne.setTrackingId(UUID.randomUUID());
    commandeLigne.setQuantite(request.quantite());
    commandeLigne.setPrixUnitaire(request.prixUnitaire());

    if (request.trackingCommandeId() != null) {
      commandeRepository.findByTrackingId(request.trackingCommandeId())
          .ifPresent(commandeLigne::setCommande);
    }

    if (request.trackingProductId() != null) {
      productRepository.findByTrackingId(request.trackingProductId())
          .ifPresent(commandeLigne::setProduct);
    }

    return commandeLigne;
  }

  public CommandeLigneResponse toResponse(CommandeLigne entity) {
    if (entity == null) return null;

    return CommandeLigneResponse.builder()
        .trackingId(entity.getTrackingId())
        .quantite(entity.getQuantite())
        .prixUnitaire(entity.getPrixUnitaire())
        .trackingCommandeId(entity.getCommande() != null ? entity.getCommande().getTrackingId() : null)
        .trackingProductId(entity.getProduct() != null ? entity.getProduct().getTrackingId() : null)
        .build();
  }
}
