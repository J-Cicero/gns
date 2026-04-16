package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.ProductRequest;
import com.backend.gns.application.dtos.responses.ProductResponse;
import com.backend.gns.domain.models.Product;
import com.backend.gns.domain.models.Merchant;
import com.backend.gns.infrastructure.repositories.ProductRepository;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ProductMapper {

    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;

    public ProductMapper(ProductRepository productRepository, 
                        MerchantRepository merchantRepository) {
        this.productRepository = productRepository;
        this.merchantRepository = merchantRepository;
    }

    public Product toEntity(ProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête ProductRequest ne peut pas être nulle");
        }

        Product product = new Product();
        product.setTrackingId(UUID.randomUUID());
        product.setNom(request.nom());
        product.setDescription(request.description());
        product.setPrix(request.prix());
        product.setStock(request.stock());
        product.setEstDisponible(request.estDisponible());
        product.setDateAjout(request.dateAjout() != null ? request.dateAjout() : LocalDateTime.now());

        if (request.merchantTrackingId() != null) {
            Merchant merchant = merchantRepository.findByTrackingId(request.merchantTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Commerçant non trouvé avec l'ID: " + request.merchantTrackingId()));
            product.setMerchant(merchant);
        }

        return product;
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("L'entité Product ne peut pas être nulle");
        }

        return ProductResponse.builder()
                .trackingId(product.getTrackingId())
                .nom(product.getNom())
                .description(product.getDescription())
                .prix(product.getPrix())
                .stock(product.getStock())
                .estDisponible(product.getEstDisponible())
                .dateAjout(product.getDateAjout())
                .merchantTrackingId(product.getMerchant() != null ? product.getMerchant().getTrackingId() : null)
                .build();
    }

    public Product toEntityFromResponse(ProductResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("La réponse ProductResponse ne peut pas être nulle");
        }

        Product product = new Product();
        product.setTrackingId(response.trackingId());
        product.setNom(response.nom());
        product.setDescription(response.description());
        product.setPrix(response.prix());
        product.setStock(response.stock());
        product.setEstDisponible(response.estDisponible());
        product.setDateAjout(response.dateAjout());

        if (response.merchantTrackingId() != null) {
            Merchant merchant = merchantRepository.findByTrackingId(response.merchantTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Commerçant non trouvé avec l'ID: " + response.merchantTrackingId()));
            product.setMerchant(merchant);
        }

        return product;
    }

}
