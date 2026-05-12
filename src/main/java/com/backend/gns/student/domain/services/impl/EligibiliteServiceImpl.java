package com.backend.gns.student.domain.services.impl;

import com.backend.gns.Shared.domain.enums.KycStatus;
import com.backend.gns.student.domain.enums.MandatStatut;
import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.EligibiliteService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EligibiliteServiceImpl implements EligibiliteService {

  private static final int AGE_MAX_LICENCE = 25;
  private static final int AGE_MAX_MASTER  = 30;

  private static final BigDecimal PLAFOND_STANDARD = BigDecimal.valueOf(36_000);
  private static final BigDecimal PLAFOND_SUPERIEUR = BigDecimal.valueOf(54_000);

  // ─────────────────────────────────────────────
  // Point d'entrée principal
  // ─────────────────────────────────────────────

  @Override
  public EligibiliteResult verifierEligibilite(
      Student student, InscriptionAnnuelle inscription, BanqueEtudiant banque) {

    if (student == null || inscription == null) {
      return EligibiliteResult.nonEligible("Données étudiant ou inscription manquantes");
    }

    String motifAge = verifierAge(student, inscription.getNiveau());
    if (motifAge != null) {
      log.info("Étudiant {} non éligible — motif: {}", student.getTrackingId(), motifAge);
      return EligibiliteResult.nonEligible(motifAge);
    }

    if (student.getStatutKYC() != KycStatus.VALIDEE) {
      String motif = "KYC non validé. Statut actuel: " + student.getStatutKYC();
      log.info("Étudiant {} non éligible — motif: {}", student.getTrackingId(), motif);
      return EligibiliteResult.nonEligible(motif);
    }

    if (banque == null || !isMandatValide(banque)) {
      String motif = "Mandat bancaire non validé ou absent";
      log.info("Étudiant {} non éligible — motif: {}", student.getTrackingId(), motif);
      return EligibiliteResult.nonEligible(motif);
    }

    if (inscription.getStatut() != StatutInscription.ACTIVE) {
      String motif = "Inscription non validée. Statut actuel: " + inscription.getStatut();
      log.info("Étudiant {} non éligible — motif: {}", student.getTrackingId(), motif);
      return EligibiliteResult.nonEligible(motif);
    }

    if (!inscription.isEstBoursier()) {
      String motif = "Étudiant non boursier";
      log.info("Étudiant {} non éligible — motif: {}", student.getTrackingId(), motif);
      return EligibiliteResult.nonEligible(motif);
    }

    EligibiliteResult resultat = calculerPlafond(inscription);
    log.info("Étudiant {} — résultat éligibilité: eligible={}, plafond={}",
        student.getTrackingId(), resultat.estEligible, resultat.plafondAccorde);
    return resultat;
  }

  private String verifierAge(Student student, StudentNiveau niveau) {
    if (student.getDateNaissance() == null) {
      return "Date de naissance manquante";
    }

    int age = Period.between(student.getDateNaissance().toLocalDate(), LocalDate.now()).getYears();
    int ageMax = isLicence(niveau) ? AGE_MAX_LICENCE : isMaster(niveau) ? AGE_MAX_MASTER : 0;
    String cycle = isLicence(niveau) ? "Licence" : "Master";

    if (ageMax > 0 && age > ageMax) {
      return String.format("Âge dépassé pour %s. Âge: %d ans, max: %d ans", cycle, age, ageMax);
    }

    return null;
  }
  
  private boolean isMandatValide(BanqueEtudiant banque) {
    return banque.getMandatStatut() == MandatStatut.VALIDE && banque.isMandatSigne();
  }

  // ─────────────────────────────────────────────
  // Calcul du plafond selon le niveau
  // ─────────────────────────────────────────────

 
  private EligibiliteResult calculerPlafond(InscriptionAnnuelle inscription) {
    return switch (inscription.getNiveau()) {
      case L1_ANNEE                                    -> calculerPlafondL1(inscription.getMentionBac());
      case L2_ANNEE, L3_ANNEE, L4_ANNEE, L5_ANNEE    -> calculerPlafondParCreditsLicence(inscription.getCreditsTotalValides());
      case M1_ANNEE                                    -> EligibiliteResult.eligible(PLAFOND_SUPERIEUR);
      case M2_ANNEE, M3_ANNEE                         -> calculerPlafondParCreditsMaster(inscription.getCreditsTotalValides());
      default -> EligibiliteResult.nonEligible("Niveau académique non reconnu: " + inscription.getNiveau());
    };
  }

  private EligibiliteResult calculerPlafondL1(String mention) {
    if (mention == null || mention.isBlank()) {
      return EligibiliteResult.nonEligible("Mention BAC requise pour L1");
    }

    return switch (mention.toUpperCase().trim()) {
      case "PASSABLE", "ASSEZ_BIEN" -> EligibiliteResult.eligible(PLAFOND_STANDARD);
      case "BIEN", "TRES_BIEN"      -> EligibiliteResult.eligible(PLAFOND_SUPERIEUR);
      default -> EligibiliteResult.nonEligible("Mention BAC non reconnue: " + mention);
    };
  }

  private EligibiliteResult calculerPlafondParCreditsLicence(int credits) {
    if (credits >= 60) return EligibiliteResult.eligible(PLAFOND_SUPERIEUR);
    if (credits >= 30) return EligibiliteResult.eligible(PLAFOND_STANDARD);
    return EligibiliteResult.nonEligible(
        String.format("Crédits insuffisants pour Licence. Crédits validés: %d, minimum: 30", credits));
  }

  private EligibiliteResult calculerPlafondParCreditsMaster(int credits) {
    if (credits >= 90) return EligibiliteResult.eligible(PLAFOND_SUPERIEUR);
    if (credits >= 45) return EligibiliteResult.eligible(PLAFOND_STANDARD);
    return EligibiliteResult.nonEligible(
        String.format("Crédits insuffisants pour Master. Crédits validés: %d, minimum: 45", credits));
  }

  private boolean isLicence(StudentNiveau niveau) {
    return switch (niveau) {
      case L1_ANNEE, L2_ANNEE, L3_ANNEE, L4_ANNEE, L5_ANNEE -> true;
      default -> false;
    };
  }

  private boolean isMaster(StudentNiveau niveau) {
    return switch (niveau) {
      case M1_ANNEE, M2_ANNEE, M3_ANNEE -> true;
      default -> false;
    };
  }
}