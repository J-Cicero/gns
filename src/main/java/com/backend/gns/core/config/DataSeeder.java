package com.backend.gns.core.config;

import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.user.domain.models.User;
import com.backend.gns.user.infrastructure.repositories.UserRepository;
import com.backend.gns.core.domain.models.Banque;
import com.backend.gns.core.infrastructure.repositories.BanqueRepository;
import com.backend.gns.admin.domain.models.BankOperator;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.models.Universite;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.domain.models.CompteBancaireUniversite;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import com.backend.gns.student.infrastructure.repositories.BanqueEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.CompteBancaireUniversiteRepository;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import com.backend.gns.core.domain.enums.KycStatus;
import com.backend.gns.student.domain.enums.MandatStatut;
import com.backend.gns.student.domain.enums.TypeBourse;
import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.enums.SourceVerification;
import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.models.ParametreGns;
import com.backend.gns.core.parametrage.infrastructure.repositories.ParametreGnsRepository;
import com.backend.gns.student.domain.enums.TypeParametreDbs;
import com.backend.gns.student.domain.models.ParametreDbs;
import com.backend.gns.student.infrastructure.repositories.ParametreDbsRepository;
import com.backend.gns.paiement.domain.models.Paiement;
import com.backend.gns.paiement.domain.enums.PaiementStatut;
import com.backend.gns.paiement.domain.enums.PaiementType;
import com.backend.gns.paiement.infrastructure.repositories.PaiementRepository;
import com.backend.gns.paiement.domain.models.Commande;
import com.backend.gns.paiement.domain.enums.CommandeStatut;
import com.backend.gns.paiement.infrastructure.repositories.CommandeRepository;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ParametreGnsRepository parametreGnsRepository;
  private final ParametreDbsRepository parametreDbsRepository;
  private final BanqueRepository banqueRepository;
  private final UniversiteRepository universiteRepository;
  private final ScolariteYearRepository scolariteYearRepository;
  private final WalletRepository walletRepository;
  private final BanqueEtudiantRepository banqueEtudiantRepository;
  private final InscriptionAnnuelleRepository inscriptionAnnuelleRepository;
  private final DocumentEtudiantRepository documentEtudiantRepository;
  private final PaiementRepository paiementRepository;
  private final CommandeRepository commandeRepository;
  private final BoutiqueRepository boutiqueRepository;
  private final CompteBancaireUniversiteRepository compteBancaireUniversiteRepository;
  private final TransactionTemplate transactionTemplate;

  @PersistenceContext
  private final EntityManager entityManager;

  @Override
  public void run(String... args) throws Exception {
    transactionTemplate.executeWithoutResult(status -> {
      try {
        seedAll();
      } catch (Exception e) {
        throw new RuntimeException("Erreur de seeding", e);
      }
    });
  }

  @Transactional
  public void seedAll() {
    // 1. Seed base configs
    seedBanques();
    seedParametresGns();
    seedParametresDbs();
    
    // 2. Clean users & polymorphic tables if they are wrong/incomplete
    cleanPolymorphicDataIfNecessary();

    // 3. Seed users
    seedUsersAndEntities();
  }

  private void seedBanques() {
    if (banqueRepository.count() == 0) {
      log.info("Création des banques partenaires...");
      banqueRepository.save(new Banque(null, UUID.randomUUID(), "POSTE", "La Poste", "/assets/logos/poste.png"));
      banqueRepository.save(new Banque(null, UUID.randomUUID(), "ECOBANK", "Ecobank", "/assets/logos/ecobank.png"));
      banqueRepository.save(new Banque(null, UUID.randomUUID(), "ORABANK", "Orabank", "/assets/logos/orabank.png"));
      banqueRepository.save(new Banque(null, UUID.randomUUID(), "IBBANK", "IB Bank", "/assets/logos/ibbank.png"));
      banqueRepository.save(new Banque(null, UUID.randomUUID(), "CORISBANK", "Coris Bank", "/assets/logos/corisbank.png"));
    } else {
      // S'assurer que le logoUrl par défaut est renseigné
      for (Banque b : banqueRepository.findAll()) {
        if (b.getLogoUrl() == null || b.getLogoUrl().trim().isEmpty()) {
          if ("POSTE".equals(b.getCode())) b.setLogoUrl("/assets/logos/poste.png");
          else if ("ECOBANK".equals(b.getCode())) b.setLogoUrl("/assets/logos/ecobank.png");
          else if ("ORABANK".equals(b.getCode())) b.setLogoUrl("/assets/logos/orabank.png");
          else if ("IBBANK".equals(b.getCode())) b.setLogoUrl("/assets/logos/ibbank.png");
          else if ("CORISBANK".equals(b.getCode())) b.setLogoUrl("/assets/logos/corisbank.png");
          banqueRepository.save(b);
        }
      }
    }
  }

  private void seedParametresGns() {
    if (parametreGnsRepository.count() == 0) {
      log.info("Création des Paramètres GNS par défaut...");
      for (TypeParametreGns type : TypeParametreGns.values()) {
        ParametreGns param = new ParametreGns();
        param.setTrackingId(UUID.randomUUID());
        param.setNomParametre(type);
        param.setValeurParametre("1000"); // valeur par défaut
        param.setDescription("Paramètre système: " + type.name());
        parametreGnsRepository.save(param);
      }
    }
  }

  private void seedParametresDbs() {
    if (parametreDbsRepository.count() == 0) {
      log.info("Création des Paramètres DBS par défaut...");
      for (TypeParametreDbs type : TypeParametreDbs.values()) {
        ParametreDbs param = new ParametreDbs();
        param.setTrackingId(UUID.randomUUID());
        param.setNomParametre(type);
        param.setValeurParametre("50000"); // valeur par défaut
        param.setDescription("Paramètre DBS: " + type.name());
        parametreDbsRepository.save(param);
      }
    }
  }

  private void cleanPolymorphicDataIfNecessary() {
    // Désactivé pour empêcher la perte des modifications en cours en base de données lors des rechargements/redémarrages
  }

  private void seedUsersAndEntities() {
    if (userRepository.count() == 0) {
      log.info("Création des utilisateurs typés et de leurs entités associées...");
      
      String encodedPassword = passwordEncoder.encode("password123");
      
      // Seed Universite de Lome
      Universite ul = universiteRepository.findByCode("UL").orElse(null);
      if (ul == null) {
        ul = new Universite();
        ul.setTrackingId(UUID.randomUUID());
        ul.setNom("Université de Lomé");
        ul.setCode("UL");
        ul = universiteRepository.save(ul);
      }

      // Seed Universite de Kara
      Universite uk = universiteRepository.findByCode("UK").orElse(null);
      if (uk == null) {
        uk = new Universite();
        uk.setTrackingId(UUID.randomUUID());
        uk.setNom("Université de Kara");
        uk.setCode("UK");
        uk = universiteRepository.save(uk);
      }

      // Seed ScolariteYear
      ScolariteYear currentYear = scolariteYearRepository.findByEstOuverteTrue().orElse(null);
      if (currentYear == null) {
        currentYear = new ScolariteYear();
        currentYear.setTrackingId(UUID.randomUUID());
        currentYear.setLibelle("2025-2026");
        currentYear.setDateDebut(java.time.LocalDate.of(2025, 9, 1));
        currentYear.setDateFin(java.time.LocalDate.of(2026, 6, 30));
        currentYear.setEstOuverte(true);
        currentYear = scolariteYearRepository.save(currentYear);
      }

      // Seed Banque partenaires references
      Banque ecobank = banqueRepository.findByCode("ECOBANK").orElseThrow();

      // Seed CompteBancaireUniversite for UL and UK
      if (compteBancaireUniversiteRepository.count() == 0) {
        CompteBancaireUniversite cpuUL = new CompteBancaireUniversite();
        cpuUL.setUniversite(ul);
        cpuUL.setBanque(ecobank);
        cpuUL.setNumeroCompte("TG012-00100-348291001-44");
        compteBancaireUniversiteRepository.save(cpuUL);

        CompteBancaireUniversite cpuUK = new CompteBancaireUniversite();
        cpuUK.setUniversite(uk);
        cpuUK.setBanque(ecobank);
        cpuUK.setNumeroCompte("TG012-00200-581902002-88");
        compteBancaireUniversiteRepository.save(cpuUK);
      }

      // 1. Admin GNS
      User adminGns = new User();
      adminGns.setTrackingId(UUID.randomUUID());
      adminGns.setNom("System");
      adminGns.setPrenom("Admin GNS");
      adminGns.setEmail("admin@gns.com");
      adminGns.setMotDePasse(encodedPassword);
      adminGns.setRole(UserRole.ADMIN_GNS);
      adminGns.setTelephone("123456789");
      adminGns.setEstActif(true);
      userRepository.save(adminGns);

      // 2. Admin DBS
      User adminDbs = new User();
      adminDbs.setTrackingId(UUID.randomUUID());
      adminDbs.setNom("Direction");
      adminDbs.setPrenom("Admin DBS");
      adminDbs.setEmail("admin@dbs.com");
      adminDbs.setMotDePasse(encodedPassword);
      adminDbs.setRole(UserRole.ADMIN_DBS);
      adminDbs.setTelephone("123456780");
      adminDbs.setEstActif(true);
      userRepository.save(adminDbs);

      // 3. Admin University
      User adminUniv = new User();
      adminUniv.setTrackingId(UUID.randomUUID());
      adminUniv.setNom("Université");
      adminUniv.setPrenom("Admin");
      adminUniv.setEmail("admin@university.com");
      adminUniv.setMotDePasse(encodedPassword);
      adminUniv.setRole(UserRole.UNIVERSITY_ADMIN);
      adminUniv.setTelephone("123456781");
      adminUniv.setEstActif(true);
      userRepository.save(adminUniv);

      // 4. Bank Operator (as BankOperator subclass)
      BankOperator bankOp = new BankOperator();
      bankOp.setTrackingId(UUID.randomUUID());
      bankOp.setNom("Banque");
      bankOp.setPrenom("Operator");
      bankOp.setEmail("operator@bank.com");
      bankOp.setMotDePasse(encodedPassword);
      bankOp.setRole(UserRole.ADMIN_BANQUE);
      bankOp.setTelephone("123456782");
      bankOp.setEstActif(true);
      bankOp.setNomBanque("Ecobank");
      bankOp.setBanquePartenaire(ecobank);
      
      Wallet bankWallet = new Wallet();
      bankWallet.setTrackingId(UUID.randomUUID());
      bankWallet.setTypeWallet(WalletType.BANK_OPERATOR);
      bankWallet.setStatutWallet(WalletStatus.ACTIF);
      bankWallet.setSolde(new BigDecimal("100000000.00"));
      bankWallet.setPlafond(new BigDecimal("500000000.00"));
      bankWallet.setDateCreation(LocalDateTime.now());
      bankWallet = walletRepository.save(bankWallet);
      bankOp.setWallet(bankWallet);
      userRepository.save(bankOp);

      // 5. Étudiant (as Student subclass)
      Student student = new Student();
      student.setTrackingId(UUID.randomUUID());
      student.setNom("L'Étudiant");
      student.setPrenom("Jean");
      student.setEmail("student@test.com");
      student.setMotDePasse(encodedPassword);
      student.setRole(UserRole.ETUDIANT);
      student.setTelephone("123456783");
      student.setEstActif(true);
      student.setNumEtudiantUniv("123456");
      student.setStatutKYC(KycStatus.VALIDEE);
      student.setUniversite(ul);
      
      Wallet studentWallet = new Wallet();
      studentWallet.setTrackingId(UUID.randomUUID());
      studentWallet.setTypeWallet(WalletType.STUDENT);
      studentWallet.setStatutWallet(WalletStatus.ACTIF);
      studentWallet.setSolde(new BigDecimal("15000.00")); // Bourse (54k000) - Dépenses (39k000)
      studentWallet.setPlafond(new BigDecimal("54000.00"));
      studentWallet.setDateCreation(LocalDateTime.now());
      studentWallet = walletRepository.save(studentWallet);
      student.setWallet(studentWallet);
      student = userRepository.save(student);

      // Link Student to partner bank (ECOBANK)
      BanqueEtudiant be = new BanqueEtudiant();
      be.setTrackingId(UUID.randomUUID());
      be.setStudent(student);
      be.setBanque(ecobank);
      be.setRIB("TG012-00100-348291001-44");
      be.setVirementEffectue(false);
      be.setMandatSigne(true);
      be.setMandatStatut(MandatStatut.VALIDE);
      banqueEtudiantRepository.save(be);

      // Create annual registration for current year
      InscriptionAnnuelle ia = new InscriptionAnnuelle();
      ia.setTrackingId(UUID.randomUUID());
      ia.setStudent(student);
      ia.setScolariteYear(currentYear);
      ia.setEstInscritDefinitif(true); // First Condition: Inscription définitive validée
      ia.setNiveau(StudentNiveau.L1_ANNEE);
      ia.setCreditsTotalValides(30);
      ia.setEstBoursier(true);
      ia.setTypeBourse(TypeBourse.BOURSE_DBS_54k);
      ia.setStatut(StatutInscription.ACTIVE); // Second Condition: Inscription active / validée
      ia.setSource(SourceVerification.MANUELLE);
      ia.setPlafondAccorde(new BigDecimal("54000.00"));
      inscriptionAnnuelleRepository.save(ia);

      // Upload mandate souche document
      DocumentEtudiant de = new DocumentEtudiant();
      de.setTrackingId(UUID.randomUUID());
      de.setStudent(student);
      de.setType(TypeDocument.SOUCHE_TAMPONNEE);
      de.setUrlFichier("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
      de.setDateDepot(LocalDateTime.now());
      de.setStatut(com.backend.gns.student.domain.enums.StatutDocument.VALIDE);
      documentEtudiantRepository.save(de);

      // Student 2: Pierre L'Étudiant (affiliated with Université de Kara)
      Student studentKara = new Student();
      studentKara.setTrackingId(UUID.randomUUID());
      studentKara.setNom("L'Étudiant");
      studentKara.setPrenom("Pierre");
      studentKara.setEmail("pierre.student@test.com");
      studentKara.setMotDePasse(encodedPassword);
      studentKara.setRole(UserRole.ETUDIANT);
      studentKara.setTelephone("123456789");
      studentKara.setEstActif(true);
      studentKara.setNumEtudiantUniv("789012");
      studentKara.setStatutKYC(KycStatus.VALIDEE);
      studentKara.setUniversite(uk);

      Wallet studentKaraWallet = new Wallet();
      studentKaraWallet.setTrackingId(UUID.randomUUID());
      studentKaraWallet.setTypeWallet(WalletType.STUDENT);
      studentKaraWallet.setStatutWallet(WalletStatus.ACTIF);
      studentKaraWallet.setSolde(new BigDecimal("44000.00")); // Bourse 54k - Scolarité 10k
      studentKaraWallet.setPlafond(new BigDecimal("54000.00"));
      studentKaraWallet.setDateCreation(LocalDateTime.now());
      studentKaraWallet = walletRepository.save(studentKaraWallet);
      studentKara.setWallet(studentKaraWallet);
      studentKara = userRepository.save(studentKara);

      // Link studentKara to ECOBANK
      BanqueEtudiant beKara = new BanqueEtudiant();
      beKara.setTrackingId(UUID.randomUUID());
      beKara.setStudent(studentKara);
      beKara.setBanque(ecobank);
      beKara.setRIB("TG012-00200-581902002-88");
      beKara.setVirementEffectue(false);
      beKara.setMandatSigne(true);
      beKara.setMandatStatut(MandatStatut.VALIDE);
      banqueEtudiantRepository.save(beKara);

      // Create annual registration for Pierre
      InscriptionAnnuelle iaKara = new InscriptionAnnuelle();
      iaKara.setTrackingId(UUID.randomUUID());
      iaKara.setStudent(studentKara);
      iaKara.setScolariteYear(currentYear);
      iaKara.setEstInscritDefinitif(true);
      iaKara.setNiveau(StudentNiveau.L1_ANNEE);
      iaKara.setCreditsTotalValides(30);
      iaKara.setEstBoursier(true);
      iaKara.setTypeBourse(TypeBourse.BOURSE_DBS_54k);
      iaKara.setStatut(StatutInscription.ACTIVE);
      iaKara.setSource(SourceVerification.MANUELLE);
      iaKara.setPlafondAccorde(new BigDecimal("54000.00"));
      inscriptionAnnuelleRepository.save(iaKara);

      // Document for Pierre
      DocumentEtudiant deKara = new DocumentEtudiant();
      deKara.setTrackingId(UUID.randomUUID());
      deKara.setStudent(studentKara);
      deKara.setType(TypeDocument.SOUCHE_TAMPONNEE);
      deKara.setUrlFichier("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
      deKara.setDateDepot(LocalDateTime.now());
      deKara.setStatut(com.backend.gns.student.domain.enums.StatutDocument.VALIDE);
      documentEtudiantRepository.save(deKara);

      // 6. Commerçant (Merchant)
      Merchant merchant = new Merchant();
      merchant.setTrackingId(UUID.randomUUID());
      merchant.setNom("Le Commerçant");
      merchant.setPrenom("Boutique");
      merchant.setEmail("merchant@test.com");
      merchant.setMotDePasse(encodedPassword);
      merchant.setRole(UserRole.COMMERCANT);
      merchant.setTelephone("123456784");
      merchant.setEstActif(true);
      merchant = userRepository.save(merchant);

      Wallet boutiqueWallet = new Wallet();
      boutiqueWallet.setTrackingId(UUID.randomUUID());
      boutiqueWallet.setTypeWallet(WalletType.BOUTIQUE);
      boutiqueWallet.setStatutWallet(WalletStatus.ACTIF);
      boutiqueWallet.setSolde(new BigDecimal("38610.00"));
      boutiqueWallet.setPlafond(new BigDecimal("1000000.00"));
      boutiqueWallet.setDateCreation(LocalDateTime.now());
      boutiqueWallet = walletRepository.save(boutiqueWallet);

      Boutique boutique = new Boutique();
      boutique.setTrackingId(UUID.randomUUID());
      boutique.setNomBoutique("Boutique Universitaire");
      boutique.setCategorieShop("ALIMENTATION");
      boutique.setCheminCarteEDJ("/assets/documents/carte_edj.pdf");
      boutique.setStatutKYC(KycStatus.VALIDEE);
      boutique.setMerchant(merchant);
      boutique.setWallet(boutiqueWallet);
      boutique.setBanquePartenaire(ecobank);
      boutique = boutiqueRepository.save(boutique);

      // 7. Seed payments and their associated orders to satisfy database constraints and have mock transaction history
      // Spent 39k:
      // payment 1 (ACHAT): 20k, commission 200, net boutique 19800
      Commande c1 = new Commande();
      c1.setTrackingId(UUID.randomUUID());
      c1.setReference("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
      c1.setStudent(student);
      c1.setBoutique(boutique);
      c1.setMontantTotal(new BigDecimal("20000.00"));
      c1.setDateCommande(LocalDateTime.now().minusDays(2));
      c1.setStatut(CommandeStatut.VALIDEE);
      c1 = commandeRepository.save(c1);

      Paiement p1 = new Paiement();
      p1.setTrackingId(UUID.randomUUID());
      p1.setCommande(c1);
      p1.setStudent(student);
      p1.setWallet(studentWallet);
      p1.setMontantDebite(new BigDecimal("20000.00"));
      p1.setCommission(new BigDecimal("200.00"));
      p1.setMontantNetBoutique(new BigDecimal("19800.00"));
      p1.setTypePaiement(PaiementType.ACHAT);
      p1.setStatutPaiement(PaiementStatut.VALIDE);
      p1.setDate(LocalDateTime.now().minusDays(2));
      paiementRepository.save(p1);

      // payment 2 (ACHAT): 19k, commission 190, net boutique 18810
      Commande c2 = new Commande();
      c2.setTrackingId(UUID.randomUUID());
      c2.setReference("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
      c2.setStudent(student);
      c2.setBoutique(boutique);
      c2.setMontantTotal(new BigDecimal("19000.00"));
      c2.setDateCommande(LocalDateTime.now().minusDays(1));
      c2.setStatut(CommandeStatut.VALIDEE);
      c2 = commandeRepository.save(c2);

      Paiement p2 = new Paiement();
      p2.setTrackingId(UUID.randomUUID());
      p2.setCommande(c2);
      p2.setStudent(student);
      p2.setWallet(studentWallet);
      p2.setMontantDebite(new BigDecimal("19000.00"));
      p2.setCommission(new BigDecimal("190.00"));
      p2.setMontantNetBoutique(new BigDecimal("18810.00"));
      p2.setTypePaiement(PaiementType.ACHAT);
      p2.setStatutPaiement(PaiementStatut.VALIDE);
      p2.setDate(LocalDateTime.now().minusDays(1));
      paiementRepository.save(p2);

      // payment 3 (SCOLARITE): 10k to Lomé university scolarité reversement calculation
      Commande c3 = new Commande();
      c3.setTrackingId(UUID.randomUUID());
      c3.setReference("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
      c3.setStudent(student);
      c3.setBoutique(boutique);
      c3.setMontantTotal(new BigDecimal("10000.00"));
      c3.setDateCommande(LocalDateTime.now().minusHours(5));
      c3.setStatut(CommandeStatut.VALIDEE);
      c3 = commandeRepository.save(c3);

      Paiement p3 = new Paiement();
      p3.setTrackingId(UUID.randomUUID());
      p3.setCommande(c3);
      p3.setStudent(student);
      p3.setWallet(studentWallet);
      p3.setMontantDebite(new BigDecimal("10000.00"));
      p3.setCommission(BigDecimal.ZERO);
      p3.setMontantNetBoutique(new BigDecimal("10000.00"));
      p3.setTypePaiement(PaiementType.SCOLARITE);
      p3.setStatutPaiement(PaiementStatut.VALIDE);
      p3.setDate(LocalDateTime.now().minusHours(5));
      paiementRepository.save(p3);

      // Seed tuition payment for Pierre L'Étudiant (10,000 FCFA to Université de Kara)
      Commande cKara = new Commande();
      cKara.setTrackingId(UUID.randomUUID());
      cKara.setReference("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
      cKara.setStudent(studentKara);
      cKara.setBoutique(boutique);
      cKara.setMontantTotal(new BigDecimal("10000.00"));
      cKara.setDateCommande(LocalDateTime.now().minusHours(3));
      cKara.setStatut(CommandeStatut.VALIDEE);
      cKara = commandeRepository.save(cKara);

      Paiement pKara = new Paiement();
      pKara.setTrackingId(UUID.randomUUID());
      pKara.setCommande(cKara);
      pKara.setStudent(studentKara);
      pKara.setWallet(studentKaraWallet);
      pKara.setMontantDebite(new BigDecimal("10000.00"));
      pKara.setCommission(BigDecimal.ZERO);
      pKara.setMontantNetBoutique(new BigDecimal("10000.00"));
      pKara.setTypePaiement(PaiementType.SCOLARITE);
      pKara.setStatutPaiement(PaiementStatut.VALIDE);
      pKara.setDate(LocalDateTime.now().minusHours(3));
      paiementRepository.save(pKara);

      log.info("Utilisateurs typés (Student, BankOperator, User) créés avec succès !");
    }
  }
}
