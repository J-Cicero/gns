package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.ProductRequest;
import com.backend.gns.commerce.application.dtos.responses.ProductResponse;
import com.backend.gns.commerce.application.mappers.ProductMapper;
import com.backend.gns.commerce.domain.models.Product;
import com.backend.gns.commerce.domain.services.ProductService;
import com.backend.gns.commerce.infrastructure.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
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
    return productRepository.findByTrackingId(trackingId).map(productMapper::toResponse);
  }

  @Override
  @Transactional
  public ProductResponse update(UUID trackingId, ProductRequest request) {
    Product product =
        productRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + trackingId));

    product.setName(request.name());
    product.setDescription(request.description());
    product.setPrice(request.price());
    product.setStock(request.stock());
    product.setIsAvailable(request.isAvailable());
    product.setAddedAt(request.addedAt());

    return productMapper.toResponse(productRepository.save(product));
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Product product =
        productRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Produit non trouvé avec l'ID: " + trackingId));
    productRepository.delete(product);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponse> findByBoutiqueTrackingId(
      UUID boutiqueTrackingId, Pageable pageable) {
    return productRepository
        .findByBoutiqueTrackingId(boutiqueTrackingId, normalize(pageable))
        .map(productMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponse> findByEstDisponible(Boolean estDisponible, Pageable pageable) {
    return productRepository
        .findByIsAvailable(estDisponible, normalize(pageable))
        .map(productMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponse> findAll(Pageable pageable) {
    return productRepository.findAll(normalize(pageable)).map(productMapper::toResponse);
  }
}
