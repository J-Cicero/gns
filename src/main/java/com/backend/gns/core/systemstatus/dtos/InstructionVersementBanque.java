package com.backend.gns.core.systemstatus.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record InstructionVersementBanque(
    UUID beneficiaireTrackingId, // Etudiant ou Universite
    String typeBeneficiaire,     // "ETUDIANT" ou "UNIVERSITE"
    String nomBeneficiaire,
    String emailBeneficiaire,
    String numeroCompteBancaireExterne, // Compte réel où verser l'argent
    BigDecimal montantAVerser
) {}
