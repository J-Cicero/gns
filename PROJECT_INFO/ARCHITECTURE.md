# 🏗️ ARCHITECTURE DÉTAILLÉE

## Structure du Projet

```
/gns (racine)
├── .git/                          (historique)
├── .mvn/                          (wrapper Maven)
├── src/
│   ├── main/java/com/backend/gns/
│   │   ├── GnsApplication.java    (point d'entrée Spring Boot)
│   │   │
│   │   ├── domain/                (couche métier)
│   │   │   ├── models/            (12 entités JPA)
│   │   │   │   ├── Student.java
│   │   │   │   ├── Merchant.java
│   │   │   │   ├── Admin.java
│   │   │   │   ├── Boutique.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Commande.java
│   │   │   │   ├── CommandeLigne.java
│   │   │   │   ├── Wallet.java
│   │   │   │   ├── Paiement.java
│   │   │   │   ├── Versement.java
│   │   │   │   ├── InscriptionAnnuelle.java
│   │   │   │   └── DocumentEtudiant.java
│   │   │   │
│   │   │   ├── services/          (interfaces services)
│   │   │   │   ├── StudentService
│   │   │   │   ├── MerchantService
│   │   │   │   ├── WalletService
│   │   │   │   ├── CommandeService
│   │   │   │   ├── PaiementService
│   │   │   │   └── autres...
│   │   │   │
│   │   │   ├── services/impl/     (implémentations)
│   │   │   │   ├── StudentServiceImpl.java
│   │   │   │   ├── MerchantServiceImpl.java
│   │   │   │   └── autres...
│   │   │   │
│   │   │   └── enums/             (énumérations)
│   │   │       ├── WalletType.java
│   │   │       ├── WalletStatus.java
│   │   │       ├── CommandeStatut.java
│   │   │       ├── KycStatus.java
│   │   │       └── 8+ autres enums
│   │   │
│   │   ├── infrastructure/        (couche persistence)
│   │   │   └── repositories/      (JPA Repositories)
│   │   │       ├── StudentRepository extends JpaRepository
│   │   │       ├── MerchantRepository
│   │   │       ├── WalletRepository
│   │   │       ├── CommandeRepository
│   │   │       ├── PaiementRepository
│   │   │       └── 8+ autres repositories
│   │   │
│   │   ├── application/           (couche présentation)
│   │   │   ├── controllers/       (endpoints REST)
│   │   │   │   ├── StudentController
│   │   │   │   ├── MerchantController
│   │   │   │   ├── UserController (login/register)
│   │   │   │   ├── CommandeController
│   │   │   │   ├── WalletController
│   │   │   │   └── autres...
│   │   │   │
│   │   │   └── dtos/              (Data Transfer Objects)
│   │   │       ├── requests/
│   │   │       │   ├── LoginRequest.java
│   │   │       │   ├── UserRequest.java
│   │   │       │   └── autres DTOs
│   │   │       └── responses/
│   │   │           └── réponses formatées
│   │   │
│   │   └── Shared/                (code partagé)
│   │       ├── ai/
│   │       │   └── GeminiExtractionService.java
│   │       ├── storage/
│   │       │   └── CloudinaryStorageService.java
│   │       ├── security/
│   │       │   ├── adapters/UserPrincipal.java
│   │       │   ├── config/SecurityConfig.java
│   │       │   ├── jwt/JwtService.java
│   │       │   └── exceptions/
│   │       ├── user/
│   │       │   └── UserManagementService
│   │       └── config/
│   │           ├── CloudinaryConfig.java
│   │           ├── JpaAuditingConfig.java
│   │           └── OpenApiConfig.java (Swagger)
│   │
│   ├── resources/
│   │   └── application.properties  (DB, server config)
│   │
│   └── test/
│       ├── java/
│       │   └── com/backend/gns/
│       │       ├── GnsApplicationTests.java
│       │       └── JpaRelationshipsIntegrationTest.java
│       └── resources/
│           └── application-test.properties
│
├── pom.xml                        (dépendances Maven)
└── PROJECT_INFO/                  (cette documentation)
    ├── INDEX_COMPLET.md           (ce fichier - vue d'ensemble)
    ├── BRANCHES_GIT.md            (stratégie branches)
    └── ARCHITECTURE.md            (ce fichier)
```

---

## 🔗 RELATIONS ENTRE ENTITÉS

```
┌─────────────┐
│   Student   │
└──────┬──────┘
       │ 1:1
       ├─────→ Wallet (multiple)
       ├─────→ Commande
       ├─────→ InscriptionAnnuelle
       └─────→ DocumentEtudiant- **Divergence vs Jude**: 17 commits (Jude) vs 6 commits (security)


┌─────────────┐
│  Merchant   │
└──────┬──────┘
       │ 1:1
       └─────→ Boutique (multiple)
               │ 1:1
               └─────→ Product (multiple)

┌─────────────┐
│   Commande  │
└──────┬──────┘
       │ 1:N
       ├─────→ CommandeLigne- **Divergence vs Jude**: 17 commits (Jude) vs 6 commits (security)

       └─────→ Paiement

┌─────────────┐
│   Wallet    │
└──────┬──────┘
       │ 1:N
       ├─────→ Versement
       └─────→ Paiement
```

---

## 🔐 FLUX DE SÉCURITÉ

```
User Request
    ↓
HTTP Header (Bearer Token)
    ↓- **Divergence vs Jude**: 17 commits (Jude) vs 6 commits (security)

JwtAuthorizationToken Filter
    ↓
JwtService.validateToken()
    ↓
UserPrincipal created
    ↓
SecurityContext populated
    ↓
Controller @PreAuthorize checks- **Divergence vs Jude**: 17 commits (Jude) vs 6 commits (security)

    ↓
Business Logic executes
    ↓
Response returned
```

---

## 📦 DÉPENDANCES PRINCIPALES

### Core Spring
- spring-boot-starter-webmvc
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation

### Database
- postgresql (driver)
- spring-data-jpa (ORM)

### API Documentation
- springdoc-openapi-starter-webmvc-ui (Swagger UI)

### External Services
- google-cloud-storage
- cloudinary-core

### Testing
- spring-boot-starter-test
- junit-jupiter

---

## 🎯 POINTS D'ENTRÉE (Controllers)- **Divergence vs Jude**: 17 commits (Jude) vs 6 commits (security)


| Controller | Endpoints | Rôle |- **Divergence vs Jude**: 17 commits (Jude) vs 6 commits (security)

|-----------|-----------|------|
| UserController | /api/auth/** | Login/Register |
| StudentController | /api/students/** | Gestion étudiants |
| MerchantController | /api/merchants/** | Gestion commerçants |
| BoutiqueController | /api/boutiques/** | Gestion boutiques |
| ProductController | /api/products/** | Gestion produits |
| CommandeController | /api/commandes/** | Gestion commandes |
| WalletController | /api/wallets/** | Gestion portefeuilles |
| PaiementController | /api/paiements/** | Gestion paiements |
| AdminController | /api/admin/** | Fonctions admin |

---

## 🧬 EXEMPLE: FLUX COMMANDE COMPLÈTE
- **Divergence vs Jude**: 17 commits (Jude) vs 6 commits (security)

```
1. Student fait une commande
   POST /api/commandes
   → CommandeController.createCommande()

2. Controller valide DTO
   → Commande entity créée

3. Service applique logique métier
   → CommandeServiceImpl.createCommande()
   → Vérifier stock via ProductRepository
   → Créer lignes (CommandeLigne)
   → Mettre à jour Wallet si nécessaire

4. Repository persiste en DB
   → CommandeRepository.save()
   → CommandeLigneRepository.saveAll()

5. Service déclenche Paiement
   → PaiementService.initiatePaiement()
   → Débite Wallet via WalletService.debit()

6. Réponse retournée
   → HTTP 201 Created
   → JSON avec Commande + détails
```

---

## ⚙️ ENUMS UTILISÉS

### Wallets
- `WalletType`: RELAIS, HORIZON
- `WalletStatus`: ACTIF, DESACTIF, SUSPENDU

### Commandes
- `CommandeStatut`: ENCOURS, VALIDEE, LIVREE, ANNULEE

### Paiements
- `PaiementStatut`: ENCOURS, COMPLETE, ECHUE, REMBOURSEE
- `PaiementType`: PAIEMENT_CLIENT, VERSEMENT_MERCHANT

### Vérification
- `KycStatus`: ENCOURS, VALIDEE, REJETEE
- `StatutDocument`: EN_ATTENTE, ACCEPTE, REJETE- **Divergence vs Jude**: 17 commits (Jude) vs 6 commits (security)


### Étudiants
- `StudentNiveau`: L1, L2, L3, M1, M2
- `TypeBourse`: BOURSE_COMPLETE, BOURSE_PARTIELLE, SANS_BOURSE

---

## 🔄 CYCLE DE VIE COMMANDE

```
NOUVEAU
  ↓
ENCOURS (awaiting payment)
  ↓
VALIDEE (paid)
  ↓
LIVREE (completed)
  
Alternative:
  ↓
ANNULEE (if cancelled)
```

---

**Généré**: 2026-05-06  
**Version**: 1.0
