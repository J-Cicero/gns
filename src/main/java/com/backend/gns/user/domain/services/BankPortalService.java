package com.backend.gns.user.domain.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BankPortalService {

  record StudentLiquidationInfo(
      UUID studentTrackingId,
      String nom,
      String prenom,
      String numEtudiant,
      BigDecimal bourseTotale,
      BigDecimal depensesStudCash,
      BigDecimal resteAPayer,
      boolean virementEffectue,
      String typeBourse,
      String urlSoucheTamponnee,
      boolean inscritAnnuel,
      boolean inscritDefinitif,
      UUID walletTrackingId,
      String walletStatus,
      String numeroCompte) {}

  List<StudentLiquidationInfo> getStudentsForBank(UUID bankOperatorTrackingId);

  void validerMandat(UUID studentTrackingId, boolean valide);

  void marquerTraite(UUID studentTrackingId);

  boolean areStudentWalletsFrozen();

  record UniversityReversementInfo(
      UUID universityTrackingId,
      String nomUniversite,
      String codeUniversite,
      BigDecimal montantTotalScolarite,
      String numeroCompteVirement) {}

  List<UniversityReversementInfo> getUniversityReversementsForBank(UUID bankOperatorTrackingId);

  record BoutiqueLiquidationInfo(
      UUID boutiqueTrackingId,
      String nomBoutique,
      String categorieShop,
      String numeroCompte,
      BigDecimal soldeWallet,
      String proprietaireNom,
      UUID walletTrackingId,
      String walletStatus) {}

  List<BoutiqueLiquidationInfo> getBoutiquesForBank(UUID bankOperatorTrackingId);

  void liquidateBoutiqueWallet(UUID boutiqueTrackingId);

  record BanqueInfo(
      UUID trackingId,
      String code,
      String nom,
      String logoUrl) {}

  BanqueInfo getBanqueInfo(UUID bankOperatorTrackingId);

  record BankFinancialSummary(
      BigDecimal totalScolariteUniversites,
      BigDecimal totalDepensesAchats,
      BigDecimal totalCommissionsAchats,
      BigDecimal totalNetCommercants) {}

  BankFinancialSummary getFinancialSummary(UUID bankOperatorTrackingId);

  void updateBoutiqueAccountNumber(UUID boutiqueTrackingId, String numeroCompte);

  void updateBanqueLogo(UUID bankOperatorTrackingId, String logoUrl);

  record BoutiqueVersementInfo(
      UUID versementTrackingId,
      String nomBoutique,
      String proprietaireNom,
      String numeroCompte,
      BigDecimal montantVerse,
      java.time.LocalDateTime dateVersement,
      String typeVersement,
      String statut) {}

  List<BoutiqueVersementInfo> getBoutiqueVersementsForBank(UUID bankOperatorTrackingId);
}
