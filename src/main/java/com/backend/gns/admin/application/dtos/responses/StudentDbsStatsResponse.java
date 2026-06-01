package com.backend.gns.admin.application.dtos.responses;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDbsStatsResponse {
  private String trackingId;
  private String nom;
  private String prenom;
  private String numEtudiantUniv;
  private String universiteNom;
  private String typeBourse;
  private BigDecimal argentDepense;
  private boolean paiementScolariteFait;
  private BigDecimal montantScolarite;
}
