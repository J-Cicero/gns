package com.backend.gns.student.application.controllers;

import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import com.backend.gns.student.domain.services.DocumentEtudiantService;
import com.backend.gns.student.domain.services.StudentService;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/students")
@Tag(name = "STUDENT", description = "Gestion des étudiants")
public class StudentController {

  private final StudentService studentService;
  private final DocumentEtudiantService documentService;

  public StudentController(
      StudentService studentService,
      DocumentEtudiantService documentService) {
    this.studentService = studentService;
    this.documentService = documentService;
  }

  @PostMapping("/{trackingId}/documents/upload")
  @Operation(summary = "Uploader un document pour un étudiant")
  public ResponseEntity<?> uploadDocument(
      @PathVariable UUID trackingId,
      @RequestParam("fichier") MultipartFile fichier,
      @RequestParam(value = "inscriptionTrackingId", required = false) UUID inscriptionTrackingId,
      @RequestParam("typeDocument") TypeDocument typeDocument) {
    try {
      var response = documentService.uploadDocument(fichier, trackingId, inscriptionTrackingId, typeDocument);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "UPLOAD_FAILED", "message", e.getMessage()));
    }
  }

  @GetMapping("/{trackingId}/documents")
  @Operation(summary = "Récupérer les documents d'un étudiant")
  public ResponseEntity<?> getDocuments(@PathVariable UUID trackingId, Pageable pageable) {
    return ResponseEntity.ok(documentService.findByUserTrackingId(trackingId, pageable));
  }

  @PostMapping(consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Créer un étudiant en format JSON")
  public ResponseEntity<StudentResponse> createJson(@RequestBody StudentRequest request) {
    StudentResponse response = studentService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Créer un étudiant avec documents")
  public ResponseEntity<StudentResponse> create(
      @RequestPart(value = "student", required = false) StudentRequest studentRequest,
      @RequestPart(value = "request", required = false) String requestJson,
      @RequestParam(value = "request", required = false) String requestParam,
      @RequestPart(value = "rib", required = false) MultipartFile rib,
      @RequestPart(value = "mandat", required = false) MultipartFile mandat) {
    
    StudentRequest req = studentRequest;
    if (req == null) {
      String json = (requestJson != null && !requestJson.isBlank()) ? requestJson : requestParam;
      if (json != null && !json.isBlank()) {
        try {
          com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
          mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
          req = mapper.readValue(json, StudentRequest.class);
        } catch (Exception e) {
          throw new RuntimeException("Erreur lors de la lecture des données de l'étudiant: " + e.getMessage(), e);
        }
      }
    }

    if (req == null) {
      throw new IllegalArgumentException("Les données de l'étudiant sont obligatoires (part 'request' ou 'student').");
    }
    
    StudentResponse response = studentService.create(req);
    
    if (rib != null) {
      documentService.uploadDocument(rib, response.trackingId(), null, TypeDocument.RIB);
    }
    if (mandat != null) {
      documentService.uploadDocument(mandat, response.trackingId(), null, TypeDocument.MANDAT);
    }
    
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{trackingId}")
  public ResponseEntity<StudentResponse> findByTrackingId(@PathVariable UUID trackingId) {
    return studentService.findByTrackingId(trackingId)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));
  }

  @PutMapping("/{trackingId}")
  public ResponseEntity<StudentResponse> update(@PathVariable UUID trackingId, @RequestBody StudentRequest request) {
    return ResponseEntity.ok(studentService.update(trackingId, request));
  }

  @PatchMapping("/{trackingId}/matricule")
  @Operation(summary = "Assigner un matricule à un étudiant")
  public ResponseEntity<StudentResponse> assignerMatricule(
      @PathVariable UUID trackingId, @RequestParam String matricule) {
    return ResponseEntity.ok(studentService.assignerMatricule(trackingId, matricule));
  }

  @DeleteMapping("/{trackingId}")
  public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
    studentService.delete(trackingId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/kyc/{statutKYC}")
  public ResponseEntity<Page<StudentResponse>> findByStatutKYC(@PathVariable KycStatus statutKYC, Pageable pageable) {
    return ResponseEntity.ok(studentService.findByStatutKYC(statutKYC, pageable));
  }

  @GetMapping
  public ResponseEntity<Page<StudentResponse>> findAll(Pageable pageable) {
    return ResponseEntity.ok(studentService.findAll(pageable));
  }

  @GetMapping("/stats")
  public ResponseEntity<Map<String, Object>> getStats() {
    return ResponseEntity.ok(
        Map.of(
            "totalStudents", studentService.countAll(),
            "activeStudents", studentService.countByEstActif(true),
            "verifiedKyc", studentService.countByStatutKYC(KycStatus.VALIDE)));
  }

  @GetMapping("/stats/total")
  public ResponseEntity<Long> getTotalStudents() {
    return ResponseEntity.ok(studentService.countAll());
  }

  @GetMapping("/{trackingId}/card")
  public ResponseEntity<?> getCard(@PathVariable UUID trackingId) {
    return ResponseEntity.ok(studentService.getCard(trackingId));
  }
}
