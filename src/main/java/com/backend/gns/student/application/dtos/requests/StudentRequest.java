package com.backend.gns.student.application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record StudentRequest(
        String email,
        String password,
        String lastName,
        String firstName,
        String phoneNumber,
        LocalDateTime birthDate,
        String birthPlace,
        String transactionPin,
        @JsonAlias({"studentNumber", "studentIdNumber", "matricule"})
        String studentNumber,
        UUID universiteTrackingId,
        @JsonAlias({"banqueTrackingId", "bankTrackingId"})
        UUID banqueTrackingId,
        @JsonAlias({"numeroCompte", "accountNumber"})
        String numeroCompte
   ) {}
