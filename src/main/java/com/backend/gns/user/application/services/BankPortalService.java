package com.backend.gns.user.application.services;

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
      boolean virementEffectue) {}

  List<StudentLiquidationInfo> getStudentsForBank(UUID bankOperatorTrackingId);

  void validerMandat(UUID studentTrackingId, boolean valide);

  void marquerTraite(UUID studentTrackingId);

  boolean areStudentWalletsFrozen();

  record UniversityReversementInfo(
      UUID universityTrackingId,
      String nomUniversite,
      String codeUniversite,
      BigDecimal montantTotalScolarite) {}

  List<UniversityReversementInfo> getUniversityReversementsForBank(UUID bankOperatorTrackingId);

  record BanqueInfo(
      UUID trackingId,
      String code,
      String nom) {}

  BanqueInfo getBanqueInfo(UUID bankOperatorTrackingId);
}
