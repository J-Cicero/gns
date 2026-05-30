package com.backend.gns.paiement.application.mappers;

import com.backend.gns.commerce.domain.models.Product;
import com.backend.gns.commerce.infrastructure.repositories.ProductRepository;
import com.backend.gns.paiement.application.dtos.requests.CommandeLigneRequest;
import com.backend.gns.paiement.application.dtos.responses.CommandeLigneResponse;
import com.backend.gns.paiement.domain.models.Commande;
import com.backend.gns.paiement.domain.models.CommandeLigne;
import com.backend.gns.paiement.infrastructure.repositories.CommandeRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CommandeLigneMapper {

  private final CommandeRepository commandeRepository;
  private final ProductRepository productRepository;

  public CommandeLigneMapper(
      CommandeRepository commandeRepository, ProductRepository productRepository) {
    this.commandeRepository = commandeRepository;
    this.productRepository = productRepository;
  }

  public CommandeLigne toEntity(CommandeLigneRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("La requête CommandeLigneRequest ne peut pas être nulle");
    }

    CommandeLigne commandeLigne = new CommandeLigne();
    commandeLigne.setTrackingId(UUID.randomUUID());
    commandeLigne.setQuantite(request.quantite());
    commandeLigne.setPrixUnitaire(request.prixUnitaire());

    if (request.trackingCommandeId() != null) {
      Commande commande =
          commandeRepository
              .findByTrackingId(request.trackingCommandeId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Commande non trouvée avec l'ID: " + request.trackingCommandeId()));
      commandeLigne.setCommande(commande);
    }

    if (request.trackingProductId() != null) {
      Product product =
          productRepository
              .findByTrackingId(request.trackingProductId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Produit non trouvé avec l'ID: " + request.trackingProductId()));
      commandeLigne.setProduct(product);
    }

    return commandeLigne;
  }

  public CommandeLigneResponse toResponse(CommandeLigne commandeLigne) {
    if (commandeLigne == null) {
      throw new IllegalArgumentException("L'entité CommandeLigne ne peut pas être nulle");
    }

    return CommandeLigneResponse.builder()
        .trackingId(commandeLigne.getTrackingId())
        .quantite(commandeLigne.getQuantite())
        .prixUnitaire(commandeLigne.getPrixUnitaire())
        .trackingCommandeId(
            commandeLigne.getCommande() != null
                ? commandeLigne.getCommande().getTrackingId()
                : null)
        .trackingProductId(
            commandeLigne.getProduct() != null ? commandeLigne.getProduct().getTrackingId() : null)
        .build();
  }

  public CommandeLigne toEntityFromResponse(CommandeLigneResponse response) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse CommandeLigneResponse ne peut pas être nulle");
    }

    CommandeLigne commandeLigne = new CommandeLigne();
    commandeLigne.setTrackingId(response.trackingId());
    commandeLigne.setQuantite(response.quantite());
    commandeLigne.setPrixUnitaire(response.prixUnitaire());

    if (response.trackingCommandeId() != null) {
      Commande commande =
          commandeRepository
              .findByTrackingId(response.trackingCommandeId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Commande non trouvée avec l'ID: " + response.trackingCommandeId()));
      commandeLigne.setCommande(commande);
    }

    if (response.trackingProductId() != null) {
      Product product =
          productRepository
              .findByTrackingId(response.trackingProductId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Produit non trouvé avec l'ID: " + response.trackingProductId()));
      commandeLigne.setProduct(product);
    }

    return commandeLigne;
  }
}
