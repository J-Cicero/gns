package com.backend.gns.commerce.application.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentLiquidationInfoResponse {
    private UUID studentTrackingId;
    private String nom;
    private String prenom;
    private String numEtudiant;
    private BigDecimal bourseTotale;
    private BigDecimal depensesStudCash;
    private BigDecimal resteAPayer;
    private Boolean virementEffectue;
    private String typeBourse;
    private String urlSoucheTamponnee;
    private Boolean inscritAnnuel;
    private Boolean inscritDefinitif;
    private UUID walletTrackingId;
    private String walletStatus;
    private String numeroCompte;
}
