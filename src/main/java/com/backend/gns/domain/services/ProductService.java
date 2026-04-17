package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.ProductRequest;
import com.backend.gns.application.dtos.responses.ProductResponse;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

  ProductResponse create(ProductRequest request);

  Optional<ProductResponse> findByTrackingId(UUID trackingId);

  ProductResponse update(UUID trackingId, ProductRequest request);

  void delete(UUID trackingId);

  Page<ProductResponse> findByBoutiqueTrackingId(UUID boutiqueTrackingId, Pageable pageable);

  Page<ProductResponse> findByEstDisponible(Boolean estDisponible, Pageable pageable);

  Page<ProductResponse> findAll(Pageable pageable);
}
