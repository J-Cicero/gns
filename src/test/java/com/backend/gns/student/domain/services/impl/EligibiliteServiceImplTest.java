package com.backend.gns.student.domain.services.impl;

import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.services.EligibiliteService;
import com.backend.gns.parametrage.domain.services.ParametreDbsService;
import com.backend.gns.parametrage.domain.enums.TypeParametreDbs;
import com.backend.gns.core.domain.enums.KycStatus;
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
@DisplayName("EligibiliteService - Vérification d'Éligibilité Étudiante (V3)")
class EligibiliteServiceImplTest {

    private EligibiliteService eligibiliteService;
    
    @Mock
    private ParametreDbsService regleBourseService;
    
    private Student student;
    private InscriptionAnnuelle inscription;
    private BanqueEtudiant banque;

    private static final BigDecimal MONTANT_STD = new BigDecimal("36000");
    private static final BigDecimal MONTANT_SUP = new BigDecimal("54000");

    @BeforeEach
    void setup() {
        eligibiliteService = new EligibiliteServiceImpl(regleBourseService);

        // Configuration des mocks avec les nouvelles clés
        lenient().when(regleBourseService.getValeurAsInteger(TypeParametreDbs.AGE_MAX_LICENCE)).thenReturn(26);
        lenient().when(regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.MONTANT_TRANCHE_STANDARD)).thenReturn(MONTANT_STD);
        lenient().when(regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.MONTANT_TRANCHE_SUPERIEURE)).thenReturn(MONTANT_SUP);
        
        lenient().when(regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.L1_MOYENNE_MIN_PASSABLE)).thenReturn(new BigDecimal("10"));
        lenient().when(regleBourseService.getValeurAsBigDecimal(TypeParametreDbs.L1_MOYENNE_MIN_MENTION_SUP)).thenReturn(new BigDecimal("12"));

        lenient().when(regleBourseService.getValeurAsInteger(TypeParametreDbs.L2_CREDITS_MIN_STANDARD)).thenReturn(25);
        lenient().when(regleBourseService.getValeurAsInteger(TypeParametreDbs.L2_CREDITS_MIN_SUPERIEUR)).thenReturn(54);

        lenient().when(regleBourseService.getValeurAsInteger(TypeParametreDbs.L3_CREDITS_MIN_STANDARD)).thenReturn(50);
        lenient().when(regleBourseService.getValeurAsInteger(TypeParametreDbs.L3_CREDITS_MIN_SUPERIEUR)).thenReturn(108);

        lenient().when(regleBourseService.getValeurAsInteger(TypeParametreDbs.L4_CREDITS_MIN_STANDARD)).thenReturn(80);
        lenient().when(regleBourseService.getValeurAsInteger(TypeParametreDbs.L5_CREDITS_MIN_STANDARD)).thenReturn(150);

        // Étudiant de base
        student = new Student();
        student.setDateNaissance(LocalDateTime.now().minusYears(22));
        student.setStatutKYC(KycStatus.VALIDEE);

        // Inscription de base
        inscription = new InscriptionAnnuelle();
        inscription.setNiveau(StudentNiveau.L1_ANNEE);
        inscription.setMoyenneBac(new BigDecimal("11.50"));
        inscription.setEstBoursier(true);
        inscription.setStatut(StatutInscription.ACTIVE);
        inscription.setCreditsTotalValides(0);

        // Mandat de base
        banque = new BanqueEtudiant();
        banque.setMandatSigne(true);
        banque.setMandatStatut(MandatStatut.VALIDE);
    }

    @Test
    @DisplayName("L1 avec moyenne 11.50 → 36 000 FCFA")
    void testL1Standard() {
        inscription.setMoyenneBac(new BigDecimal("11.50"));
        var result = eligibiliteService.verifierEligibilite(student, inscription, banque);
        assertTrue(result.estEligible);
        assertEquals(MONTANT_STD, result.plafondAccorde);
    }

    @Test
    @DisplayName("L1 avec moyenne 14.00 → 54 000 FCFA")
    void testL1Superieur() {
        inscription.setMoyenneBac(new BigDecimal("14.00"));
        var result = eligibiliteService.verifierEligibilite(student, inscription, banque);
        assertTrue(result.estEligible);
        assertEquals(MONTANT_SUP, result.plafondAccorde);
    }

    @Test
    @DisplayName("L2 avec 55 crédits → 54 000 FCFA")
    void testL2Superieur() {
        inscription.setNiveau(StudentNiveau.L2_ANNEE);
        inscription.setCreditsTotalValides(55);
        var result = eligibiliteService.verifierEligibilite(student, inscription, banque);
        assertTrue(result.estEligible);
        assertEquals(MONTANT_SUP, result.plafondAccorde);
    }

    @Test
    @DisplayName("L4 Recyclage avec 85 crédits → 36 000 FCFA uniquement")
    void testL4Recyclage() {
        inscription.setNiveau(StudentNiveau.L4_ANNEE);
        inscription.setCreditsTotalValides(85);
        var result = eligibiliteService.verifierEligibilite(student, inscription, banque);
        assertTrue(result.estEligible);
        assertEquals(MONTANT_STD, result.plafondAccorde);
    }

    @Test
    @DisplayName("Licence avec 27 ans → Non éligible")
    void testAgeTropGrand() {
        student.setDateNaissance(LocalDateTime.now().minusYears(27));
        var result = eligibiliteService.verifierEligibilite(student, inscription, banque);
        assertFalse(result.estEligible);
        assertTrue(result.motifRejet.contains("Âge"));
    }
}
