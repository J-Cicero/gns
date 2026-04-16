package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.ProductRequest;
import com.backend.gns.application.dtos.responses.ProductResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    Optional<ProductResponse> findByTrackingId(UUID trackingId);

    ProductResponse update(UUID trackingId, ProductRequest request);

    void delete(UUID trackingId);

    List<ProductResponse> findByMerchantTrackingId(UUID merchantTrackingId);

    List<ProductResponse> findByEstDisponible(Boolean estDisponible);

    List<ProductResponse> findAll();
}
