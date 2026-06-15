package com.backend.gns.student.application.dtos.responses;

import java.time.LocalDate;
import java.util.UUID;

public record ScolariteYearResponse(
    UUID trackingId,
    String label,
    LocalDate startDate,
    LocalDate endDate,
    boolean isOpen,
    boolean isClosed) {}
