package com.backend.gns.student.domain.services.impl;

import com.backend.gns.Shared.domain.enums.KycStatus;
import com.backend.gns.student.domain.enums.MandatStatut;
import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.enums.TypeParametreDbs;
import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.EligibiliteService;
import com.backend.gns.student.domain.services.ParametreDbsService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EligibiliteServiceImpl implements EligibiliteService {

    private final ParametreDbsService regleBourseService;

    @Override
    public EligibiliteResult verifierEligibilite(
            Student student, InscriptionAnnuelle inscription, BanqueEtudiant banque) {

        if (student == null || inscription == null) {
            return EligibiliteResult.nonEligible("Données étudiant ou inscription manquantes");
        }

        // 1. Vérification de l'âge (Max 26 ans pour Licence)
        String motifAge = verifierAge(student);
        if (motifAge != null) {
            return EligibiliteResult.nonEligible(motifAge);
        }

        // 2. Vérification administrative
        if (student.getStatutKYC() != KycStatus.VALIDEE) {
            return EligibiliteResult.nonEligible("KYC non validé");
        }

        if (banque == null || banque.getMandatStatut() != MandatStatut.VALIDE || !banque.isMandatSigne()) {
            return EligibiliteResult.nonEligible("Mandat bancaire absent ou non validé");
        }

        if (inscription.getStatut() != StatutInscription.ACTIVE) {
            return EligibiliteResult.nonEligible("Inscription non active");
        }

        if (!inscription.isEstBoursier()) {
            return EligibiliteResult.nonEligible("Étudiant non déclaré boursier par la DBS");
        }

        // 3. Calcul de l'éligibilité financière par niveau
        return calculerEligibiliteFinanciere(inscription);
    }

    private String verifierAge(Student student) {
        if (student.getDateNaissance() == null) {
            return "Date de naissance manquante";
        }
        int age = Period.between(student.getDateNaissance().toLocalDate(), LocalDate.now()).getYears();
        int ageMax = regleBourseService.getValeurAsInteger(TypeParametreDbs.AGE_MAX_LICENCE);

        if (age > ageMax) {
            return String.format("Âge limite dépassé (%d ans > %d ans)", age, ageMax);
        }
        return null;
    }

    private EligibiliteResult calculerEligibiliteFinanciere(InscriptionAnnuelle ins) {
        return switch (ins.getNiveau()) {
            case L1_ANNEE -> verifierL1(ins.getMoyenneBac());
            case L2_ANNEE -> verifierCredits(ins.getCreditsTotalValides(), 
                    TypeParametreDbs.L2_CREDITS_MIN_STANDARD, TypeParametreDbs.L2_CREDITS_MIN_SUPERIEUR);
            case L3_ANNEE -> verifierCredits(ins.getCreditsTotalValides(), 
                    TypeParametreDbs.L3_CREDITS_MIN_STANDARD, TypeParametreDbs.L3_CREDITS_MIN_SUPERIEUR);
            case L4_ANNEE -> verifierCreditsRecyclage(ins.getCreditsTotalValides(), TypeParametreDbs.L4_CREDITS_MIN_STANDARD);
            case L5_ANNEE -> verifierCreditsRecyclage(ins.getCreditsTotalValides(), TypeParametreDbs.L5_CREDITS_MIN_STANDARD);
            default -> EligibiliteResult.nonEligible("Niveau non géré pour la bourse DBS");
        };
    }

    private EligibiliteResult verifierL1(BigDecimal moyenne) {
        if (moyenne == null) return EligibiliteResult.nonEligible("Moyenne BAC manquante");

        BigDecimal minPassable = regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.L1_MOYENNE_MIN_PASSABLE);
        BigDecimal minMentionSup = regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.L1_MOYENNE_MIN_MENTION_SUP);

        if (moyenne.compareTo(minMentionSup) >= 0) {
            return EligibiliteResult.eligible(regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.MONTANT_TRANCHE_SUPERIEURE));
        } else if (moyenne.compareTo(minPassable) >= 0) {
            return EligibiliteResult.eligible(regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.MONTANT_TRANCHE_STANDARD));
        }
        return EligibiliteResult.nonEligible("Moyenne insuffisante pour L1");
    }

    private EligibiliteResult verifierCredits(int credits, TypeParametreDbs regleMin, TypeParametreDbs regleSup) {
        int minStandard = regleBourseService.getValeurAsInteger(regleMin);
        int minSuperieur = regleBourseService.getValeurAsInteger(regleSup);

        if (credits >= minSuperieur) {
            return EligibiliteResult.eligible(regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.MONTANT_TRANCHE_SUPERIEURE));
        } else if (credits >= minStandard) {
            return EligibiliteResult.eligible(regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.MONTANT_TRANCHE_STANDARD));
        }
        return EligibiliteResult.nonEligible(String.format("Crédits insuffisants (%d < %d)", credits, minStandard));
    }

    private EligibiliteResult verifierCreditsRecyclage(int credits, TypeParametreDbs regleMin) {
        int min = regleBourseService.getValeurAsInteger(regleMin);
        if (credits >= min) {
            return EligibiliteResult.eligible(regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.MONTANT_TRANCHE_STANDARD));
        }
        return EligibiliteResult.nonEligible(String.format("Crédits insuffisants pour recyclage (%d < %d)", credits, min));
    }
}
