package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.ProductRequest;
import com.backend.gns.domain.dtos.responses.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    List<ProductResponse> getAll();

    ProductResponse getByTrackingId(UUID trackingId);

    ProductResponse update(UUID trackingId, ProductRequest request);

    void delete(UUID trackingId);
}
