package com.backend.gns.domain.mappers;

import com.backend.gns.domain.dtos.requests.ProductRequest;
import com.backend.gns.domain.dtos.responses.ProductResponse;
import com.backend.gns.domain.models.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }

        Product product = new Product();
        product.setTrackingId(UUID.randomUUID());
        product.setMerchantTrackingId(request.merchantTrackingId());
        product.setNom(request.nom());
        product.setDescription(request.description());
        product.setPrix(request.prix());
        product.setStock(request.stock());
        product.setEstDisponible(request.stock() > 0);
        product.setDateAjout(LocalDate.now());

        return product;
    }

    public ProductResponse toResponse(Product entity) {
        if (entity == null) {
            return null;
        }

        return new ProductResponse(
                entity.getTrackingId(),
                entity.getMerchantTrackingId(),
                entity.getNom(),
                entity.getDescription(),
                entity.getPrix(),
                entity.getStock(),
                entity.getEstDisponible(),
                entity.getDateAjout(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<ProductResponse> toResponseList(List<Product> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
