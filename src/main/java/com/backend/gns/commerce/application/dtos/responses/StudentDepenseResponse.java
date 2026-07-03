package com.backend.gns.commerce.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDepenseResponse {
    private UUID trackingId;
    private LocalDateTime date;
    private BigDecimal montant;
    private String boutiqueNom;
}
