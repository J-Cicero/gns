package com.backend.gns.student.application.dtos.requests;

import java.time.LocalDate;

public record ScolariteYearRequest(
    String label, LocalDate startDate, LocalDate endDate, boolean isOpen) {}
