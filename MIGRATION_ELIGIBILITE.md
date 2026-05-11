# 📋 Migration Modulaire: Logique d'Éligibilité Implémentée

**Date**: 2026-05-11  
**Module**: Student  
**Composant**: EligibiliteService

---

## 🎯 Objectif

Implémenter la logique complète de vérification d'éligibilité selon le **Cahier des Charges (section 6.6 & 6.1)** avec calcul automatique du plafond d'avance boursière.

---

## ✅ Critères d'Éligibilité Couverts

### 7 Vérifications Mandatoires

| # | Critère | Source | Enum/Type | Action si Échoué |
|---|---------|--------|-----------|------------------|
| 1️⃣ | **Âge** | User.dateNaissance | Calculé | REJETER |
| 2️⃣ | **KYC Validé** | Student.statutKYC | KycStatus.VALIDE | REJETER |
| 3️⃣ | **Mandat Bancaire** | BanqueEtudiant.mandatStatut | MandatStatut.VALIDE | REJETER |
| 4️⃣ | **Inscription Validée** | InscriptionAnnuelle.statut | StatutInscription.VALIDEE | REJETER |
| 5️⃣ | **Statut Boursier** | InscriptionAnnuelle.estBoursier | Boolean | REJETER |
| 6️⃣ | **Niveau & Crédits** | InscriptionAnnuelle.niveau + creditsTotalValides | StudentNiveau | Calcul plafond |
| 7️⃣ | **Mention (L1)** | InscriptionAnnuelle.mentionBac | String | Calcul plafond |

---

## 📊 Seuils d'Éligibilité par Niveau

### LICENCE (L1-L5)

#### L1 - Basé sur Mention BAC
```
PASSABLE        →  36 000 FCFA
ASSEZ_BIEN/BIEN →  54 000 FCFA
Autre           →  NON ÉLIGIBLE
```

#### L2/L3/L4/L5 - Basé sur Crédits ECTS
```
>= 60 crédits    →  54 000 FCFA
>= 30 crédits    →  36 000 FCFA
<  30 crédits    →  NON ÉLIGIBLE
```

### MASTER (M1-M3)

#### M1 - Automatique (si bac valide)
```
→  54 000 FCFA
```

#### M2/M3 - Basé sur Crédits ECTS
```
>= 90 crédits    →  54 000 FCFA
>= 45 crédits    →  36 000 FCFA
<  45 crédits    →  NON ÉLIGIBLE
```

### Limite d'Âge
```
LICENCE (L1-L5) :  ≤ 25 ans (rejet si > 25)
MASTER (M1-M3)  :  ≤ 30 ans (rejet si > 30)
```

---

## 🛠️ Architecture

### Interface
**Fichier**: `/student/domain/services/EligibiliteService.java`

```java
public interface EligibiliteService {
  EligibiliteResult verifierEligibilite(
    Student student,
    InscriptionAnnuelle inscription,
    BanqueEtudiant banque
  );

  // Classe imbriquée pour le résultat
  class EligibiliteResult {
    boolean estEligible;
    BigDecimal plafondAccorde;
    String motifRejet;

    static EligibiliteResult eligible(BigDecimal plafond) { ... }
    static EligibiliteResult nonEligible(String motif) { ... }
  }
}
```

### Implémentation
**Fichier**: `/student/domain/services/impl/EligibiliteServiceImpl.java`

```java
@Service
public class EligibiliteServiceImpl implements EligibiliteService {
  // Constantes
  private static final int AGE_MAX_LICENCE = 25;
  private static final int AGE_MAX_MASTER = 30;
  private static final BigDecimal PLAFOND_LICENCE_BASE = BigDecimal.valueOf(36000);
  private static final BigDecimal PLAFOND_MASTER_BASE = BigDecimal.valueOf(54000);

  // Méthodes privées de vérification
  private String verifierAge(Student student, StudentNiveau niveau)
  private boolean isMandatValide(BanqueEtudiant banque)
  private EligibiliteResult calculerPlafond(InscriptionAnnuelle inscription)
  private EligibiliteResult calculerPlafondL1(String mention)
  private EligibiliteResult calculerPlafondL2L3(int credits)
  private EligibiliteResult calculerPlafondM2M3(int credits)
}
```

---

## 📌 Points d'Intégration

### 1️⃣ Au Moment de la Création de l'Inscription
```java
@Service
public class InscriptionAnnuelleService {
  
  @Autowired
  private EligibiliteService eligibiliteService;

  public InscriptionAnnuelle creerInscription(Student student, InscriptionAnnuelleRequest request) {
    InscriptionAnnuelle inscription = new InscriptionAnnuelle();
    // ... mapper les données
    
    // Récupérer le BanqueEtudiant lié au student
    BanqueEtudiant banque = student.getBanqueEtudiant();
    
    // Vérifier l'éligibilité
    EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);
    
    if (result.estEligible) {
      inscription.setPlafondAccorde(result.plafondAccorde);
      inscription.setStatut(StatutInscription.VALIDEE);
      // Créer/mettre à jour le wallet avec le plafond
      updateWallet(student, result.plafondAccorde);
    } else {
      inscription.setStatut(StatutInscription.REJETEE);
      // Logger le motif: result.motifRejet
      emailNotification.envoyerMotifRejet(student.getEmail(), result.motifRejet);
    }
    
    return inscriptionRepository.save(inscription);
  }
}
```

### 2️⃣ Lors de la Validation KYC par Admin
```java
@RestController
@RequestMapping("/api/admin/kyc")
public class AdminKYCController {
  
  @Autowired
  private EligibiliteService eligibiliteService;
  
  @PostMapping("/{studentId}/validate")
  @PreAuthorize("hasAnyRole('ADMIN_GNS', 'ADMIN_UL')")
  public ResponseEntity<?> validateKYC(@PathVariable UUID studentId) {
    Student student = studentRepository.findByTrackingId(studentId);
    student.setStatutKYC(KycStatus.VALIDE);
    
    // Obtenir l'inscription actuelle
    InscriptionAnnuelle inscription = 
      inscriptionRepository.findByStudentAndAnneeActive(student);
    
    BanqueEtudiant banque = student.getBanqueEtudiant();
    
    // Re-vérifier l'éligibilité (KYC peut débloquer l'accès)
    EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);
    
    if (result.estEligible) {
      inscription.setPlafondAccorde(result.plafondAccorde);
      inscriptionRepository.save(inscription);
    }
    
    return ResponseEntity.ok("KYC validé");
  }
}
```

### 3️⃣ Lors de la Validation du Mandat Bancaire
```java
@RestController
@RequestMapping("/api/admin/banque")
public class AdminBanqueController {
  
  @Autowired
  private EligibiliteService eligibiliteService;
  
  @PostMapping("/{banqueEtudiantId}/valider-mandat")
  @PreAuthorize("hasAnyRole('ADMIN_BANQUE', 'ADMIN_GNS')")
  public ResponseEntity<?> validerMandat(@PathVariable UUID banqueEtudiantId) {
    BanqueEtudiant banque = banqueRepository.findByTrackingId(banqueEtudiantId);
    banque.setMandatStatut(MandatStatut.VALIDE);
    banque.setMandatValideLeDate(LocalDateTime.now());
    
    Student student = banque.getStudent();
    InscriptionAnnuelle inscription = 
      inscriptionRepository.findByStudentAndAnneeActive(student);
    
    // Re-vérifier l'éligibilité (mandat peut débloquer l'accès)
    EligibiliteResult result = eligibiliteService.verifierEligibilite(student, inscription, banque);
    
    if (result.estEligible) {
      inscription.setPlafondAccorde(result.plafondAccorde);
      inscriptionRepository.save(inscription);
      emailNotification.envoyerPlafondAccorde(student.getEmail(), result.plafondAccorde);
    }
    
    return ResponseEntity.ok("Mandat validé");
  }
}
```

---

## 🔄 Flux Complet d'Éligibilité

```
ÉTUDIANT S'INSCRIT
    ↓
1. Créer InscriptionAnnuelle (niveau, crédits, mention BAC, boursier)
    ↓
2. Vérifier Éligibilité
    ├─ Âge ?                              [Automatique sur dateNaissance]
    ├─ KYC Validé ?                       [Dépend de l'admin]
    ├─ Mandat Bancaire Validé ?           [Dépend de la banque]
    ├─ Inscription Validée ?              [Status auto ou admin]
    ├─ Statut Boursier ?                  [Dans l'inscription]
    └─ Niveau + Crédits/Mention ?         [Calculé automatiquement]
    ↓
3. Résultat
    ├─ ✅ ÉLIGIBLE → Calculer Plafond → Créer/Updater Wallet
    └─ ❌ NON ÉLIGIBLE → Notifier étudiant du motif
```

---

## 📝 Exemple d'Utilisation

```java
// Cas 1: L1 avec mention PASSABLE
InscriptionAnnuelle insc = new InscriptionAnnuelle();
insc.setNiveau(StudentNiveau.L1_ANNEE);
insc.setMentionBac("PASSABLE");
insc.setEstBoursier(true);
insc.setStatut(StatutInscription.VALIDEE);

Student student = // ... avec dateNaissance = 2002-01-15 (22 ans) et KYC VALIDE
BanqueEtudiant banque = // ... avec mandatStatut = VALIDE

EligibiliteResult result = eligibiliteService.verifierEligibilite(student, insc, banque);
// result.estEligible = true
// result.plafondAccorde = 36 000 FCFA


// Cas 2: L2 avec 25 crédits (insuffisant)
insc.setNiveau(StudentNiveau.L2_ANNEE);
insc.setCreditsTotalValides(25);

result = eligibiliteService.verifierEligibilite(student, insc, banque);
// result.estEligible = false
// result.motifRejet = "Crédits insuffisants pour Licence. Crédits validés: 25, minimum: 30"


// Cas 3: Master M1
insc.setNiveau(StudentNiveau.M1_ANNEE);

result = eligibiliteService.verifierEligibilite(student, insc, banque);
// result.estEligible = true (si tous les critères min sont OK)
// result.plafondAccorde = 54 000 FCFA
```

---

## 🔐 Sécurité & Validations

- ✅ Tous les null checks sont effectués
- ✅ Dates sont validées (LocalDate.now() pour l'âge)
- ✅ Les enums sont vérifiés strictement
- ✅ Les plafonds sont immutables (BigDecimal)
- ✅ Pas d'effets de bord (la méthode ne modifie rien, elle retourne seulement un résultat)

---

## 📊 Résumé des Constantes

```java
LICENCE
  ├─ L1 : Mention BAC → 36k ou 54k
  ├─ L2-L5 : Crédits → 36k (≥30) ou 54k (≥60)
  └─ Âge max : 25 ans

MASTER
  ├─ M1 : Forfait → 54k
  ├─ M2-M3 : Crédits → 36k (≥45) ou 54k (≥90)
  └─ Âge max : 30 ans
```

---

## 🚀 Prochaines Étapes

1. **Intégrer dans InscriptionAnnuelleService** — appeler lors de la création/validation
2. **Ajouter endpoints admin** — pour valider KYC, mandat, et forcer recalcul
3. **Tests unitaires** — couvrir tous les cas limites (L1 avec mention, M2 avec crédits, âge limite, etc.)
4. **Monitoring** — logger tous les calculs de plafond pour audit
5. **Webhooks bancaires** — déclencher recalcul quand banque valide mandat

---

**Statut**: ✅ IMPLÉMENTÉ & PRÊT À INTÉGRER  
**Tests Recommandés**: Tous les niveaux (L1-L5, M1-M3), tous les cas d'âge, tous les cas de crédits
