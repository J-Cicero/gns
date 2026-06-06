package com.backend.gns.student.application.dtos.requests;

import java.time.LocalDate;

public record ScolariteYearRequest(
    String libelle, LocalDate dateDebut, LocalDate dateFin, boolean estOuverte) {}
