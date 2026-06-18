package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record StudentRequest(
    String email,
    String password,
    String lastName,
    String firstName,
    Boolean isActive,
    String phoneNumber,
    LocalDateTime birthDate,
    String birthPlace, 
    String studentIdNumber,
    KycStatus kycStatus,
    UUID walletTrackingId,
    UUID bankTrackingId,
    String accountNumber,
    UUID universiteTrackingId
   ) {}
