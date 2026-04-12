package com.backend.gns.application.controllers;

import com.backend.gns.domain.dtos.requests.StudentRequest;
import com.backend.gns.domain.dtos.responses.StudentResponse;
import com.backend.gns.domain.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> create(@RequestBody StudentRequest request) {
        StudentResponse response = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll() {
        List<StudentResponse> responses = studentService.getAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<StudentResponse> getOne(@PathVariable UUID trackingId) {
        StudentResponse response = studentService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{trackingId}")
    public ResponseEntity<StudentResponse> update(
            @PathVariable UUID trackingId,
            @RequestBody StudentRequest request) {
        StudentResponse response = studentService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        studentService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{trackingId}/valider-kyc")
    public ResponseEntity<StudentResponse> validerKYC(@PathVariable UUID trackingId) {
        StudentResponse response = studentService.validerKYC(trackingId);
        return ResponseEntity.ok(response);
    }
}
