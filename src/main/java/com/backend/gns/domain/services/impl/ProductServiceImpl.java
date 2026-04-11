package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.ProductRequest;
import com.backend.gns.domain.dtos.responses.ProductResponse;
import com.backend.gns.domain.mappers.ProductMapper;
import com.backend.gns.domain.models.Product;
import com.backend.gns.infrastructure.repositories.ProductRepository;
import com.backend.gns.domain.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        List<Product> products = productRepository.findAll();
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getByTrackingId(UUID trackingId) {
        Product product = productRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with trackingId: " + trackingId));
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse update(UUID trackingId, ProductRequest request) {
        Product product = productRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with trackingId: " + trackingId));

        product.setNom(request.nom());
        product.setDescription(request.description());
        product.setPrix(request.prix());
        product.setStock(request.stock());
        product.setEstDisponible(request.stock() > 0);

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void delete(UUID trackingId) {
        Product product = productRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with trackingId: " + trackingId));
        productRepository.delete(product);
    }
}
