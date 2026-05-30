package com.backend.gns.user.application.services.impl;

import com.backend.gns.user.application.services.BankPortalService;
import com.backend.gns.user.domain.models.BankOperator;
import com.backend.gns.user.infrastructure.repositories.BankOperatorRepository;
import com.backend.gns.paiement.infrastructure.repositories.PaiementRepository;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.enums.MandatStatut;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.BanqueEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import com.backend.gns.student.domain.models.ScolariteYear;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankPortalServiceImpl implements BankPortalService {

    private final StudentRepository studentRepository;
    private final BankOperatorRepository bankOperatorRepository;
    private final InscriptionAnnuelleRepository inscriptionAnnuelleRepository;
    private final ScolariteYearRepository scolariteYearRepository;
    private final BanqueEtudiantRepository banqueEtudiantRepository;

    @Override
    public List<StudentLiquidationInfo> getStudentsForBank(UUID bankOperatorTrackingId) {
        BankOperator operator = bankOperatorRepository.findByTrackingId(bankOperatorTrackingId)
                .orElseThrow(() -> new EntityNotFoundException("Opérateur bancaire non trouvé"));

        // Normalement on filtrerait par la banque de l'opérateur
        // Ici on va chercher tous les étudiants dont la banqueEtudiant.nomBanque correspondrait 
        // ou via une relation directe si on l'ajoute plus tard.
        
        List<Student> students = studentRepository.findAll(); // À filtrer par banque plus tard
        List<StudentLiquidationInfo> results = new ArrayList<>();
        
        ScolariteYear currentYear = scolariteYearRepository.findByEstOuverteTrue().orElse(null);

        for (Student s : students) {
            BanqueEtudiant be = s.getBanqueEtudiant();
            if (be != null && be.getBanque() != null && be.getBanque().name().contains(operator.getNom())) {
                
                BigDecimal bourseTotale = BigDecimal.ZERO;
                if (currentYear != null) {
                    bourseTotale = inscriptionAnnuelleRepository.findByStudentAndScolariteYear(s, currentYear)
                            .map(InscriptionAnnuelle::getPlafondAccorde)
                            .orElse(BigDecimal.ZERO);
                }

                // Dépenses = ce qui a été retiré du wallet de l'étudiant
                // Le solde actuel du wallet étudiant = Bourse - Dépenses
                // Donc Dépenses = Bourse - SoldeActuel
                BigDecimal soldeActuel = s.getWallet() != null ? s.getWallet().getSolde() : BigDecimal.ZERO;
                BigDecimal depenses = bourseTotale.subtract(soldeActuel);
                if (depenses.compareTo(BigDecimal.ZERO) < 0) depenses = BigDecimal.ZERO;

                results.add(new StudentLiquidationInfo(
                    s.getTrackingId(),
                    s.getNom(),
                    s.getPrenom(),
                    s.getNumEtudiantUniv(),
                    bourseTotale,
                    depenses,
                    soldeActuel, // Ce qui reste sur le wallet est ce qui doit être liquidé à la fin ?
                               // Ou plutôt ce qui reste de la bourse non encore chargée sur StudCash ?
                               // Selon ton explication : BourseTotale - DépensesStudCash = Reste envoyé sur compte étudiant
                    be.isVirementEffectue()
                ));
            }
        }
        return results;
    }

    @Override
    @Transactional
    public void validerMandat(UUID studentTrackingId, boolean valide) {
        Student student = studentRepository.findByTrackingId(studentTrackingId)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));
        
        BanqueEtudiant be = student.getBanqueEtudiant();
        if (be != null) {
            be.setMandatStatut(valide ? MandatStatut.VALIDE : MandatStatut.REJETE);
            be.setMandatSigne(valide);
            banqueEtudiantRepository.save(be);
        }
    }

    @Override
    @Transactional
    public void marquerTraite(UUID studentTrackingId) {
        Student student = studentRepository.findByTrackingId(studentTrackingId)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));
        
        BanqueEtudiant be = student.getBanqueEtudiant();
        if (be != null) {
            be.setVirementEffectue(true);
            banqueEtudiantRepository.save(be);
        }
    }
}
