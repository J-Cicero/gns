# Documentation des Cas d'Utilisation (Use Cases) par Rôle Utilisateur

Ce document décrit les différents cas d'utilisation pour chaque type d'utilisateur du système GNS (Global Network System), basés sur les interfaces et les flux actuellement implémentés dans les applications Frontend (Web et Mobile).

---

## 1. Étudiant (Étudiant Boursier / Régulier)
**Application cible :** `gns_mobile_student`

### Cas d'Utilisation Principaux :
*   **Inscription & Authentification** : S'inscrire en fournissant son Matricule, ses informations personnelles et en téléversant ses justificatifs (RIB, Mandat). Choisir son université et la banque de domiciliation.
*   **Paiement de la Scolarité** : Autoriser le prélèvement d'une partie de sa bourse pour payer directement les frais de scolarité à son université via l'application.
*   **Consultation de Solde** : Visualiser le montant de la bourse disponible (Wallet GNS).
*   **Paiement Marchand (via studCASH)** : Régler ses achats quotidiens auprès des marchands affiliés (Cantines, Boutiques, etc.) en scannant un QR code ou via NFC.
*   **Suivi des Dépenses** : Consulter l'historique complet de ses transactions et paiements effectués.
*   **Gestion du Profil** : Mettre à jour ses informations de base.

---

## 2. Marchand / Commerçant (Boutique)
**Application cible :** `gns_mobile_merchant`

### Cas d'Utilisation Principaux :
*   **Inscription & Création de Boutique** : S'inscrire en tant que gérant, renseigner le nom de la boutique, la banque de liquidation et le RIB pour recevoir les fonds.
*   **Encaissement (Recevoir des paiements)** : Générer des QR codes pour que les étudiants puissent payer, ou utiliser le NFC pour débiter les portefeuilles étudiants.
*   **Suivi des Ventes** : Visualiser les transactions entrantes en temps réel.
*   **Liquidation des Fonds** : Transférer l'argent accumulé sur le Wallet de la boutique vers le compte bancaire physique de la boutique (compte de liquidation).
*   **Historique et Comptabilité** : Consulter l'historique des ventes et le solde net disponible.

---

## 3. Administrateur GNS (AdminGNS)
**Application cible :** `gns_front` (Portail Admin GNS)

L'Administrateur GNS est le super-utilisateur (propriétaire) de la plateforme.

### Cas d'Utilisation Principaux :
*   **Gestion des Inscriptions (KYC)** : Valider ou rejeter les comptes étudiants et marchands après vérification des pièces justificatives (RIB, Mandat).
*   **Configuration du Système** : Paramétrer les frais, les commissions globales (GNS et Banque) et les montants des bourses.
*   **Gestion des Universités et Scolarités** : Ajouter de nouvelles universités partenaires et configurer les années scolaires en cours.
*   **Distribution des Bourses (Versements)** : Déclencher ou valider l'alimentation des portefeuilles virtuels (Wallets) des étudiants.
*   **Supervision Financière** : Consulter le tableau de bord complet avec le volume des transactions, les revenus générés par les commissions, et auditer toutes les transactions de la plateforme.
*   **Gestion des Cartes NFC** : Créer, associer et gérer le cycle de vie des cartes NFC remises aux étudiants.

---

## 4. Agent Bancaire (AdminBanque)
**Application cible :** `gns_front` (Portail Bancaire)

L'Agent Bancaire représente l'institution financière partenaire (ex: ECOBANK) qui détient les fonds réels et effectue les vrais virements.

### Cas d'Utilisation Principaux :
*   **Surveillance Financière** : Visualiser sur son tableau de bord le montant total "Net à liquider" (l'argent dû aux marchands) et les commissions générées par la banque.
*   **File de Liquidation** : Consulter la liste des marchands et universités qui demandent un retrait de leurs fonds.
*   **Exécution des Virements** : Marquer les liquidations comme "Traitées" après avoir effectué les virements depuis le Compte Central GNS vers les comptes des destinataires (RIB des marchands).
*   **Audit Ciblé** : 
    *   *Audit Étudiants* : Vérifier la bonne réception de la bourse par un étudiant et ses dépenses.
    *   *Audit Boutiques* : Vérifier l'authenticité des volumes de vente d'une boutique avant de liquider les fonds.
*   **Gestion du Profil Bancaire** : Consulter ses informations d'agence et le numéro de compte central GNS utilisé pour les opérations.

---

## 5. Administrateur Université (AdminUniversité)
**Application cible :** `gns_front` (Portail Université) *(En cours)*

### Cas d'Utilisation Principaux :
*   **Suivi des Étudiants** : Consulter la liste des étudiants de son établissement inscrits sur la plateforme studCASH.
*   **Suivi des Frais de Scolarité** : Vérifier en temps réel les paiements de scolarité effectués par les étudiants via leur bourse.
*   **Rapprochement Bancaire** : Vérifier le montant total des frais de scolarité qui doit être viré par la banque sur le compte de l'université.
