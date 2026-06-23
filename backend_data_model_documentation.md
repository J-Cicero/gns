# Documentation Exacte du Modèle de Données (Backend GNS)

Ce document liste **exactement** les entités présentes dans le code source du backend (dossiers `models`), avec tous leurs attributs et relations exactes définies dans les classes, sans aucun ajout ni omission.

---

## Module User

### 1. `User`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `lastName` (String)
- `firstName` (String)
- `email` (String)
- `password` (String)
- `role` (UserRole)
- `isActive` (boolean, par défaut false)
- `phoneNumber` (String)
- `country` (String)
- `registrationDate` (LocalDateTime)
- `birthDate` (LocalDateTime)
- `birthPlace` (String)
- `kycStatus` (KycStatus, par défaut EN_ATTENTE)

### 2. `AdminBanque` (hérite de User)
Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Banque` (champ `banque`)

---

## Module Student

### 3. `Student` (hérite de User)
Attributs :
- `studenNumber` (String)

Relations :
- `@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)` vers `Wallet` (champ `wallet`) : Chaque étudiant possède exactement un portefeuille géré de manière liée.
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Universite` (champ `universite`) : Un étudiant est rattaché à une université.

### 4. `Universite`
Attributs :
- `id` (Long)
- `code` (String)
- `fullName` (String)
- `city` (String)
- `isActive` (boolean, par défaut true)

### 5. `InscriptionAnnuelle`
Attributs :
- `id` (Long)
- `isFullyEnrolled` (boolean, par défaut false)
- `studyLevel` (StudentNiveau)
- `isEligibleForScholarship` (boolean, par défaut false)
- `scholarshipType` (TypeBourse)
- `apiValidationDate` (LocalDateTime)
- `allocatedBudget` (BigDecimal)

Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Student` (champ `student`)
- `@ManyToOne(fetch = FetchType.LAZY)` vers `ScolariteYear` (champ `scolariteYear`)

### 6. `ScolariteYear`
Attributs :
- `id` (Long)
- `label` (String)
- `startDate` (LocalDate)
- `endDate` (LocalDate)
- `isOpen` (boolean, par défaut true)
- `isClosed` (boolean, par défaut false)

### 7. `Card`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `cardNumber` (String)
- `qrCodeData` (String)
- `status` (CardStatut)
- `emissionDate` (LocalDateTime)
- `expirationDate` (LocalDateTime)

Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Wallet` (champ `wallet`) : Une carte est toujours liée à un portefeuille spécifique.

---

## Module Commerce

### 8. `Merchant` (hérite de User)
Attributs propres : *Aucun (hérite uniquement des attributs de `User`).*

### 9. `Boutique`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `name` (String)
- `description` (String)
- `registrationNumber` (String)
- `kycStatus` (KycStatus)
- `latitude` (Double)
- `longitude` (Double)

Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Merchant` (champ `merchant`) : Plusieurs boutiques peuvent appartenir au même marchand.
- `@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)` vers `Wallet` (champ `wallet`) : Une boutique possède un portefeuille unique.

### 10. `Product`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `name` (String)
- `description` (String)
- `price` (BigDecimal)
- `stock` (int)
- `isAvailable` (Boolean, par défaut true)
- `addedAt` (LocalDateTime)

Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Boutique` (champ `boutique`)

### 11. `Transaction`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `amount` (BigDecimal)
- `status` (TransactionStatut)
- `createdAt` (LocalDateTime)
- `amountDebited` (BigDecimal)
- `amountCredited` (BigDecimal)
- `totalCommission` (BigDecimal)
- `gnsCommission` (BigDecimal)
- `bankCommission` (BigDecimal)
- `isCommissionPaid` (Boolean, par défaut false)
- `isRetry` (Boolean, par défaut false)
- `retrievedByBoutique` (Boolean, par défaut false)
- `deductedFromStudentBourse` (Boolean, par défaut false)

Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Student` (champ `sender`)
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Boutique` (champ `receiver`)
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Liquidation` (champ `liquidation`)
- `@ManyToOne(fetch = FetchType.LAZY)` vers `StudentLiquidation` (champ `studentLiquidation`)

### 12. `Liquidation`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `amountToLiquidate` (BigDecimal)
- `createdAt` (LocalDateTime)
- `validatedAt` (LocalDateTime)
- `status` (LiquidationStatut)
- `transferReference` (String)

Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Boutique` (champ `boutique`)
- `@ManyToOne(fetch = FetchType.LAZY)` vers `ScolariteYear` (champ `scolariteYear`)

### 13. `StudentLiquidation`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `amountDeducted` (BigDecimal)
- `createdAt` (LocalDateTime)
- `validatedAt` (LocalDateTime)
- `status` (LiquidationStatut)
- `transactionReference` (String)

Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Student` (champ `student`)
- `@ManyToOne(fetch = FetchType.LAZY)` vers `ScolariteYear` (champ `scolariteYear`)

---

## Module Wallet

### 14. `Wallet`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `walletType` (WalletType)
- `status` (WalletStatus)
- `fundingLevel` (WalletFundingLevel)
- `balance` (BigDecimal)
- `limitAmount` (BigDecimal)

Relations :
- `@OneToOne(fetch = FetchType.LAZY)` vers `Student` (champ `student`)

### 15. `Versement`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `montantVerse` (BigDecimal)
- `dateVersement` (LocalDateTime)
- `typeVersement` (VersementType)
- `statut` (VersementStatut)

Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Wallet` (champ `wallet`)

---

## Module Core / Paramétrage et Documents

### 16. `Document` (Classe de base pour les documents)
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `documentType` (TypeDocument)
- `fileUrl` (String)
- `providerPublicId` (String)
- `status` (StatutDocument, par défaut EN_ATTENTE)
- `uploadedAt` (LocalDateTime)

### 17. `DocumentEtudiant` (hérite de Document)
Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Student` (champ `student`)
- `@ManyToOne(fetch = FetchType.LAZY)` vers `InscriptionAnnuelle` (champ `inscription`)

### 18. `DocumentMerchant` (hérite de Document)
Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Merchant` (champ `merchant`)

### 19. `DocumentBanque` (hérite de Document)
Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `CompteBancaire` (champ `compteBancaire`)

### 20. `DocumentRequis`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `typeDocument` (TypeDocument)
- `niveauRequis` (StudentNiveau)
- `required` (boolean)
- `description` (String)

### 21. `Banque`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `code` (String)
- `name` (String)

### 22. `CompteBancaire`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `accountNumber` (String)
- `ribUrl` (String)
- `ownerType` (ProprietaireType)
- `isMainScholarshipAccount` (boolean, par défaut false)
- `transferCompleted` (boolean, par défaut false)

Relations :
- `@ManyToOne(fetch = FetchType.LAZY)` vers `Banque` (champ `bank`)
- `@ManyToOne(fetch = FetchType.LAZY)` vers `User` (champ `proprietaire`)

### 23. `ParametreGns`
Attributs :
- `id` (Long)
- `trackingId` (UUID)
- `nomParametre` (TypeParametreGns)
- `valeurParametre` (String)
- `valeurAsBigDecimal` (BigDecimal)
- `valeurAsInteger` (Integer)
- `description` (String)

---
## Résumé des Relations Clés

1. **Relations Parent-Enfant (Héritage)** :
   - `Student`, `Merchant`, `AdminBanque` héritent de la classe de base `User`.
   - `DocumentEtudiant`, `DocumentMerchant`, `DocumentBanque` héritent de la classe de base `Document`.

2. **Relations Bidirectionnelles / Complémentaires** :
   - Un `Student` a exactement un `Wallet` (`@OneToOne`).
   - Un `Student` est rattaché à une `Universite` (`@ManyToOne`).
   - Une `Boutique` a exactement un `Wallet` (`@OneToOne`).
   - Un `Merchant` peut avoir plusieurs `Boutique`s (la boutique a un `@ManyToOne` vers Merchant).

3. **Flux Financier et de Liquidation** :
   - Une `Transaction` implique un expéditeur (`Student`) et un destinataire (`Boutique`), utilisant tous deux un `@ManyToOne`.
   - Les `Transaction`s peuvent être rattachées à une `Liquidation` (pour le marchand) et/ou une `StudentLiquidation` (pour la déduction étudiante) via des annotations `@ManyToOne`.
