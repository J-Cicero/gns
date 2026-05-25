package com.backend.gns.student.application.controllers;

import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/scolarite-years")
@Tag(name = "SCOLARITE_YEAR", description = "Gestion des années scolaires")
@RequiredArgsConstructor
public class ScolariteYearController {

    private final ScolariteYearRepository repository;

    @GetMapping
    @Operation(summary = "Lister toutes les années scolaires")
    public ResponseEntity<List<ScolariteYear>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer l'année scolaire active")
    public ResponseEntity<ScolariteYear> findActive() {
        return repository.findByEstOuverteTrue()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer une année scolaire")
    public ResponseEntity<ScolariteYear> create(@RequestBody ScolariteYear scolariteYear) {
        if (scolariteYear.getTrackingId() == null) {
            scolariteYear.setTrackingId(UUID.randomUUID());
        }
        return ResponseEntity.ok(repository.save(scolariteYear));
    }
}
