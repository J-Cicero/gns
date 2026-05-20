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
import org.springframework.data.domain.Page;
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
  public ResponseEntity<StudentResponse> create(@RequestBody StudentRequest request) {
    StudentResponse response = studentService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{trackingId}")
  @Operation(summary = "Récupérer un étudiant", description = "Récupère un étudiant par son ID")
  @ApiResponse(responseCode = "200", description = "Étudiant trouvé")
  @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
  public ResponseEntity<StudentResponse> findByTrackingId(@PathVariable UUID trackingId) {
    return studentService
        .findByTrackingId(trackingId)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + trackingId));
  }

  @PutMapping("/{trackingId}")
  @Operation(
      summary = "Mettre à jour un étudiant",
      description = "Mettre à jour les informations d'un étudiant")
  @ApiResponse(responseCode = "200", description = "Étudiant mis à jour avec succès")
  @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
  public ResponseEntity<StudentResponse> update(
      @PathVariable UUID trackingId, @RequestBody StudentRequest request) {
    StudentResponse response = studentService.update(trackingId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{trackingId}")
  @Operation(summary = "Supprimer un étudiant", description = "Supprime un étudiant par son ID")
  @ApiResponse(responseCode = "204", description = "Étudiant supprimé avec succès")
  @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
  public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
    studentService.delete(trackingId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/kyc/{statutKYC}")
  @Operation(
      summary = "Récupérer les étudiants par statut KYC",
      description = "Récupère tous les étudiants avec un statut KYC donné")
  @ApiResponse(responseCode = "200", description = "Étudiants trouvés")
  @ApiResponse(responseCode = "404", description = "Aucun étudiant trouvé")
  public ResponseEntity<Page<StudentResponse>> findByStatutKYC(
      @PathVariable KycStatus statutKYC,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<StudentResponse> responses = studentService.findByStatutKYC(statutKYC, pageable);
    return ResponseEntity.ok(responses);
  }

  @GetMapping
  @Operation(
      summary = "Récupérer tous les étudiants",
      description = "Récupère la liste de tous les étudiants")
  @ApiResponse(responseCode = "200", description = "Étudiants récupérés avec succès")
  @ApiResponse(responseCode = "404", description = "Aucun étudiant trouvé")
  public ResponseEntity<Page<StudentResponse>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<StudentResponse> responses = studentService.findAll(pageable);
    return ResponseEntity.ok(responses);
  }

  @PostMapping("/{trackingId}/verify-pin")
  @Operation(
      summary = "Vérifier le code PIN d'un étudiant",
      description = "Vérifie si le code PIN fourni correspond au PIN haché de l'étudiant")
  @ApiResponse(responseCode = "200", description = "PIN vérifié")
  @ApiResponse(responseCode = "404", description = "Étudiant non trouvé")
  @ApiResponse(responseCode = "400", description = "PIN incorrect")
  public ResponseEntity<Map<String, Object>> verifyPin(
      @PathVariable UUID trackingId, @RequestParam String pinCode) {
    boolean isValid = studentService.verifyPin(trackingId, pinCode);
    if (isValid) {
      return ResponseEntity.ok(Map.of("success", true, "message", "PIN correct"));
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("success", false, "message", "PIN incorrect"));
    }
  }


  @GetMapping("/universite/{universiteTrackingId}")
  @Operation(summary = "Récupérer les étudiants par université")
  public ResponseEntity<Page<StudentResponse>> findByUniversite(@PathVariable UUID universiteTrackingId, Pageable pageable) {
    return ResponseEntity.ok(studentService.findByUniversiteTrackingId(universiteTrackingId, pageable));
  }
}
