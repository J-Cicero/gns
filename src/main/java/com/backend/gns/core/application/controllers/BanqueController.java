package com.backend.gns.core.application.controllers;

import com.backend.gns.core.application.dtos.responses.BanqueResponse;
import com.backend.gns.core.domain.services.BanqueService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/banques")
@RequiredArgsConstructor
public class BanqueController {

  private final BanqueService banqueService;

  @GetMapping
  public ResponseEntity<List<BanqueResponse>> getAllBanques() {
    return ResponseEntity.ok(banqueService.getAllBanques());
  }
}
