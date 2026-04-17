package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.ProductRequest;
import com.backend.gns.application.dtos.responses.ProductResponse;
import com.backend.gns.domain.models.Product;
import com.backend.gns.domain.models.Boutique;
import com.backend.gns.infrastructure.repositories.ProductRepository;
import com.backend.gns.infrastructure.repositories.BoutiqueRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ProductMapper {

    private final BoutiqueRepository boutiqueRepository;

    public ProductMapper( BoutiqueRepository boutiqueRepository) {
        this.boutiqueRepository = boutiqueRepository;
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

        if (request.boutiqueTrackingId() != null) {
            Boutique boutique = boutiqueRepository.findByTrackingId(request.boutiqueTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Boutique non trouvée avec l'ID: " + request.boutiqueTrackingId()));
            product.setBoutique(boutique);
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
                .boutiqueTrackingId(product.getBoutique() != null ? product.getBoutique().getTrackingId() : null)
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

        if (response.boutiqueTrackingId() != null) {
            Boutique boutique = boutiqueRepository.findByTrackingId(response.boutiqueTrackingId())
                .orElseThrow(() -> new IllegalArgumentException("Boutique non trouvée avec l'ID: " + response.boutiqueTrackingId()));
            product.setBoutique(boutique);
        }

        return product;
    }

}
