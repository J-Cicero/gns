package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.RegleBourseDbsRequest;
import com.backend.gns.student.application.dtos.responses.RegleBourseDbsResponse;
import com.backend.gns.student.domain.services.RegleBourseDbsService;
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

    private final RegleBourseDbsService service;

    @PostMapping
    @Operation(summary = "Créer ou mettre à jour une règle de bourse (Unicité garantie)")
    public ResponseEntity<RegleBourseDbsResponse> saveOrUpdate(@RequestBody RegleBourseDbsRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.saveOrUpdate(request));
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<RegleBourseDbsResponse> findByTrackingId(@PathVariable UUID trackingId) {
        return service.findByTrackingId(trackingId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<RegleBourseDbsResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Rechercher par type de règle")
    public ResponseEntity<RegleBourseDbsResponse> findByType(@PathVariable com.backend.gns.student.domain.enums.TypeRegleBourse type) {
        return service.findByTypeRegle(type)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
