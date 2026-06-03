package com.backend.gns.user.application.services.impl;

import com.backend.gns.admin.domain.models.BankOperator;
import com.backend.gns.admin.infrastructure.repositories.BankOperatorRepository;
import com.backend.gns.student.domain.enums.MandatStatut;
import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.models.Universite;
import com.backend.gns.student.infrastructure.repositories.BanqueEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.user.application.services.BankPortalService;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import com.backend.gns.paiement.infrastructure.repositories.PaiementRepository;
import com.backend.gns.paiement.domain.enums.PaiementType;
import com.backend.gns.paiement.domain.enums.PaiementStatut;
import com.backend.gns.paiement.domain.models.Paiement;
import com.backend.gns.core.domain.models.Banque;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankPortalServiceImpl implements BankPortalService {

  private final StudentRepository studentRepository;
  private final BankOperatorRepository bankOperatorRepository;
  private final InscriptionAnnuelleRepository inscriptionAnnuelleRepository;
  private final ScolariteYearRepository scolariteYearRepository;
  private final BanqueEtudiantRepository banqueEtudiantRepository;
  private final WalletRepository walletRepository;
  private final PaiementRepository paiementRepository;

  @Override
  public List<StudentLiquidationInfo> getStudentsForBank(UUID bankOperatorTrackingId) {
    BankOperator operator =
        bankOperatorRepository
            .findByTrackingId(bankOperatorTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Opérateur bancaire non trouvé"));

    // Normalement on filtrerait par la banque de l'opérateur
    // Ici on va chercher tous les étudiants dont la banqueEtudiant.nomBanque correspondrait
    // ou via une relation directe si on l'ajoute plus tard.

    List<Student> students = studentRepository.findAll(); // À filtrer par banque plus tard
    List<StudentLiquidationInfo> results = new ArrayList<>();

    ScolariteYear currentYear = scolariteYearRepository.findByEstOuverteTrue().orElse(null);

    for (Student s : students) {
      BanqueEtudiant be = s.getBanqueEtudiant();
      if (be != null
          && be.getBanque() != null
          && operator.getBanquePartenaire() != null
          && be.getBanque().getId().equals(operator.getBanquePartenaire().getId())) {

        BigDecimal bourseTotale = BigDecimal.ZERO;
        if (currentYear != null) {
          bourseTotale =
              inscriptionAnnuelleRepository
                  .findByStudentAndScolariteYear(s, currentYear)
                  .map(InscriptionAnnuelle::getPlafondAccorde)
                  .orElse(BigDecimal.ZERO);
        }

        // Dépenses = ce qui a été retiré du wallet de l'étudiant
        // Le solde actuel du wallet étudiant = Bourse - Dépenses
        // Donc Dépenses = Bourse - SoldeActuel
        BigDecimal soldeActuel = s.getWallet() != null ? s.getWallet().getSolde() : BigDecimal.ZERO;
        BigDecimal depenses = bourseTotale.subtract(soldeActuel);
        if (depenses.compareTo(BigDecimal.ZERO) < 0) depenses = BigDecimal.ZERO;

        results.add(
            new StudentLiquidationInfo(
                s.getTrackingId(),
                s.getNom(),
                s.getPrenom(),
                s.getNumEtudiantUniv(),
                bourseTotale,
                depenses,
                soldeActuel, // Ce qui reste sur le wallet est ce qui doit être liquidé à la fin ?
                // Ou plutôt ce qui reste de la bourse non encore chargée sur StudCash ?
                // Selon ton explication : BourseTotale - DépensesStudCash = Reste envoyé sur compte
                // étudiant
                be.isVirementEffectue()));
      }
    }
    return results;
  }

  @Override
  @Transactional
  public void validerMandat(UUID studentTrackingId, boolean valide) {
    Student student =
        studentRepository
            .findByTrackingId(studentTrackingId)
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
    Student student =
        studentRepository
            .findByTrackingId(studentTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));

    BanqueEtudiant be = student.getBanqueEtudiant();
    if (be != null) {
      be.setVirementEffectue(true);
      banqueEtudiantRepository.save(be);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public boolean areStudentWalletsFrozen() {
    List<Wallet> studentWallets = walletRepository.findAll().stream()
        .filter(w -> w.getTypeWallet() == WalletType.STUDENT)
        .toList();
    if (studentWallets.isEmpty()) {
      return false;
    }
    return studentWallets.stream().allMatch(w -> w.getStatutWallet() == WalletStatus.GELE);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UniversityReversementInfo> getUniversityReversementsForBank(UUID bankOperatorTrackingId) {
    BankOperator operator =
        bankOperatorRepository
            .findByTrackingId(bankOperatorTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Opérateur bancaire non trouvé"));

    Banque bank = operator.getBanquePartenaire();
    if (bank == null) {
      return new ArrayList<>();
    }

    List<Student> students = studentRepository.findAll().stream()
        .filter(s -> s.getBanqueEtudiant() != null 
                  && s.getBanqueEtudiant().getBanque() != null 
                  && s.getBanqueEtudiant().getBanque().getId().equals(bank.getId()))
        .toList();

    Map<Universite, BigDecimal> reversementsMap = new HashMap<>();

    for (Student s : students) {
      Universite univ = s.getUniversite();
      if (univ != null) {
        List<Paiement> paiements = paiementRepository.findByStudentAndTypePaiementAndStatutPaiement(
            s, PaiementType.SCOLARITE, PaiementStatut.VALIDE);
        
        BigDecimal totalScolarite = paiements.stream()
            .map(Paiement::getMontantDebite)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalScolarite.compareTo(BigDecimal.ZERO) > 0) {
          reversementsMap.put(univ, reversementsMap.getOrDefault(univ, BigDecimal.ZERO).add(totalScolarite));
        }
      }
    }

    List<UniversityReversementInfo> results = new ArrayList<>();
    for (Map.Entry<Universite, BigDecimal> entry : reversementsMap.entrySet()) {
      Universite u = entry.getKey();
      results.add(new UniversityReversementInfo(
          u.getTrackingId(),
          u.getNom(),
          u.getCode(),
          entry.getValue()
      ));
    }
    return results;
  }

  @Override
  @Transactional(readOnly = true)
  public BanqueInfo getBanqueInfo(UUID bankOperatorTrackingId) {
    BankOperator operator =
        bankOperatorRepository
            .findByTrackingId(bankOperatorTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Opérateur bancaire non trouvé"));

    Banque bank = operator.getBanquePartenaire();
    if (bank == null) {
      throw new IllegalStateException("Aucune banque partenaire associée à cet opérateur");
    }

    return new BanqueInfo(bank.getTrackingId(), bank.getCode(), bank.getNom());
  }
}
