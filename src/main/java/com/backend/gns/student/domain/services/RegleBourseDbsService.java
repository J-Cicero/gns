package com.backend.gns.student.domain.services;

import com.backend.gns.student.application.dtos.requests.RegleBourseDbsRequest;
import com.backend.gns.student.application.dtos.responses.RegleBourseDbsResponse;
import com.backend.gns.student.domain.enums.TypeRegleBourse;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegleBourseDbsService {
    RegleBourseDbsResponse saveOrUpdate(RegleBourseDbsRequest request);
    Optional<RegleBourseDbsResponse> findByTrackingId(UUID trackingId);
    Page<RegleBourseDbsResponse> findAll(Pageable pageable);
    Optional<RegleBourseDbsResponse> findByTypeRegle(TypeRegleBourse type);
    BigDecimal getValeurCritere(TypeRegleBourse type);
    Integer getValeurCritereAsInteger(TypeRegleBourse type);
}
