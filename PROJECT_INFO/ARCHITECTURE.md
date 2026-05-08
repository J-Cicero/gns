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
│   │   │   ├── models/            (18 entités JPA)
│   │   │   │   ├── User.java (Abstraite)
│   │   │   │   ├── BaseEntity.java (Abstraite)
│   │   │   │   ├── Student.java
│   │   │   │   ├── Merchant.java
│   │   │   │   ├── Admin.java
│   │   │   │   ├── AdminUL.java
│   │   │   │   ├── BankOperator.java
│   │   │   │   ├── Boutique.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Commande.java
│   │   │   │   ├── CommandeLigne.java
│   │   │   │   ├── Wallet.java
│   │   │   │   ├── Paiement.java
│   │   │   │   ├── Versement.java
│   │   │   │   ├── InscriptionAnnuelle.java
│   │   │   │   ├── DocumentEtudiant.java
│   │   │   │   ├── Card.java
│   │   │   │   └── BanqueEtudiant.java
│   │   │   │
│   │   │   ├── services/          (interfaces services)
│   │   │   │
│   │   │   ├── services/impl/     (implémentations)
│   │   │   │
│   │   │   └── enums/             (énumérations)
│   │   │
│   │   ├── infrastructure/        (couche persistence)
│   │   │   └── repositories/      (JPA Repositories)
│   │   │
│   │   ├── application/           (couche présentation)
│   │   │   ├── controllers/       (endpoints REST)
│   │   │   │
│   │   │   └── dtos/              (Data Transfer Objects)
│   │   │
│   │   └── Shared/                (code partagé)
│   │
│   ├── resources/
│   │   └── application.properties
│   │
│   └── test/
│
├── pom.xml
└── PROJECT_INFO/
    └── ARCHITECTURE.md            (ce fichier)
```

---

## MODÈLE DE DONNÉES (ENTITÉS)

Le domaine métier est modélisé par 18 entités JPA.

-   **`User` (Abstraite)**: Superclasse pour tous les types d'utilisateurs, gère l'héritage avec `@DiscriminatorColumn`.
-   **`BaseEntity` (Abstraite)**: Superclasse pour l'audit (`createdBy`, `updatedBy`, `createdAt`, `updatedAt`).
-   **`Student`**: Entité pour les étudiants, hérite de `User`.
-   **`Admin`**: Entité pour les administrateurs, hérite de `User`.
-   **`Merchant`**: Entité pour les commerçants, hérite de `User`.
-   **`AdminUL`**: Entité pour les administrateurs de l'UL, hérite de `User`.
-   **`BankOperator`**: Entité pour les opérateurs bancaires, hérite de `User`.
-   **`Boutique`**: Entité pour les boutiques des commerçants.
-   **`Product`**: Entité pour les produits vendus dans les boutiques.
-   **`Commande`**: Entité pour les commandes passées par les étudiants.
-   **`CommandeLigne`**: Ligne d'une commande, lie un produit et une quantité.
-   **`Wallet`**: Entité pour les portefeuilles électroniques.
-   **`Paiement`**: Entité pour les paiements d'une commande.
-   **`Versement`**: Entité pour les versements sur les portefeuilles.
-   **`InscriptionAnnuelle`**: Entité pour l'inscription annuelle d'un étudiant.
-   **`DocumentEtudiant`**: Entité pour les documents soumis par les étudiants.
-   **`Card`**: Entité pour la carte d'étudiant.
-   **`BanqueEtudiant`**: Entité pour les informations bancaires d'un étudiant.

---

## 🔗 RELATIONS ENTRE ENTITÉS (Mis à jour)

### User & ses sous-types
-   **`User`** est une superclasse abstraite.
-   **`Student`**, **`Admin`**, **`Merchant`**, **`AdminUL`**, **`BankOperator`** héritent de `User`.

### Student
-   `1:1` avec **`Wallet`** (`mappedBy` dans `Student`)
-   `1:1` avec **`Card`** (`mappedBy` dans `Student`)
-   `1:1` avec **`BanqueEtudiant`** (`mappedBy` dans `Student`)
-   `1:N` vers **`Commande`** (Un `Student` peut avoir plusieurs `Commande`s)
-   `1:N` vers **`Paiement`** (Un `Student` peut faire plusieurs `Paiement`s)
-   `1:N` vers **`InscriptionAnnuelle`** (Un `Student` peut avoir plusieurs `InscriptionAnnuelle`s)
-   `1:N` vers **`DocumentEtudiant`** (Un `Student` peut avoir plusieurs `DocumentEtudiant`s)

### Merchant
-   `1:N` vers **`Boutique`** (Un `Merchant` peut avoir plusieurs `Boutique`s)

### Boutique
-   `N:1` avec **`Merchant`**
-   `1:1` avec **`Wallet`**
-   `1:N` vers **`Product`** (Une `Boutique` peut avoir plusieurs `Product`s)
-   `1:N` vers **`Commande`** (Une `Boutique` peut être dans plusieurs `Commande`s)

### Commande
-   `N:1` avec **`Student`**
-   `N:1` avec **`Boutique`**
-   `1:N` vers **`CommandeLigne`**
-   `1:N` vers **`Paiement`**

### CommandeLigne
-   `N:1` avec **`Commande`**
-   `N:1` avec **`Product`**

### Paiement
-   `N:1` avec **`Commande`**
-   `N:1` avec **`Student`**
-   `N:1` avec **`Wallet`**

### Versement
-   `N:1` avec **`Wallet`**
-   `N:1` avec **`Admin`** (`initiePar`)

### InscriptionAnnuelle
-   `N:1` avec **`Student`**
-   `1:N` vers **`DocumentEtudiant`**

### DocumentEtudiant
-   `N:1` avec **`Student`**
-   `N:1` avec **`InscriptionAnnuelle`** (nullable)
-   `N:1` avec **`BanqueEtudiant`** (nullable)

---

## ⚙️ ENUMS UTILISÉS (Mis à jour)

### Wallets
-   `WalletType`: RELAIS, HORIZON, BOURSE_DBS_36k
-   `WalletStatus`: ACTIF, DESACTIF, SUSPENDU

### Commandes
-   `CommandeStatut`: EN_COURS, FINALISEE, ANNULEE

### Paiements
-   `PaiementStatut`: EN_ATTENTE, EFFECTUE, ECHOUE, ANNULE, REMBOURSE
-   `PaiementType`: PAIEMENT_CLIENT, VERSEMENT_MERCHANT, BOURSE_DBS_36k

### Versements
-   `VersementStatut`: EN_ATTENTE, EFFECTUE, ECHOUE, ANNULE
-   `VersementType`: BOURSE_DBS_36k, MERCHANT

### Vérification
-   `KycStatus`: EN_COURS, VALIDEE, REJETEE
-   `StatutDocument`: EN_ATTENTE, ACCEPTE, REJETE

### Étudiants
-   `StudentNiveau`: L1, L2, L3, M1, M2
-   `TypeBourse`: BOURSE_COMPLETE, BOURSE_PARTIELLE, SANS_BOURSE
-   `TypeDocument`: RELEVE_BAC, CARTE_ETUDIANT, SOUCHE_TAMPONNEE, RELEVE_NOTES
-   `Banque`: ECOBANK, ORABANK, UBA, NSIA
-   `MandatStatut`: EN_ATTENTE_DEPOT, DEPOSE, VALIDE, REJETE

### Cartes
-   `CardStatus`: ACTIVE, PERDUE, REMPLACEE

---

**Généré**: 2026-05-08
**Version**: 1.1
