package com.backend.gns.student.domain.services.impl;

import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.services.EligibiliteService;
import com.backend.gns.student.domain.services.RegleBourseDbsService;
import com.backend.gns.student.domain.enums.TypeRegleBourse;
import com.backend.gns.Shared.domain.enums.KycStatus;
import com.backend.gns.student.domain.enums.MandatStatut;
import com.backend.gns.student.domain.enums.StatutInscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EligibiliteService - Vérification d'Éligibilité Étudiante")
class EligibiliteServiceImplTest {

    private EligibiliteService eligibiliteService;
    
    @Mock
    private RegleBourseDbsService regleBourseService;
    
    private Student student;
    private InscriptionAnnuelle inscription;
    private BanqueEtudiant banque;

    @BeforeEach
    void setup() {
        eligibiliteService = new EligibiliteServiceImpl(regleBourseService);

        // Configuration par défaut des mocks
        lenient().when(regleBourseService.getValeurCritereAsInteger(TypeRegleBourse.AGE_MAX_LICENCE)).thenReturn(25);
        lenient().when(regleBourseService.getValeurCritereAsInteger(TypeRegleBourse.AGE_MAX_MASTER)).thenReturn(30);
        lenient().when(regleBourseService.getValeurCritere(TypeRegleBourse.L1_MONTANT_STANDARD)).thenReturn(BigDecimal.valueOf(36000));
        lenient().when(regleBourseService.getValeurCritere(TypeRegleBourse.L1_MONTANT_SUPERIEUR)).thenReturn(BigDecimal.valueOf(54000));
        lenient().when(regleBourseService.getValeurCritere(TypeRegleBourse.LICENCE_MIN_30_CREDITS)).thenReturn(BigDecimal.valueOf(36000));
        lenient().when(regleBourseService.getValeurCritere(TypeRegleBourse.LICENCE_MIN_60_CREDITS)).thenReturn(BigDecimal.valueOf(54000));
        lenient().when(regleBourseService.getValeurCritere(TypeRegleBourse.MASTER_MIN_45_CREDITS)).thenReturn(BigDecimal.valueOf(36000));
        lenient().when(regleBourseService.getValeurCritere(TypeRegleBourse.MASTER_MIN_90_CREDITS)).thenReturn(BigDecimal.valueOf(54000));

        // Créer un étudiant de base (22 ans, KYC validé, etc)
        student = new Student();
        student.setDateNaissance(LocalDateTime.of(2002, 1, 15, 0, 0)); // 22 ans
        student.setStatutKYC(KycStatus.VALIDEE);

        // Créer une inscription de base
        inscription = new InscriptionAnnuelle();
        inscription.setNiveau(StudentNiveau.L1_ANNEE);
        inscription.setMentionBac("PASSABLE");
        inscription.setEstBoursier(true);
        inscription.setStatut(StatutInscription.ACTIVE);
        inscription.setCreditsTotalValides(0);

        // Créer un mandat bancaire de base
        banque = new BanqueEtudiant();
        banque.setMandatSigne(true);
        banque.setMandatStatut(MandatStatut.VALIDE);
    }

    // ========== TESTS L1 ==========

    @Test
    @DisplayName("L1 avec mention PASSABLE → 36 000 FCFA")
    void testL1PassableEligible() {
        inscription.setNiveau(StudentNiveau.L1_ANNEE);
        inscription.setMentionBac("PASSABLE");

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(36000), result.plafondAccorde);
    }

    @Test
    @DisplayName("L1 avec mention ASSEZ_BIEN → 36 000 FCFA")
    void testL1AssezBienEligible() {
        inscription.setNiveau(StudentNiveau.L1_ANNEE);
        inscription.setMentionBac("ASSEZ_BIEN");

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(36000), result.plafondAccorde);
    }

    @Test
    @DisplayName("L1 avec mention BIEN → 54 000 FCFA")
    void testL1BienEligible() {
        inscription.setNiveau(StudentNiveau.L1_ANNEE);
        inscription.setMentionBac("BIEN");

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(54000), result.plafondAccorde);
    }

    @Test
    @DisplayName("L1 sans mention BAC → Non éligible")
    void testL1SansMentionNonEligible() {
        inscription.setNiveau(StudentNiveau.L1_ANNEE);
        inscription.setMentionBac(null);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertNotNull(result.motifRejet);
    }

    // ========== TESTS L2/L3 ==========

    @Test
    @DisplayName("L2 avec 60 crédits → 54 000 FCFA")
    void testL2Avec60CreditseEligible() {
        inscription.setNiveau(StudentNiveau.L2_ANNEE);
        inscription.setCreditsTotalValides(60);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(54000), result.plafondAccorde);
    }

    @Test
    @DisplayName("L3 avec 50 crédits → 36 000 FCFA")
    void testL3Avec50CreditEligible() {
        inscription.setNiveau(StudentNiveau.L3_ANNEE);
        inscription.setCreditsTotalValides(50);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(36000), result.plafondAccorde);
    }

    @Test
    @DisplayName("L2 avec 25 crédits (< 30) → Non éligible")
    void testL2Avec25CreditsNonEligible() {
        inscription.setNiveau(StudentNiveau.L2_ANNEE);
        inscription.setCreditsTotalValides(25);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("30"));
    }

    // ========== TESTS MASTER ==========

    @Test
    @DisplayName("M1 automatique → 54 000 FCFA")
    void testM1Eligible() {
        inscription.setNiveau(StudentNiveau.M1_ANNEE);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(54000), result.plafondAccorde);
    }

    @Test
    @DisplayName("M2 avec 90 crédits → 54 000 FCFA")
    void testM2Avec90CreditEligible() {
        inscription.setNiveau(StudentNiveau.M2_ANNEE);
        inscription.setCreditsTotalValides(90);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(54000), result.plafondAccorde);
    }

    @Test
    @DisplayName("M3 avec 60 crédits → 36 000 FCFA")
    void testM3Avec60CreditEligible() {
        inscription.setNiveau(StudentNiveau.M3_ANNEE);
        inscription.setCreditsTotalValides(60);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(36000), result.plafondAccorde);
    }

    @Test
    @DisplayName("M2 avec 30 crédits (< 45) → Non éligible")
    void testM2Avec30CreditsNonEligible() {
        inscription.setNiveau(StudentNiveau.M2_ANNEE);
        inscription.setCreditsTotalValides(30);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("45"));
    }

    // ========== TESTS ÂGE ==========

    @Test
    @DisplayName("Licence avec 25 ans → Éligible")
    void testLicenceAge25Eligible() {
        student.setDateNaissance(LocalDateTime.now().minusYears(25)); // Exactement 25 ans

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
    }

    @Test
    @DisplayName("Licence avec 26 ans → Non éligible")
    void testLicenceAge26NonEligible() {
        student.setDateNaissance(LocalDateTime.now().minusYears(26)); // 26 ans

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("25"));
    }

    @Test
    @DisplayName("Master avec 30 ans → Éligible")
    void testMasterAge30Eligible() {
        student.setDateNaissance(LocalDateTime.now().minusYears(30)); // 30 ans
        inscription.setNiveau(StudentNiveau.M1_ANNEE);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
    }

    @Test
    @DisplayName("Master avec 31 ans → Non éligible")
    void testMasterAge31NonEligible() {
        student.setDateNaissance(LocalDateTime.now().minusYears(31)); // 31 ans
        inscription.setNiveau(StudentNiveau.M1_ANNEE);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("30"));
    }

    // ========== TESTS KYC ==========

    @Test
    @DisplayName("KYC non validé → Non éligible")
    void testKYCNonValideNonEligible() {
        student.setStatutKYC(KycStatus.EN_ATTENTE);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("KYC"));
    }

    @Test
    @DisplayName("KYC rejeté → Non éligible")
    void testKYCRejeteNonEligible() {
        student.setStatutKYC(KycStatus.REJETE);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("KYC"));
    }

    // ========== TESTS MANDAT BANCAIRE ==========

    @Test
    @DisplayName("Mandat non signé → Non éligible")
    void testMandatNonSigneNonEligible() {
        banque.setMandatSigne(false);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("Mandat"));
    }

    @Test
    @DisplayName("Mandat non validé → Non éligible")
    void testMandatNonValideNonEligible() {
        banque.setMandatStatut(MandatStatut.EN_ATTENTE_DEPOT); 

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("Mandat"));
    }

    @Test
    @DisplayName("Pas de banque (null) → Non éligible")
    void testBanqueNullNonEligible() {
        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, null);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("Mandat"));
    }

    // ========== TESTS STATUT INSCRIPTION ==========

    @Test
    @DisplayName("Inscription non validée → Non éligible")
    void testInscriptionNonValideeNonEligible() {
        inscription.setStatut(StatutInscription.EN_ATTENTE);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("Inscription"));
    }

    @Test
    @DisplayName("Inscription rejetée → Non éligible")
    void testInscriptionRejeteeNonEligible() {
        inscription.setStatut(StatutInscription.EXPIREE);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("Inscription"));
    }

    // ========== TESTS STATUT BOURSIER ==========

    @Test
    @DisplayName("Étudiant non boursier → Non éligible")
    void testNonBourierNonEligible() {
        inscription.setEstBoursier(false);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("boursier"));
    }

    // ========== TESTS DONNÉES MANQUANTES ==========

    @Test
    @DisplayName("Student null → Non éligible")
    void testStudentNullNonEligible() {
        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(null, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("manquantes"));
    }

    @Test
    @DisplayName("Inscription null → Non éligible")
    void testInscriptionNullNonEligible() {
        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, null, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("manquantes"));
    }

    @Test
    @DisplayName("DateNaissance null → Non éligible")
    void testDateNaissanceNullNonEligible() {
        student.setDateNaissance(null);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("Date de naissance"));
    }

    // ========== TESTS COMBINAISONS COMPLEXES ==========

    @Test
    @DisplayName("Tous les critères OK: L1 Passable, 22 ans, KYC OK, Mandat OK")
    void testTousCriteresOK() {
        inscription.setNiveau(StudentNiveau.L1_ANNEE);
        inscription.setMentionBac("PASSABLE");
        student.setDateNaissance(LocalDateTime.now().minusYears(22));
        student.setStatutKYC(KycStatus.VALIDEE);
        banque.setMandatSigne(true);
        banque.setMandatStatut(MandatStatut.VALIDE);
        inscription.setEstBoursier(true);
        inscription.setStatut(StatutInscription.ACTIVE);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(36000), result.plafondAccorde);
    }

    @Test
    @DisplayName("L2 excellent: 65 crédits → 54 000 FCFA")
    void testL2Excellent() {
        inscription.setNiveau(StudentNiveau.L2_ANNEE);
        inscription.setCreditsTotalValides(65);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(54000), result.plafondAccorde);
    }

    @Test
    @DisplayName("M2 limite: 45 crédits (juste OK) → 36 000 FCFA")
    void testM2JusteOK() {
        inscription.setNiveau(StudentNiveau.M2_ANNEE);
        inscription.setCreditsTotalValides(45);

        EligibiliteService.EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);

        assertTrue(result.estEligible);
        assertEquals(BigDecimal.valueOf(36000), result.plafondAccorde);
    }
}
