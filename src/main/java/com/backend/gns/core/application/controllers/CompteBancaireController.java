package com.backend.gns.core.application.controllers;

import com.backend.gns.core.domain.models.CompteBancaire;
import com.backend.gns.core.infrastructure.repositories.CompteBancaireRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compte-bancaire")
@RequiredArgsConstructor
public class CompteBancaireController {

  private final CompteBancaireRepository compteBancaireRepository;

  @GetMapping("/proprietaire/{trackingId}")
  public ResponseEntity<List<CompteBancaire>> getByProprietaire(@PathVariable UUID trackingId) {
    return ResponseEntity.ok(compteBancaireRepository.findByProprietaireTrackingId(trackingId));
  }
}
