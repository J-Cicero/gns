package com.backend.gns.commerce.domain.services;

import com.backend.gns.commerce.application.dtos.requests.ProductRequest;
import com.backend.gns.commerce.application.dtos.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProductService {

  ProductResponse create(ProductRequest request);

  Optional<ProductResponse> findByTrackingId(UUID trackingId);

  ProductResponse update(UUID trackingId, ProductRequest request);

  void delete(UUID trackingId);

  Page<ProductResponse> findByBoutiqueTrackingId(UUID boutiqueTrackingId, Pageable pageable);

  Page<ProductResponse> findByEstDisponible(Boolean estDisponible, Pageable pageable);

  Page<ProductResponse> findAll(Pageable pageable);
}
