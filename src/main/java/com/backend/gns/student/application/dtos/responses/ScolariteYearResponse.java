package com.backend.gns.student.application.dtos.responses;

import java.time.LocalDate;
import java.util.UUID;

public record ScolariteYearResponse(
    UUID trackingId,
    String libelle,
    LocalDate dateDebut,
    LocalDate dateFin,
    boolean estOuverte,
    boolean estCloturee
) {}
