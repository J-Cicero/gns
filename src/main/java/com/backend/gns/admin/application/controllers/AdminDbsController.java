package com.backend.gns.admin.application.controllers;

import com.backend.gns.admin.application.dtos.responses.StudentDbsStatsResponse;
import com.backend.gns.admin.domain.services.AdminDbsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin-dbs")
@RequiredArgsConstructor
public class AdminDbsController {

  private final AdminDbsService adminDbsService;

  @GetMapping("/students/stats")
  @PreAuthorize("hasRole('ADMIN_DBS')")
  public ResponseEntity<Page<StudentDbsStatsResponse>> getStudentStats(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    
    Page<StudentDbsStatsResponse> stats = adminDbsService.getStudentStats(page, size);
    return ResponseEntity.ok(stats);
  }
}
