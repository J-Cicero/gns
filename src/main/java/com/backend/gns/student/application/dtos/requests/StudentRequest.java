package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.core.domain.enums.KycStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StudentRequest(
    String email,
    String password,
    String lastName,
    String firstName,
    Boolean isActive,
    String phoneNumber,
    LocalDateTime birthDate,
    String studentIdNumber,
    KycStatus kycStatus,
    UUID walletTrackingId,
    UUID bankTrackingId,
    String accountNumber,
    UUID universiteTrackingId,
    String pinCodeHash) {}
