package com.backend.gns.wallet.domain.enums;

public enum WalletFundingLevel {
  EPUISE,    // Solde = 0
  CRITIQUE,  // Solde <= 10% du plafond
  FAIBLE,    // Solde <= 30% du plafond
  NORMAL     // Solde > 30% du plafond
}
