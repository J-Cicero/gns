package com.backend.gns.student.application.controllers;

import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import com.backend.gns.Shared.domain.enums.KycStatus;
import com.backend.gns.student.domain.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@Tag(name = "STUDENT", description = "Gestion des étudiants")
@CrossOrigin("*")
public class StudentController {

  private final StudentService studentService;

  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  @PostMapping
  @Operation(summary = "Créer un étudiant", description = "Crée un nouvel étudiant")
  @ApiResponse(responseCode = "201", description = "Étudiant créé avec succès")
  public ResponseEntity<?> create(@RequestBody StudentRequest request) {
    try {
      StudentResponse response = studentService.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "CREATION_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/{trackingId}")
  @Operation(summary = "Récupérer un étudiant", description = "Récupère un étudiant par son ID")
  @ApiResponse(responseCode = "200", description = "Étudiant trouvé")
  @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
  public ResponseEntity<?> findByTrackingId(@PathVariable UUID trackingId) {
    try {
      return studentService
          .findByTrackingId(trackingId)
          .map(response -> ResponseEntity.ok((Object) response))
          .orElse(
              ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("error", "NOT_FOUND", "message", "Étudiant non trouvé")));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PutMapping("/{trackingId}")
  @Operation(
      summary = "Mettre à jour un étudiant",
      description = "Mettre à jour les informations d'un étudiant")
  @ApiResponse(responseCode = "200", description = "Étudiant mis à jour avec succès")
  @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
  public ResponseEntity<?> update(
      @PathVariable UUID trackingId, @RequestBody StudentRequest request) {
    try {
      StudentResponse response = studentService.update(trackingId, request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPDATE_FAILED", "message", e.getMessage()));
    }
  }

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer un étudiant", description = "Supprime un étudiant par son ID")
  @ApiResponse(responseCode = "204", description = "Étudiant supprimé avec succès")
  @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
  public ResponseEntity<?> delete(@PathVariable UUID trackingId) {
    try {
      studentService.delete(trackingId);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "DELETE_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/kyc/{statutKYC}")
  @Operation(
      summary = "Récupérer les étudiants par statut KYC",
      description = "Récupère tous les étudiants avec un statut KYC donné")
  @ApiResponse(responseCode = "200", description = "Étudiants trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun étudiant trouvé")
  public ResponseEntity<?> findByStatutKYC(
      @PathVariable KycStatus statutKYC,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = studentService.findByStatutKYC(statutKYC, pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun étudiant avec ce statut KYC"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping
  @Operation(
      summary = "Récupérer tous les étudiants",
      description = "Récupère la liste de tous les étudiants")
  @ApiResponse(responseCode = "200", description = "Étudiants récupérés avec succès")
  @ApiResponse(responseCode = "404", description = "Aucun étudiant trouvé")
  public ResponseEntity<?> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    try {
      Pageable pageable = PageRequest.of(page, size);
      var responses = studentService.findAll(pageable);
      if (!responses.hasContent()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "NOT_FOUND", "message", "Aucun étudiant trouvé"));
      }
      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "SEARCH_FAILED", "message", e.getMessage()));
    }
  }

  @PostMapping("/{trackingId}/verify-pin")
  @Operation(
      summary = "Vérifier le code PIN d'un étudiant",
      description = "Vérifie si le code PIN fourni correspond au PIN haché de l'étudiant")
  @ApiResponse(responseCode = "200", description = "PIN vérifié")
  @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
  @ApiResponse(responseCode = "400", description = "PIN incorrect")
  public ResponseEntity<?> verifyPin(
      @PathVariable UUID trackingId, @RequestParam String pinCode) {
    try {
      boolean isValid = studentService.verifyPin(trackingId, pinCode);
      if (isValid) {
        return ResponseEntity.ok(Map.of("success", true, "message", "PIN correct"));
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("success", false, "message", "PIN incorrect"));
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "VERIFICATION_FAILED", "message", e.getMessage()));
    }
  }
}
