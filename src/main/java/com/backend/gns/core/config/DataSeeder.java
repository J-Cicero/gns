package com.backend.gns.core.config;

import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.user.domain.models.User;
import com.backend.gns.user.infrastructure.repositories.UserRepository;
import com.backend.gns.core.domain.models.Banque;
import com.backend.gns.core.infrastructure.repositories.BanqueRepository;
import com.backend.gns.admin.domain.models.BankOperator;
import com.backend.gns.admin.infrastructure.repositories.BankOperatorRepository;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.models.Universite;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import com.backend.gns.student.infrastructure.repositories.BanqueEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletFundingLevel;
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

  @PersistenceContext
  private final EntityManager entityManager;

  @Override
  public void run(String... args) throws Exception {
    seedAll();
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
      banqueRepository.save(new Banque(null, UUID.randomUUID(), "POSTE", "La Poste"));
      banqueRepository.save(new Banque(null, UUID.randomUUID(), "ECOBANK", "Ecobank"));
      banqueRepository.save(new Banque(null, UUID.randomUUID(), "ORABANK", "Orabank"));
      banqueRepository.save(new Banque(null, UUID.randomUUID(), "IBBANK", "IB Bank"));
      banqueRepository.save(new Banque(null, UUID.randomUUID(), "CORISBANK", "Coris Bank"));
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
    // If operator@bank.com is not of type BankOperator in DB, reset users to trigger clean seeding.
    boolean needsClean = false;
    var uOpt = userRepository.findByEmail("operator@bank.com");
    if (uOpt.isPresent() && !"BANK_OPERATOR".equals(uOpt.get().getClass().getSimpleName()) && !(uOpt.get() instanceof BankOperator)) {
      needsClean = true;
    }
    
    if (needsClean) {
      log.info("Détection d'utilisateurs de test non typés (polymorphisme). Nettoyage de la base de données...");
      entityManager.createNativeQuery("DELETE FROM paiement").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM versement").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM card").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM commande_ligne").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM commande").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM pret_scolarite").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM banque_etudiant").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM inscription_annuelle").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM document_etudiant").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM boutique").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM product").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
      entityManager.createNativeQuery("DELETE FROM wallet").executeUpdate();
    }
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

      // Seed ScolariteYear
      ScolariteYear currentYear = scolariteYearRepository.findByEstOuverteTrue().orElse(null);
      if (currentYear == null) {
        currentYear = new ScolariteYear();
        currentYear.setTrackingId(UUID.randomUUID());
        currentYear.setAnnee("2025-2026");
        currentYear.setEstOuverte(true);
        currentYear = scolariteYearRepository.save(currentYear);
      }

      // Seed Banque partenaires references
      Banque ecobank = banqueRepository.findByCode("ECOBANK").orElseThrow();

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
      de.setNomFichier("souche_mandat.pdf");
      de.setUrlFichier("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf");
      de.setMandatSigne(true);
      de.setDateUpload(LocalDateTime.now());
      documentEtudiantRepository.save(de);

      // 6. Commerçant (Merchant)
      User merchant = new User();
      merchant.setTrackingId(UUID.randomUUID());
      merchant.setNom("Le Commerçant");
      merchant.setPrenom("Boutique");
      merchant.setEmail("merchant@test.com");
      merchant.setMotDePasse(encodedPassword);
      merchant.setRole(UserRole.COMMERCANT);
      merchant.setTelephone("123456784");
      merchant.setEstActif(true);
      userRepository.save(merchant);

      // 7. Seed payments to have mock transaction history
      // Spent 39k:
      // payment 1 (ACHAT): 20k, commission 200, net boutique 19800
      Paiement p1 = new Paiement();
      p1.setTrackingId(UUID.randomUUID());
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
      Paiement p2 = new Paiement();
      p2.setTrackingId(UUID.randomUUID());
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
      Paiement p3 = new Paiement();
      p3.setTrackingId(UUID.randomUUID());
      p3.setStudent(student);
      p3.setWallet(studentWallet);
      p3.setMontantDebite(new BigDecimal("10000.00"));
      p3.setCommission(BigDecimal.ZERO);
      p3.setMontantNetBoutique(new BigDecimal("10000.00"));
      p3.setTypePaiement(PaiementType.SCOLARITE);
      p3.setStatutPaiement(PaiementStatut.VALIDE);
      p3.setDate(LocalDateTime.now().minusHours(5));
      paiementRepository.save(p3);

      log.info("Utilisateurs typés (Student, BankOperator, User) créés avec succès !");
    }
  }
}
