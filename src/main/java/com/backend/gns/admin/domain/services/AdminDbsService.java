package com.backend.gns.admin.domain.services;

import com.backend.gns.admin.application.dtos.responses.StudentDbsStatsResponse;
import org.springframework.data.domain.Page;

public interface AdminDbsService {
  Page<StudentDbsStatsResponse> getStudentStats(int page, int size);
}
