package com.backend.gns.core.systemstatus.enums;

public enum SystemStatus {
  INITIALISATION, // Avant la rentrée, paramètres configurables
  ACTIVE, // Année en cours, paiements autorisés
  CLOSURE_PENDING, // Gel des paiements, phase de reporting bancaire
  CLOSED // Année terminée, archivage
}
