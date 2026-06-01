package com.backend.gns.core.config;

import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.user.domain.models.User;
import com.backend.gns.user.infrastructure.repositories.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.models.ParametreGns;
import com.backend.gns.core.parametrage.infrastructure.repositories.ParametreGnsRepository;
import com.backend.gns.student.domain.enums.TypeParametreDbs;
import com.backend.gns.student.domain.models.ParametreDbs;
import com.backend.gns.core.infrastructure.repositories.BanqueRepository;
import com.backend.gns.core.domain.models.Banque;
import com.backend.gns.student.infrastructure.repositories.ParametreDbsRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ParametreGnsRepository parametreGnsRepository;
  private final ParametreDbsRepository parametreDbsRepository;
  private final BanqueRepository banqueRepository;

  @Override
  public void run(String... args) throws Exception {
    seedUsers();
    seedParametresGns();
    seedParametresDbs();
    seedBanques();
  }

  private void seedUsers() {
    if (userRepository.count() == 0) {
      log.info("Table Users vide. Création des utilisateurs de test...");
      
      String encodedPassword = passwordEncoder.encode("password123");

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

      // 4. Bank Operator
      User bankOp = new User();
      bankOp.setTrackingId(UUID.randomUUID());
      bankOp.setNom("Banque");
      bankOp.setPrenom("Operator");
      bankOp.setEmail("operator@bank.com");
      bankOp.setMotDePasse(encodedPassword);
      bankOp.setRole(UserRole.ADMIN_BANQUE);
      bankOp.setTelephone("123456782");
      bankOp.setEstActif(true);
      userRepository.save(bankOp);

      // 5. Étudiant (Student)
      User student = new User();
      student.setTrackingId(UUID.randomUUID());
      student.setNom("L'Étudiant");
      student.setPrenom("Jean");
      student.setEmail("student@test.com");
      student.setMotDePasse(encodedPassword);
      student.setRole(UserRole.ETUDIANT);
      student.setTelephone("123456783");
      student.setEstActif(true);
      userRepository.save(student);

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

      log.info("Utilisateurs de test créés avec succès ! Mot de passe par défaut: password123");
    } else {
      log.info("La table Users contient déjà des données. Seeding ignoré.");
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
}
