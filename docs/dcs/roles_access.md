# Mapping des accès par rôle — GNS (StudCash)

Date: 2026-05-25
Auteur: Analyse automatique du code

But: Ce document compare ce que le projet déclare/intende (constantes & docs) et ce qui est effectivement appliqué dans le code source actuel.

---

## Résumé exécutif (très court)
- Intention (déclarée) : les URLs sont groupées par rôle dans `JavaConstant` (voir sections ci‑dessous).
- État réel : `SecurityConfig` autorise **toutes** les requêtes (`.anyRequest().permitAll()`), la configuration CORS globale autorise toutes origines. Les annotations `@CrossOrigin("*")` ont été retirées des controllers (déplacées vers `corsConfigurationSource()`), mais la configuration globale reste permissive.
- Méthode-level security : quelques contrôleurs contiennent `@PreAuthorize(...)` (notamment `WalletController` et `DocumentEtudiantController`), mais le projet ne contient pas d'activation explicite de la method security (`@EnableMethodSecurity` / `@EnableGlobalMethodSecurity`). Donc, aujourd'hui, **il n'y a pas de contrôle effectif** par rôle appliqué globalement.
- Remarques de cohérence : certaines chaînes de rôles utilisées dans `@PreAuthorize` (ex: `ADMIN_UL`) ne correspondent pas exactement au `enum UserRole` (`UNIVERSITY_ADMIN`), créant des mismatches si la vérification de rôle est activée.

---

## Sources consultées
- `src/main/java/com/backend/gns/Shared/security/constants/JavaConstant.java` (définitions URL ↔ rôles)
- `src/main/java/com/backend/gns/Shared/security/config/SecurityConfig.java` (filtre CORS + règle d'autorisation)
- `src/main/java/com/backend/gns/Shared/security/userDetailsConf/UserPrincipal.java` (construction des `ROLE_`)
- Controllers (présence/retrait de `@CrossOrigin`) et quelques `@PreAuthorize`

---

## 1) Rôles identifiés (enum utilisé)
- `ETUDIANT`
- `COMMERCANT`
- `ADMIN_GNS`
- `ADMIN_BANQUE`
- `ADMIN_DBS`
- `UNIVERSITY_ADMIN` (équivalent docs: ADMIN_UL)

Note: Spring authorities sont construites comme `ROLE_<valeur>` via `UserPrincipal`.

---

## 2) Intention (JavaConstant) — URLs par rôle
- ADMIN (Administrateurs transverses)
  - /admin/**, /api/admin/**, /api/admin-university/**, /api/admin-dbs/**, /api/kyc/**, /api/documents/validate/**, /api/documents/reject/**

- ETUDIANT
  - /api/students/**, /api/wallets/**, /api/paiements/**, /api/commandes/**, /api/documents/**, /api/cards/**, /api/inscriptions/**, /api/scolarite/**

- COMMERCANT
  - /api/merchants/**, /api/boutiques/**, /api/products/**

- BANQUE / PORTAIL BANQUE
  - /api/bank-portal/**, /api/bank-operator/**, /api/versements/**

- UNIVERSITY_ADMIN
  - /api/admin-university/**

- ADMIN_DBS
  - /api/admin-dbs/**, /api/stats/**, /api/regles-bourse-dbs/**

(=> ces listes sont la « source d'intention » — ce que le projet veut restreindre par rôle)

---

## 3) État réel d'application des règles (code actuel)
- `SecurityConfig` :
  - `authorizeHttpRequests(...).anyRequest().permitAll()` → **Aucune restriction d'accès au niveau global**.
  - `CorsConfigurationSource` autorise toutes origines (`setAllowedOriginPatterns(["*"])`) — CORS global toujours permissif.
- `UserPrincipal` :
  - transforme la valeur de rôle en `SimpleGrantedAuthority("ROLE_" + user.getRole())`.
- Méthode-level protections observées :
  - `WalletController` : `@PreAuthorize("hasAnyRole('ETUDIANT', 'COMMERCANT', 'ADMIN_GNS', 'ADMIN_UL')")` sur `create` et `update` ; `@PreAuthorize("hasAnyRole('ADMIN_GNS', 'ADMIN_UL')")` sur `delete`.
  - `DocumentEtudiantController` : `@PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN_GNS')")` sur upload.
  - Quelques contrôleurs importent `PreAuthorize` mais n'appliquent pas d'annotations.
- Activation method-level : Aucun `@EnableMethodSecurity` ou `@EnableGlobalMethodSecurity` trouvé → les `@PreAuthorize` **ne sont pas actifs** tant que la method security n'est pas activée.

Conclusion : à date, malgré des intentions et quelques annotations locales, le code n'applique pas réellement de restrictions basées sur les rôles.

---

## 4) Détail par rôle — Ce que GNS DÉCLARE vs CE QUI EST RÉELLEMENT APPLIQUÉ

Format : (Déclaré) → (Réel aujourd'hui)

- ETUDIANT
  - Déclaré: CRUD profil étudiant, créer commandes, accéder à `/api/wallets`, `/api/paiements`, `/api/commandes`, upload KYC, consulter cartes et inscriptions.
  - Réel: Toutes les routes sont accessibles (pas d'autorisation), sauf quelques méthodes annotées `@PreAuthorize` (non actives). Donc, dans l'état actuel, un client peut appeler les endpoints ETUDIANT sans contrôle de rôle.

- COMMERCANT
  - Déclaré: gestion `Merchant`, `Boutique`, `Product` via `/api/merchants/**`,`/api/boutiques/**`,`/api/products/**`.
  - Réel: mêmes remarques — pas de restriction active, endpoints accessibles publiquement.

- ADMIN_GNS
  - Déclaré: contrôle global `/admin/**` et endpoints KYC/validation/documents/admin actions.
  - Réel: accessible publiquement (aucun blocage). Certaines actions critiques ne sont pas protégées au niveau global.

- ADMIN_BANQUE / BANQUE
  - Déclaré: accès au portail banque, opérations de versement, endpoints `/api/bank-operator/**`, `/api/versements/**`.
  - Réel: accessibles publiquement.

- ADMIN_DBS
  - Déclaré: gestion règles bourse, `/api/regles-bourse-dbs/**`, `/api/admin-dbs/**`.
  - Réel: accessibles publiquement. `RegleBourseDbsController` contient import `PreAuthorize` mais pas d'annotation active.

- UNIVERSITY_ADMIN (ADMIN_UL dans docs)
  - Déclaré: `/api/admin-university/**` (déblocage L1, validations UL).
  - Réel: accessible publiquement. Note: certains `@PreAuthorize` utilisent `ADMIN_UL` (string) alors que l'enum est `UNIVERSITY_ADMIN` — incohérence.

---

## 5) Incohérences et risques identifiés (prioritaires)
1. SecurityConfig permet tout (`permitAll`) → sécurité non appliquée.
2. CORS est configuré globalement pour `*` → retirer annotations a réduit la surface mais la configuration centrale reste permissive.
3. `@PreAuthorize` présentes mais method security non activée → sécurité inopérante.
4. Mismatch de noms de rôles : `ADMIN_UL` vs `UNIVERSITY_ADMIN` → cassera l'autorisation si activée.
5. `UserMapper` crée par défaut `UserRole.ETUDIANT` pour tous les utilisateurs créés — attention si création automatique d'utilisateurs.

---

## 6) Recommandations concrètes (ordre prioritaire)
1. Ne pas supposer que `@PreAuthorize` fonctionne : activer la method security si vous voulez des contrôles méthode‑level (`@EnableMethodSecurity`) OU déplacer les règles dans `SecurityConfig` (URL-based) en configurant `requestMatchers(...).hasRole(...)`.
2. Aligner les noms : harmoniser `UserRole` enum et toutes les références `@PreAuthorize` (utiliser les mêmes identifiants sans préfixe `ROLE_`).
3. Mettre en place les règles URL-based dans `SecurityConfig` en utilisant les tableaux `JavaConstant.ETUDIANT_URLS`, `ADMIN_URLS`, etc., puis remplacer `anyRequest().permitAll()` par des `requestMatchers(...).hasRole(...)` et `anyRequest().authenticated()` par défaut.
4. Restreindre la CORS globale aux origines frontend réelles (ne pas garder `*`).
5. Ajouter des tests d'intégration qui valident qu'un token avec `ROLE_ETUDIANT` ne peut pas accéder aux endpoints `ADMIN`.

---

## 7) Prochaine tâche possible (si vous le souhaitez)
- Je peux générer automatiquement un patch pour `SecurityConfig` qui :
  - applique `requestMatchers(JavaConstant.ADMIN_URLS).hasRole("ADMIN_GNS")` etc.,
  - active la method security si vous préférez `@PreAuthorize`,
  - et restreint CORS à origines spécifiques.

Dites-moi si vous voulez que je crée ce patch maintenant (je peux le faire), ou seulement produire des tests d'intégration illustrant la configuration souhaitée.

---

Fichier généré automatiquement: `gns/docs/dcs/roles_access.md`

