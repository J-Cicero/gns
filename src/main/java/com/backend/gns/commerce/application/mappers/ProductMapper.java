package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.ProductRequest;
import com.backend.gns.commerce.application.dtos.responses.ProductResponse;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Product;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ProductMapper {

  private final BoutiqueRepository boutiqueRepository;

  public ProductMapper(BoutiqueRepository boutiqueRepository) {
    this.boutiqueRepository = boutiqueRepository;
  }

  public Product toEntity(ProductRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("ProductRequest cannot be null");
    }

    Product product = new Product();
    product.setTrackingId(UUID.randomUUID());
    product.setName(request.name());
    product.setDescription(request.description());
    product.setPrice(request.price());
    product.setStock(request.stock());
    product.setIsAvailable(request.isAvailable());
    product.setAddedAt(request.addedAt() != null ? request.addedAt() : LocalDateTime.now());
    product.setImageUrl(request.imageUrl());

    if (request.boutiqueTrackingId() != null) {
      Boutique boutique =
          boutiqueRepository
              .findByTrackingId(request.boutiqueTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Boutique not found with trackingId: " + request.boutiqueTrackingId()));
      product.setBoutique(boutique);
    }

    return product;
  }

  public ProductResponse toResponse(Product product) {
    if (product == null) {
      return null;
    }

    return ProductResponse.builder()
        .trackingId(product.getTrackingId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .stock(product.getStock())
        .isAvailable(product.getIsAvailable())
        .addedAt(product.getAddedAt())
        .boutiqueTrackingId(
            product.getBoutique() != null ? product.getBoutique().getTrackingId() : null)
        .imageUrl(product.getImageUrl())
        .build();
  }
}
