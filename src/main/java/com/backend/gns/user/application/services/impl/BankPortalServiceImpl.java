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
import com.backend.gns.core.infrastructure.repositories.BanqueRepository;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.student.domain.models.CompteBancaireUniversite;
import com.backend.gns.student.infrastructure.repositories.CompteBancaireUniversiteRepository;
import com.backend.gns.wallet.domain.models.Versement;
import com.backend.gns.wallet.domain.enums.VersementStatut;
import com.backend.gns.wallet.domain.enums.VersementType;
import com.backend.gns.wallet.infrastructure.repositories.VersementRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
  private final DocumentEtudiantRepository documentEtudiantRepository;
  private final CompteBancaireUniversiteRepository compteBancaireUniversiteRepository;
  private final BoutiqueRepository boutiqueRepository;
  private final VersementRepository versementRepository;
  private final BanqueRepository banqueRepository;

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
        String typeBourseStr = "AUCUNE";
        boolean inscritAnnuel = false;
        boolean inscritDefinitif = false;
        if (currentYear != null) {
          var insOpt = inscriptionAnnuelleRepository.findByStudentAndScolariteYear(s, currentYear);
          if (insOpt.isPresent()) {
            InscriptionAnnuelle ins = insOpt.get();
            inscritAnnuel = true;
            inscritDefinitif = ins.isEstInscritDefinitif();
            bourseTotale = ins.getPlafondAccorde();
            if (ins.getTypeBourse() != null) {
              typeBourseStr = ins.getTypeBourse().name();
            }
          }
        }

        // Dépenses = ce qui a été retiré du wallet de l'étudiant
        // Le solde actuel du wallet étudiant = Bourse - Dépenses
        // Donc Dépenses = Bourse - SoldeActuel
        BigDecimal soldeActuel = s.getWallet() != null ? s.getWallet().getSolde() : BigDecimal.ZERO;
        BigDecimal depenses = bourseTotale.subtract(soldeActuel);
        if (depenses.compareTo(BigDecimal.ZERO) < 0) depenses = BigDecimal.ZERO;

        // Récupérer le document de souche tamponnée
        List<DocumentEtudiant> docs = documentEtudiantRepository.findByStudentTrackingId(s.getTrackingId());
        String urlSouche = docs.stream()
            .filter(d -> d.getType() == TypeDocument.SOUCHE_TAMPONNEE)
            .map(DocumentEtudiant::getUrlFichier)
            .findFirst()
            .orElse(null);

        results.add(
            new StudentLiquidationInfo(
                s.getTrackingId(),
                s.getNom(),
                s.getPrenom(),
                s.getNumEtudiantUniv(),
                bourseTotale,
                depenses,
                soldeActuel,
                be.isVirementEffectue(),
                typeBourseStr,
                urlSouche,
                inscritAnnuel,
                inscritDefinitif));
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
      
      String numCompte = compteBancaireUniversiteRepository.findByUniversiteAndBanque(u, bank)
          .map(CompteBancaireUniversite::getNumeroCompte)
          .orElse("Non configuré");

      results.add(new UniversityReversementInfo(
          u.getTrackingId(),
          u.getNom(),
          u.getCode(),
          entry.getValue(),
          numCompte
      ));
    }
    return results;
  }

  @Override
  @Transactional(readOnly = true)
  public List<BoutiqueLiquidationInfo> getBoutiquesForBank(UUID bankOperatorTrackingId) {
    BankOperator operator =
        bankOperatorRepository
            .findByTrackingId(bankOperatorTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Opérateur bancaire non trouvé"));

    Banque bank = operator.getBanquePartenaire();
    if (bank == null) {
      return new ArrayList<>();
    }

    List<Boutique> boutiques = boutiqueRepository.findAll().stream()
        .filter(b -> b.getBanquePartenaire() != null 
                  && b.getBanquePartenaire().getId().equals(bank.getId()))
        .toList();

    List<BoutiqueLiquidationInfo> results = new ArrayList<>();
    for (Boutique b : boutiques) {
      BigDecimal solde = b.getWallet() != null ? b.getWallet().getSolde() : BigDecimal.ZERO;
      String proprietaire = b.getMerchant() != null ? (b.getMerchant().getNom() + " " + b.getMerchant().getPrenom()) : "Inconnu";
      results.add(new BoutiqueLiquidationInfo(
          b.getTrackingId(),
          b.getNomBoutique(),
          b.getCategorieShop(),
          b.getNumeroCompte() != null ? b.getNumeroCompte() : "Non renseigné",
          solde,
          proprietaire
      ));
    }
    return results;
  }

  @Override
  @Transactional
  public void liquidateBoutiqueWallet(UUID boutiqueTrackingId) {
    Boutique boutique = boutiqueRepository.findByTrackingId(boutiqueTrackingId)
        .orElseThrow(() -> new EntityNotFoundException("Boutique non trouvée"));
    Wallet w = boutique.getWallet();
    if (w != null && w.getSolde().compareTo(BigDecimal.ZERO) > 0) {
      BigDecimal balance = w.getSolde();
      w.setSolde(BigDecimal.ZERO);
      walletRepository.save(w);

      Versement v = new Versement();
      v.setTrackingId(UUID.randomUUID());
      v.setWallet(w);
      v.setMontantVerse(balance);
      v.setDateVersement(LocalDateTime.now());
      v.setTypeVersement(VersementType.RECHARGE_QUOTA_BOUTIQUE);
      v.setStatut(VersementStatut.VALIDEE);
      versementRepository.save(v);
    }
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

    return new BanqueInfo(bank.getTrackingId(), bank.getCode(), bank.getNom(), bank.getLogoUrl());
  }

  @Override
  @Transactional(readOnly = true)
  public BankFinancialSummary getFinancialSummary(UUID bankOperatorTrackingId) {
    BankOperator operator =
        bankOperatorRepository
            .findByTrackingId(bankOperatorTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Opérateur bancaire non trouvé"));

    Banque bank = operator.getBanquePartenaire();
    if (bank == null) {
      return new BankFinancialSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    List<Student> students = studentRepository.findAll().stream()
        .filter(s -> s.getBanqueEtudiant() != null 
                  && s.getBanqueEtudiant().getBanque() != null 
                  && s.getBanqueEtudiant().getBanque().getId().equals(bank.getId()))
        .toList();

    BigDecimal totalScolarite = BigDecimal.ZERO;
    BigDecimal totalDepensesAchats = BigDecimal.ZERO;
    BigDecimal totalCommissionsAchats = BigDecimal.ZERO;
    BigDecimal totalNetCommercants = BigDecimal.ZERO;

    for (Student s : students) {
      List<Paiement> paiementsScolarite = paiementRepository.findByStudentAndTypePaiementAndStatutPaiement(
          s, PaiementType.SCOLARITE, PaiementStatut.VALIDE);
      
      BigDecimal sumScolarite = paiementsScolarite.stream()
          .map(Paiement::getMontantDebite)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      totalScolarite = totalScolarite.add(sumScolarite);

      List<Paiement> paiementsAchats = paiementRepository.findByStudentAndTypePaiementAndStatutPaiement(
          s, PaiementType.ACHAT, PaiementStatut.VALIDE);

      BigDecimal sumDepenses = paiementsAchats.stream()
          .map(Paiement::getMontantDebite)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      totalDepensesAchats = totalDepensesAchats.add(sumDepenses);

      BigDecimal sumCommissions = paiementsAchats.stream()
          .map(Paiement::getCommission)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      totalCommissionsAchats = totalCommissionsAchats.add(sumCommissions);

      BigDecimal sumNet = paiementsAchats.stream()
          .map(Paiement::getMontantNetBoutique)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      totalNetCommercants = totalNetCommercants.add(sumNet);
    }

    return new BankFinancialSummary(
        totalScolarite,
        totalDepensesAchats,
        totalCommissionsAchats,
        totalNetCommercants
    );
  }

  @Override
  @Transactional
  public void updateBoutiqueAccountNumber(UUID boutiqueTrackingId, String numeroCompte) {
    Boutique boutique = boutiqueRepository.findByTrackingId(boutiqueTrackingId)
        .orElseThrow(() -> new EntityNotFoundException("Boutique non trouvée"));
    boutique.setNumeroCompte(numeroCompte);
    boutiqueRepository.save(boutique);
  }

  @Override
  @Transactional
  public void updateBanqueLogo(UUID bankOperatorTrackingId, String logoUrl) {
    BankOperator operator =
        bankOperatorRepository
            .findByTrackingId(bankOperatorTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Opérateur bancaire non trouvé"));
    Banque bank = operator.getBanquePartenaire();
    if (bank == null) {
      throw new IllegalStateException("Aucune banque partenaire associée à cet opérateur");
    }
    bank.setLogoUrl(logoUrl);
    banqueRepository.save(bank);
  }
}
