package com.backend.gns.application.controllers;

import com.backend.gns.application.dtos.responses.StudentResponse;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.domain.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bank")
@Tag(
    name = "BANK",
    description = "Portail Partenaire Bancaire - Accès restreint aux institutions bancaires")
@CrossOrigin("*")
public class BankController {

  private final StudentService studentService;

  public BankController(StudentService studentService) {
    this.studentService = studentService;
  }

  @GetMapping("/students/mandats")
  @Operation(
      summary = "Lister les étudiants en attente de validation de mandat",
      description =
          "Retourne la liste des étudiants avec le statut KYC 'EN_ATTENTE' "
              + "(en attente de validation de leur mandat de prélèvement papier).")
  @ApiResponse(responseCode = "200", description = "Liste des mandats en cours")
  @ApiResponse(responseCode = "404", description = "Aucun mandat en cours")
  public ResponseEntity<?> listPendingMandates(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      Page<StudentResponse> mandatEnCours =
          studentService.findByStatutKYC(KycStatus.EN_ATTENTE, pageable);

      if (!mandatEnCours.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                Map.of(
                    "error",
                    "NO_PENDING_MANDATES",
                    "message",
                    "Aucun mandat en attente de validation"));
      }

      return ResponseEntity.ok(mandatEnCours);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "FETCH_FAILED", "message", e.getMessage()));
    }
  }
}
