# Résumé technique — Entités, modules et accès par rôle

Date: 2026-05-25
Projet: GNS (StudCash)

## 1) Modules principaux et entités observées

- Shared (code transverse)
  - `BaseEntity`, `Wallet` (classe unique simplifiée), `ParametreGns`, éléments sécurité (`UserPrincipal`, `SecurityConfig`), mappers/utilitaires.
  - Users transverses: `User` (abstraite) → sous-types: `Admin`, `UniversityAdmin`, `BankOperator`.

- Student
  - Entités: `Student`, `ScolariteYear`, `DocumentEtudiant`, `DocumentRequis`, `InscriptionAnnuelle`, `Card`, `BanqueEtudiant`, `RegleBourseDbs`.

- Commerce
  - Entités: `Merchant`, `Boutique`, `Product`.

- Paiement / Commandes
  - Entités: `Commande`, `CommandeLigne`, `Paiement`, `Versement`.

- Observations supplémentaires
  - Beaucoup d'entités héritent de `BaseEntity` (audit).
  - Wallet est une entité unique avec un `WalletType`, gérée automatiquement par les services (StudentService, BoutiqueService, etc.) via Cascade JPA.
  - Certains fichiers (controllers) utilisaient `@CrossOrigin("*")` — risque de sécurité identifié.

## 2) Modèle de rôles et attribution d'accès (comment c'est implémenté)

- Rôles observés (extraits de la config `SECURITY.md` et du code):
  - `ETUDIANT`, `COMMERCANT`, `ADMIN_GNS`, `ADMIN_BANQUE`, `ADMIN_DBS`, `ADMIN_UL`.
- Mécanisme observé: `UserPrincipal` construit les autorités avec `new SimpleGrantedAuthority("ROLE_" + user.getRole())` — le rôle est une chaîne stockée sur l'entité `User`.
- Pattern recommandé déjà appliqué partiellement: URL patterns autorisés par rôle dans `SecurityConfig` (ex: `/api/students/**` → `ETUDIANT`, `/api/merchants/**` → `COMMERCANT`, `/admin/**` → `ADMIN_GNS`).

## 3) Recommandations d'attribution d'accès (pratiques)

- Centraliser la définition des rôles dans une `enum UserRole` et utiliser cette enum partout (mappers, sécurité, tests).
- Conserver la convention `ROLE_<ROLE_NAME>` pour Spring Security, mais vérifier l'alignement entre la valeur stockée en base et les constantes utilisées dans `SecurityConfig`.
- Préférer `@PreAuthorize("hasRole('ADMIN_GNS')")` ou `hasAnyRole(...)` au niveau des contrôleurs pour les actions sensibles (KYC, versement, suppression de wallet).
- Éviter `@CrossOrigin("*")` et remplacer par une whitelist d'origines via la configuration globale CORS dans `SecurityConfig`.
- Introduire un mapping clair role→scopes (exemples):
  - ETUDIANT → accès CRUD sur son profil, `Commande` creation, `Wallet` (débit demandé), upload documents KYC.
  - COMMERCANT → gestion `Boutique`, `Product`, consultation commandes reçues.
  - ADMIN_GNS → gestion globale (KYC validation, approvisionnement, reporting, accès admin/*).
  - ADMIN_DBS → règles de bourse (`RegleBourseDbs`) CRUD.
  - ADMIN_BANQUE → accès aux endpoints bancaires et versements batch.
  - ADMIN_UL → gestion des validations universitaires et déblocage L1.

## 4) Liens rapides (fichiers clés)
- `SecurityConfig` et `JavaConstant` : configuration des URL/roles (voir docs `SECURITY.md`).
- `UserPrincipal` : place où les rôles sont transformés en `GrantedAuthority`.
- Entités JPA observées : `student/domain/models`, `commerce/domain/models`, `Shared`.

## 5) Actions proposées (priorisées)
1. Uniformiser les rôles (enum + constantes) — évite mismatch.
2. Ajouter `@PreAuthorize` sur endpoints sensibles et revoir tous les controllers exposés.
3. Retirer `@CrossOrigin("*")` et mettre une configuration CORS restrictive.
4. Ajouter tests d'accès pour chaque rôle (integration tests) et un audit des endpoints non protégés.

---
Résumé généré automatiquement par analyse du code source et de la documentation du projet.
*Note: Le refactor Wallet vers une classe unique a été effectué le 2026-05-26.*
