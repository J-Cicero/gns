package com.backend.gns.Shared.application.controllers;

import com.backend.gns.Shared.application.services.ConfigurationService;
import com.backend.gns.Shared.domain.models.ConfigurationGns;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/configurations")
@Tag(name = "CONFIGURATION", description = "Gestion des paramètres plateforme")
@RequiredArgsConstructor
public class ConfigurationGnsController {

    private final ConfigurationService configurationService;

    @GetMapping
    @Operation(summary = "Lister toutes les configurations")
    public ResponseEntity<List<ConfigurationGns>> getAll() {
        return ResponseEntity.ok(configurationService.getAllConfigurations());
    }

    @PutMapping("/{cle}")
    @Operation(summary = "Mettre à jour une configuration")
    public ResponseEntity<?> update(@PathVariable String cle, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(configurationService.updateConfiguration(cle, body.get("valeur")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
        }
    }
}
