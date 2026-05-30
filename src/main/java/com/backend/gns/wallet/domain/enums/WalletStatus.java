package com.backend.gns.wallet.domain.enums;

public enum WalletStatus {
  INACTIF, // wallet créé mais KYC pas encore validé
  ACTIF, // fonctionnel, paiements autorisés
  GELE, // temporaire — pendant la réconciliation bancaire DBS
  BLOQUE // permanent — perte bourse, action admin, fraude
}
