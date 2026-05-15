package com.backend.gns.Shared.application.controllers;

import com.backend.gns.Shared.application.dtos.requests.ParametreGnsRequest;
import com.backend.gns.Shared.application.dtos.responses.ParametreGnsResponse;
import com.backend.gns.Shared.domain.models.ParametreGns;
import com.backend.gns.Shared.application.mappers.ParametreGnsMapper;
import com.backend.gns.Shared.infrastructure.repositories.ParametreGnsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parametres-gns")
@Tag(name = "PARAMETRES GNS", description = "Gestion des paramètres système (Admin GNS)")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ParametreGnsController {

    private final ParametreGnsRepository repository;
    private final ParametreGnsMapper mapper;

    @PostMapping
    @Operation(summary = "Créer un paramètre")
    public ResponseEntity<ParametreGnsResponse> create(@RequestBody ParametreGnsRequest request) {
        ParametreGns entity = mapper.toEntity(request);
        ParametreGns saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(saved));
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<ParametreGnsResponse> findByTrackingId(@PathVariable UUID trackingId) {
        return repository.findByTrackingId(trackingId)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<ParametreGnsResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(repository.findAll(pageable).map(mapper::toResponse));
    }
}
