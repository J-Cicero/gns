package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.ProductRequest;
import com.backend.gns.domain.dtos.responses.ProductResponse;
import com.backend.gns.domain.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<ProductResponse> responses = productService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<ProductResponse> getOne(@PathVariable UUID trackingId) {
        ProductResponse response = productService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody ProductRequest request) {
        ProductResponse response = productService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        productService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }
}
