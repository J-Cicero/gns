package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.RegleBourseDbsRequest;
import com.backend.gns.student.application.dtos.responses.RegleBourseDbsResponse;
import com.backend.gns.student.domain.models.RegleBourseDbs;
import com.backend.gns.student.application.mappers.RegleBourseDbsMapper;
import com.backend.gns.student.infrastructure.repositories.RegleBourseDbsRepository;
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
@RequestMapping("/api/regles-bourse-dbs")
@Tag(name = "REGLES BOURSE DBS", description = "Gestion des règles d'éligibilité (Admin DBS)")
@CrossOrigin("*")
@RequiredArgsConstructor
public class RegleBourseDbsController {

    private final RegleBourseDbsRepository repository;
    private final RegleBourseDbsMapper mapper;

    @PostMapping
    @Operation(summary = "Créer une règle de bourse")
    public ResponseEntity<RegleBourseDbsResponse> create(@RequestBody RegleBourseDbsRequest request) {
        RegleBourseDbs entity = mapper.toEntity(request);
        RegleBourseDbs saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(saved));
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<RegleBourseDbsResponse> findByTrackingId(@PathVariable UUID trackingId) {
        return repository.findByTrackingId(trackingId)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<RegleBourseDbsResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(repository.findAll(pageable).map(mapper::toResponse));
    }
}
