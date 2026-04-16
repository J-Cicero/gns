package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.ProductRequest;
import com.backend.gns.application.dtos.responses.ProductResponse;
import com.backend.gns.application.mappers.ProductMapper;
import com.backend.gns.domain.models.Product;
import com.backend.gns.infrastructure.repositories.ProductRepository;
import com.backend.gns.domain.services.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> findByTrackingId(UUID trackingId) {
        return productRepository.findByTrackingId(trackingId)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductResponse update(UUID trackingId, ProductRequest request) {
        Product product = productRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + trackingId));
        
        product.setNom(request.nom());
        product.setDescription(request.description());
        product.setPrix(request.prix());
        product.setStock(request.stock());
        product.setEstDisponible(request.estDisponible());
        product.setDateAjout(request.dateAjout());
        
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Product product = productRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + trackingId));
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByMerchantTrackingId(UUID merchantTrackingId) {
        return productRepository.findByMerchantTrackingId(merchantTrackingId).stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByEstDisponible(Boolean estDisponible) {
        return productRepository.findByEstDisponible(estDisponible).stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }
}
