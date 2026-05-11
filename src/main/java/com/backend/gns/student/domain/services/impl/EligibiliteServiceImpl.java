package com.backend.gns.student.domain.services.impl;

import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.services.EligibiliteService;
import com.backend.gns.Shared.domain.enums.KycStatus;
import com.backend.gns.student.domain.enums.MandatStatut;
import com.backend.gns.student.domain.enums.StatutInscription;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Service
public class EligibiliteServiceImpl implements EligibiliteService {

  private static final int AGE_MAX_LICENCE = 25;
  private static final int AGE_MAX_MASTER = 30;

  private static final BigDecimal PLAFOND_LICENCE_BASE = BigDecimal.valueOf(36000);
  private static final BigDecimal PLAFOND_MASTER_BASE = BigDecimal.valueOf(54000);

  @Override
  public EligibiliteResult verifierEligibilite(Student student, InscriptionAnnuelle inscription, BanqueEtudiant banque) {

    if (student == null || inscription == null) {
      return EligibiliteResult.nonEligible("Données étudiant ou inscription manquantes");
    }

    // 1️⃣ Vérifier l'âge
    String motifAge = verifierAge(student, inscription.getNiveau());
    if (motifAge != null) {
      return EligibiliteResult.nonEligible(motifAge);
    }

    // 2️⃣ Vérifier le KYC (documents validés)
    if (student.getStatutKYC() != KycStatus.VALIDEE) {
      return EligibiliteResult.nonEligible("KYC non validé. Statut actuel: " + student.getStatutKYC());
    }

    // 3️⃣ Vérifier le mandat bancaire
    if (banque == null || !isMandatValide(banque)) {
      return EligibiliteResult.nonEligible("Mandat bancaire non validé ou absent");
    }

    // 4️⃣ Vérifier le statut d'inscription
    if (inscription.getStatut() != StatutInscription.ACTIVE) {
      return EligibiliteResult.nonEligible("Inscription non validée. Statut actuel: " + inscription.getStatut());
    }

    // 5️⃣ Vérifier que l'étudiant est boursier
    if (!inscription.isEstBoursier()) {
      return EligibiliteResult.nonEligible("Étudiant non boursier");
    }

    // 6️⃣ Vérifier les critères académiques et calculer plafond
    EligibiliteResult resultat = calculerPlafond(inscription);
    if (!resultat.estEligible) {
      return resultat;
    }

    return resultat;
  }

  /**
   * 1️⃣ Vérifier l'âge selon le niveau (Licence: 25 ans max, Master: 30 ans max)
   */
  private String verifierAge(Student student, StudentNiveau niveau) {
    if (student.getDateNaissance() == null) {
      return "Date de naissance manquante";
    }

    LocalDate today = LocalDate.now();
    int age = Period.between(student.getDateNaissance().toLocalDate(), today).getYears();

    if (isLicence(niveau)) {
      if (age > AGE_MAX_LICENCE) {
        return String.format("Âge dépassé pour Licence. Âge: %d ans, max: %d ans", age, AGE_MAX_LICENCE);
      }
    } else if (isMaster(niveau)) {
      if (age > AGE_MAX_MASTER) {
        return String.format("Âge dépassé pour Master. Âge: %d ans, max: %d ans", age, AGE_MAX_MASTER);
      }
    }

    return null;
  }

  /**
   * 3️⃣ Vérifier que le mandat bancaire est validé
   */
  private boolean isMandatValide(BanqueEtudiant banque) {
    return banque.getMandatStatut() == MandatStatut.VALIDE && banque.isMandatSigne();
  }

  /**
   * 6️⃣ Calculer le plafond selon le niveau et les critères académiques
   *
   * LICENCE (L1-L5):
   *   L1: Plafond basé sur mention BAC
   *       - PASSABLE → 36 000 FCFA
   *       - ASSEZ_BIEN/BIEN → 54 000 FCFA
   *   L2/L3: Plafond basé sur crédits ECTS validés
   *       - >= 60 crédits → 54 000 FCFA
   *       - >= 30 crédits → 36 000 FCFA
   *       - < 30 crédits → Non éligible
   *
   * MASTER (M1-M3):
   *   M1: 54 000 FCFA (si bac valide ou L3 complète)
   *   M2/M3: Plafond basé sur crédits ECTS validés
   *       - >= 90 crédits → 54 000 FCFA
   *       - >= 45 crédits → 36 000 FCFA
   *       - < 45 crédits → Non éligible
   */
  private EligibiliteResult calculerPlafond(InscriptionAnnuelle inscription) {
    StudentNiveau niveau = inscription.getNiveau();
    int credits = inscription.getCreditsTotalValides();
    String mention = inscription.getMentionBac();

    // LICENCE
    if (niveau == StudentNiveau.L1_ANNEE) {
      return calculerPlafondL1(mention);
    } else if (niveau == StudentNiveau.L2_ANNEE || niveau == StudentNiveau.L3_ANNEE) {
      return calculerPlafondL2L3(credits);
    } else if (niveau == StudentNiveau.L4_ANNEE || niveau == StudentNiveau.L5_ANNEE) {
      return calculerPlafondL2L3(credits);
    }

    if (niveau == StudentNiveau.M1_ANNEE) {
      return EligibiliteResult.eligible(PLAFOND_MASTER_BASE);
    } else if (niveau == StudentNiveau.M2_ANNEE || niveau == StudentNiveau.M3_ANNEE) {
      return calculerPlafondM2M3(credits);
    }

    return EligibiliteResult.nonEligible("Niveau académique non reconnu: " + niveau);
  }


  private EligibiliteResult calculerPlafondL1(String mention) {
    if (mention == null || mention.isEmpty()) {
      return EligibiliteResult.nonEligible("Mention BAC requise pour L1");
    }

    if (mention.equalsIgnoreCase("PASSABLE")) {
      return EligibiliteResult.eligible(PLAFOND_LICENCE_BASE);
    } else if (mention.equalsIgnoreCase("ASSEZ_BIEN") || mention.equalsIgnoreCase("BIEN")) {
      return EligibiliteResult.eligible(BigDecimal.valueOf(54000));
    } else {
      return EligibiliteResult.nonEligible("Mention BAC non reconnue: " + mention);
    }
  }


  private EligibiliteResult calculerPlafondL2L3(int credits) {
    if (credits >= 60) {
      return EligibiliteResult.eligible(BigDecimal.valueOf(54000));
    } else if (credits >= 30) {
      return EligibiliteResult.eligible(PLAFOND_LICENCE_BASE);
    } else {
      return EligibiliteResult.nonEligible(
          String.format("Crédits insuffisants pour Licence. Crédits validés: %d, minimum: 30", credits));
    }
  }

  /**
   * Calcul du plafond pour M2/M3 selon les crédits validés
   */
  private EligibiliteResult calculerPlafondM2M3(int credits) {
    if (credits >= 90) {
      return EligibiliteResult.eligible(PLAFOND_MASTER_BASE);
    } else if (credits >= 45) {
      return EligibiliteResult.eligible(PLAFOND_LICENCE_BASE);
    } else {
      return EligibiliteResult.nonEligible(
          String.format("Crédits insuffisants pour Master. Crédits validés: %d, minimum: 45", credits));
    }
  }

  /**
   * Vérifier si le niveau est Licence
   */
  private boolean isLicence(StudentNiveau niveau) {
    return niveau == StudentNiveau.L1_ANNEE ||
           niveau == StudentNiveau.L2_ANNEE ||
           niveau == StudentNiveau.L3_ANNEE ||
           niveau == StudentNiveau.L4_ANNEE ||
           niveau == StudentNiveau.L5_ANNEE;
  }

  /**
   * Vérifier si le niveau est Master
   */
  private boolean isMaster(StudentNiveau niveau) {
    return niveau == StudentNiveau.M1_ANNEE ||
           niveau == StudentNiveau.M2_ANNEE ||
           niveau == StudentNiveau.M3_ANNEE;
  }
}
