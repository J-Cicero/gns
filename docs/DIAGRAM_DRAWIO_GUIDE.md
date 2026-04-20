# 📊 Guide Complet pour Créer le Diagramme de Classes DrawIO

Ce document contient toutes les informations nécessaires pour créer votre diagramme de classes dans DrawIO. Il inclut toutes les entités, leurs attributs, les énumérations et les relations.

---

## 🎯 Instructions pour DrawIO

### Comment créer le diagramme :
1. **Ouvrir DrawIO** → Nouveau diagramme → UML Diagram
2. **Pour chaque classe** : Ajouter une classe (UML Class box)
3. **Attributs** : Ajouter avec leur type (String, int, BigDecimal, LocalDateTime, Boolean, Enum)
4. **Énumérations** : Ajouter comme classe avec `<<enumeration>>`
5. **Relations** : Connecter les classes avec les bons types de flèches
6. **Héritage** : Utiliser les flèches triangulaires pour `extends`
7. **Associations** : Utiliser les flèches appropriées (ManyToOne, OneToOne, etc.)

---

## 📋 LISTE COMPLÈTE DES CLASSES ET ATTRIBUTS

### ✅ IMPORTANT : Attributs PROPRES à chaque classe
- **EXCLU** : Les IDs auto-générés (`Long id`)
- **EXCLU** : Les champs de tracking (`UUID trackingId`)
- **EXCLU** : Les champs d'audit (`createdBy`, `updatedBy`, `createdAt`, `updatedAt`)
- **INCLUS** : Tous les attributs métier spécifiques à la classe
- **INCLUS** : Les relations (OneToOne, ManyToOne, etc.)

---

## 1️⃣ **CLASSE : User** (Classe mère - @Entity SINGLE_TABLE)

**Type de classe :** Classe abstraite avec héritage simple table
**Discriminator :** `user_type` (STRING)

### Attributs :
```
- email : String (length=100, unique)
- password : String (length=100)
- nomComplet : String (length=100)
- statut : String (length=20)
- adresse : String (nullable)
- telephone : String (nullable)
- photoUrl : String (nullable)
- nomUtilisateur : String (nullable)
```

---

## 2️⃣ **CLASSE : Admin** (extends User)

**Type de classe :** Classe concrète
**Discriminator Value :** `ADMIN`
**Annotation :** `@DiscriminatorValue("ADMIN")`

### Attributs spécifiques à Admin :
```
- numeroCompte : String (length=20, nullable)
```

**Relations :** Aucune

---

## 3️⃣ **CLASSE : Merchant** (extends User)

**Type de classe :** Classe concrète
**Discriminator Value :** `MERCHANT`
**Annotation :** `@DiscriminatorValue("MERCHANT")`

### Attributs spécifiques à Merchant :
```
(Aucun attribut spécifique - hérite uniquement de User)
```

**Relations :**
- OneToMany → `Commande` (une boutique peut avoir plusieurs commandes)
- OneToMany → `Boutique` (un commerçant peut avoir plusieurs boutiques)

---

## 4️⃣ **CLASSE : Student** (extends User)

**Type de classe :** Classe concrète
**Discriminator Value :** `ETUDIANT`
**Annotation :** `@DiscriminatorValue("ETUDIANT")`

### Attributs spécifiques à Student :
```
- numEtudiantUL : String (length=50, unique, not null)
- banque : Banque (enum, length=20, not null)
- RIB : String (length=100, unique, not null)
- mandatSigne : boolean (default=false)
- mandatTimestamp : LocalDateTime (nullable)
- mandatIpAddress : String (length=45, nullable)
- statutKYC : KycStatus (enum, length=20, not null)
```

**Relations :**
- **OneToOne** → `Wallet` (chaque étudiant a UN wallet)
- **OneToMany** → `Commande` (un étudiant peut faire plusieurs commandes)
- **OneToMany** → `DocumentEtudiant` (un étudiant peut avoir plusieurs documents)
- **OneToMany** → `InscriptionAnnuelle` (un étudiant peut avoir plusieurs inscriptions)

---

## 5️⃣ **CLASSE : Boutique** (BaseEntity)

**Type de classe :** Entité métier
**Table :** `BOUTIQUE`

### Attributs :
```
- nomBoutique : String (length=100, not null)
- categorieShop : String (length=100, not null)
- cheminCarteEDJ : String (length=255, nullable)
- statutKYC : KycStatus (enum, length=20, not null)
- latitude : Double (nullable)
- longitude : Double (nullable)
```

**Relations :**
- **ManyToOne** → `Merchant` (plusieurs boutiques → un commerçant) [Clé étrangère: merchant_id]
- **OneToOne** → `Wallet` (une boutique a UN wallet) [Clé étrangère: wallet_id]
- **OneToMany** → `Product` (une boutique a plusieurs produits)

---

## 6️⃣ **CLASSE : Product** (BaseEntity)

**Type de classe :** Entité métier
**Table :** `product`

### Attributs :
```
- nom : String (length=100, not null)
- description : String (TEXT, nullable)
- prix : BigDecimal (not null)
- stock : int (not null)
- estDisponible : Boolean (default=true)
- dateAjout : LocalDateTime (format: yyyy-MM-dd'T'HH:mm:ss)
```

**Relations :**
- **ManyToOne** → `Boutique` (plusieurs produits → une boutique) [Clé étrangère: boutique_id]
- **OneToMany** → `CommandeLigne` (un produit peut être dans plusieurs lignes de commande)

---

## 7️⃣ **CLASSE : Commande** (BaseEntity)

**Type de classe :** Entité métier
**Table :** `COMMANDE`

### Attributs :
```
- reference : String (length=36, unique, not null)
- montantTotal : BigDecimal (not null)
- dateCommande : LocalDateTime (nullable, format: yyyy-MM-dd'T'HH:mm:ss)
- statut : CommandeStatut (enum, length=20, nullable)
```

**Relations :**
- **ManyToOne** → `Student` (plusieurs commandes → un étudiant) [Clé étrangère: student_id]
- **ManyToOne** → `Merchant` (plusieurs commandes → un commerçant) [Clé étrangère: merchant_id]
- **OneToMany** → `CommandeLigne` (une commande a plusieurs lignes)
- **OneToMany** → `Paiement` (une commande peut avoir plusieurs paiements)

---

## 8️⃣ **CLASSE : CommandeLigne** (BaseEntity)

**Type de classe :** Entité métier (classe de jointure)
**Table :** `COMMANDE_LIGNE`

### Attributs :
```
- quantite : int (not null)
- prixUnitaire : BigDecimal (not null)
```

**Méthode calculée :**
```
+ getSousTotal() : BigDecimal = prixUnitaire × quantite
```

**Relations :**
- **ManyToOne** → `Commande` (plusieurs lignes → une commande) [Clé étrangère: commande_id, FetchType.EAGER]
- **ManyToOne** → `Product` (plusieurs lignes → un produit) [Clé étrangère: product_id, FetchType.EAGER]

---

## 9️⃣ **CLASSE : Paiement** (BaseEntity)

**Type de classe :** Entité métier
**Table :** `PAIEMENT`

### Attributs :
```
- montantProduit : BigDecimal (not null)
- commission : BigDecimal (not null)
- montantDebite : BigDecimal (not null)
- date : LocalDateTime (not null, format: yyyy-MM-dd'T'HH:mm:ss)
- typePaiement : PaiementType (enum, length=20, nullable)
- statutPaiement : PaiementStatut (enum, length=20, nullable)
```

**Relations :**
- **ManyToOne** → `Commande` (plusieurs paiements → une commande) [Clé étrangère: commande_id]
- **ManyToOne** → `Wallet` (plusieurs paiements débités → un wallet) [Clé étrangère: wallet_id]

---

## 🔟 **CLASSE : Wallet** (BaseEntity)

**Type de classe :** Entité métier
**Table :** `wallet`

### Attributs :
```
- typeWallet : WalletType (enum, length=20, not null)
- statutWallet : WalletStatus (enum, length=20, not null)
- solde : BigDecimal (not null)
- plafond : BigDecimal (not null)
- estVerrouille : Boolean (default=false)
- dateCreation : LocalDateTime (nullable, format: yyyy-MM-dd'T'HH:mm:ss)
```

**Relations :**
- **OneToOne inverse** ← `Student` (chaque étudiant a un wallet)
- **OneToOne inverse** ← `Boutique` (chaque boutique a un wallet)
- **OneToMany inverse** ← `Paiement` (un wallet reçoit plusieurs paiements)
- **OneToMany inverse** ← `Versement` (un wallet reçoit plusieurs versements)

---

## 1️⃣1️⃣ **CLASSE : Versement** (BaseEntity)

**Type de classe :** Entité métier
**Table :** `versement`

### Attributs :
```
- montantVerse : BigDecimal (not null)
- typeVersement : VersementType (enum, length=30, nullable)
- dateVersement : LocalDateTime (nullable, format: yyyy-MM-dd'T'HH:mm:ss)
- statut : VersementStatut (enum, length=20, nullable)
```

**Relations :**
- **ManyToOne** → `Wallet` (plusieurs versements → un wallet) [Clé étrangère: wallet_id]

---

## 1️⃣2️⃣ **CLASSE : DocumentEtudiant** (BaseEntity)

**Type de classe :** Entité métier
**Table :** `DOCUMENT_ETUDIANT`

### Attributs :
```
- type : TypeDocument (enum, length=30, not null)
- cheminFichier : String (length=255, not null)
- statut : StatutDocument (enum, length=20, not null)
- commentaireRejet : String (length=255, nullable)
- dateDepot : LocalDateTime (not null)
- dateValidation : LocalDateTime (nullable)
```

**Relations :**
- **ManyToOne** → `Student` (plusieurs documents → un étudiant) [Clé étrangère: student_id, FetchType.LAZY]
- **ManyToOne** → `InscriptionAnnuelle` (plusieurs documents → une inscription) [Clé étrangère: inscription_id, FetchType.LAZY]

---

## 1️⃣3️⃣ **CLASSE : InscriptionAnnuelle** (BaseEntity)

**Type de classe :** Entité métier
**Table :** `INSCRIPTION_ANNUELLE`

### Attributs :
```
- anneeAcademique : String (length=20, not null)
- niveau : NiveauEtude (enum, length=10, not null)
- creditsTotalValides : int (not null)
- estBoursier : boolean (default=false)
- typeBourse : TypeBourse (enum, length=20, nullable)
- fraisScolaritePayes : boolean (default=false)
- statut : StatutInscription (enum, length=30, not null)
- source : SourceVerification (enum, length=20, nullable)
- dateActivation : LocalDateTime (nullable)
```

**Relations :**
- **ManyToOne** → `Student` (plusieurs inscriptions → un étudiant) [Clé étrangère: student_id, FetchType.LAZY]
- **OneToMany** ← `DocumentEtudiant` (une inscription peut avoir plusieurs documents)

---

---

# 📌 ÉNUMÉRATIONS (ENUMS)

Les énumérations doivent être représentées comme des classes avec le stéréotype `<<enumeration>>`

## **Enum : Banque**
```
Valeurs : POSTE, ECOBANK, ORABANK, IBBANK, CORISBANK
```

## **Enum : CommandeStatut**
```
Valeurs : EN_COURS, FINALISEE, ANNULEE
```

## **Enum : KycStatus**
```
Valeurs : EN_ATTENTE, VALIDE, REJETE
```

## **Enum : NiveauEtude**
```
Valeurs : L1, L2, L3
```

## **Enum : PaiementStatut**
```
Valeurs : EN_COURS, VALIDE, ECHOUE
```

## **Enum : PaiementType**
```
Valeurs : ACHAT, SCOLARITE
```

## **Enum : SourceVerification**
```
Valeurs : MANUELLE, API_UL
```

## **Enum : StatutDocument**
```
Valeurs : EN_ATTENTE, VALIDE, REJETE, REMPLACE
```

## **Enum : StatutInscription**
```
Valeurs : EN_ATTENTE, VERIFIE_UL, DOCUMENTS_EN_COURS, ACTIVE, EXPIREE
```

## **Enum : TypeBourse**
```
Valeurs : TRANCHE_36000, TRANCHE_54000
```

## **Enum : TypeDocument**
```
Valeurs : FICHE_UE, RELEVE_BAC, RELEVE_ANNEE_PRECEDENTE
```

## **Enum : VersementStatut**
```
Valeurs : EN_ATTENTE, EFFECTUE, ANNULE
```

## **Enum : VersementType**
```
Valeurs : BOURSE_DBS_36k, MERCHANT
```

## **Enum : WalletStatus**
```
Valeurs : VIDE, ACTIF, BLOQUE, CONTENANTE
```

## **Enum : WalletType**
```
Valeurs : BOURSE_DBS_36k, BOURSE_DBS_54k, BOUTIQUE, UL
```

---

---

# 🔗 RELATIONS COMPLÈTES (Pour DrawIO)

### **Héritages (extends)** - Utiliser des flèches triangulaires

```
User (classe mère)
  ↑
  ├─ Admin
  ├─ Merchant
  └─ Student
```

### **Relations OneToOne** - Flèche simple avec "1 --- 1"

```
Student ----------- 1 --- 1 ----------- Wallet
                    (foreign key: wallet_id)

Boutique ---------- 1 --- 1 ----------- Wallet
                    (foreign key: wallet_id)
```

### **Relations OneToMany** - Flèche simple avec "1 --- *"

```
Boutique --------- 1 --- * --------- Product

Commande --------- 1 --- * --------- CommandeLigne

Commande --------- 1 --- * --------- Paiement

Product --------- 1 --- * --------- CommandeLigne

Student --------- 1 --- * --------- Commande

Student --------- 1 --- * --------- DocumentEtudiant

Student --------- 1 --- * --------- InscriptionAnnuelle

InscriptionAnnuelle -- 1 --- * -- DocumentEtudiant

Wallet --------- 1 --- * --------- Paiement

Wallet --------- 1 --- * --------- Versement

Merchant --------- 1 --- * --------- Boutique

Merchant --------- 1 --- * --------- Commande
```

### **Relations ManyToOne** - (Inverse des OneToMany)

```
Product -------- * --- 1 -------- Boutique
                        (foreign key: boutique_id)

CommandeLigne -- * --- 1 -------- Commande
                        (foreign key: commande_id, EAGER)

CommandeLigne -- * --- 1 -------- Product
                        (foreign key: product_id, EAGER)

Commande -------- * --- 1 -------- Student
                        (foreign key: student_id)

Commande -------- * --- 1 -------- Merchant
                        (foreign key: merchant_id)

Paiement -------- * --- 1 -------- Commande
                        (foreign key: commande_id)

Paiement -------- * --- 1 -------- Wallet
                        (foreign key: wallet_id)

Versement ------- * --- 1 -------- Wallet
                        (foreign key: wallet_id)

DocumentEtudiant * --- 1 -------- Student
                        (foreign key: student_id, LAZY)

DocumentEtudiant * --- 1 -- InscriptionAnnuelle
                        (foreign key: inscription_id, LAZY)

InscriptionAnnuelle * --- 1 ------ Student
                        (foreign key: student_id, LAZY)

Boutique -------- * --- 1 -------- Merchant
                        (foreign key: merchant_id)
```

---

---

# 📐 RECOMMANDATIONS POUR LE DIAGRAMME

## **Organisation visuelle suggérée** :

```
                    ┌─────────────────┐
                    │      USER       │ (Classe mère)
                    └────────┬────────┘
                             │
                  ┌──────────┼──────────┐
                  │          │          │
            ┌─────▼───┐ ┌────▼────┐ ┌──▼──────┐
            │  ADMIN  │ │MERCHANT │ │ STUDENT │
            └─────────┘ └────┬────┘ └──┬──────┘
                             │          │
                             │      ┌───▼──────┐
                             │      │  WALLET  │
                             │      └──────────┘
                          ┌──▼───┐
                          │BOUTIQUE
                          └──┬────┘
                             │
                           ┌─▼───┐
                           │PRODUCT
                           └──┬───┘
                    ┌──────────┼──────────┐
                    │          │          │
              ┌─────▼────┐ ┌───▼────┐ ┌──▼─────────┐
              │ COMMANDE │ │PAIEMENT│ │ VERSEMENT  │
              └──────┬───┘ └────────┘ └────────────┘
                     │
                ┌────▼──────────┐
                │COMMANDE_LIGNE │
                └────────────────┘
```

## **Règles importantes** :

1. ✅ **IDs, UUIDs, champs d'audit** = NE PAS afficher dans le diagramme
2. ✅ **Attributs métier** = AFFICHER avec leur type
3. ✅ **Enums** = AFFICHER avec stéréotype `<<enumeration>>`
4. ✅ **Relations** = AFFICHER avec cardinalité (1, *)
5. ✅ **Héritage** = Flèches triangulaires vers la classe mère
6. ✅ **Clés étrangères** = Afficher les cardinalités correctes

---

# 🎨 COULEURS SUGGÉRÉES

- **Classes métier** (Commande, Product, etc.) : Bleu clair
- **Classe User et héritiers** : Vert clair
- **Enums** : Rose/Mauve
- **Classes de jointure** (CommandeLigne) : Jaune/Orange
- **Classes transversales** (Wallet, Versement) : Gris clair

---

**Document généré le : 19 avril 2026**
**Utilisé pour créer le diagramme dans : DrawIO**
