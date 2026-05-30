package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.ParametreDbsRequest;
import com.backend.gns.student.application.dtos.responses.ParametreDbsResponse;
import com.backend.gns.student.domain.enums.TypeParametreDbs;
import com.backend.gns.student.domain.services.ParametreDbsService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dbs-parameters")
@RequiredArgsConstructor
public class ParametreDbsController {

    private final ParametreDbsService service;

    @PostMapping
    public ResponseEntity<ParametreDbsResponse> saveOrUpdate(@Valid @RequestBody ParametreDbsRequest request) {
        return ResponseEntity.ok(service.saveOrUpdate(request));
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<ParametreDbsResponse> findByTrackingId(@PathVariable UUID trackingId) {
        return service.findByTrackingId(trackingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ParametreDbsResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/by-nom/{type}")
    public ResponseEntity<ParametreDbsResponse> findByNom(@PathVariable TypeParametreDbs type) {
        return service.findByNomParametre(type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
