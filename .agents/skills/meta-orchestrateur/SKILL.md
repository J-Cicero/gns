---
name: meta-orchestrateur
description: >-
  Méta-méthode d'ingénierie et d'orchestration de projet combinant BMAD (Architecture modulaire & DDD),
  Graphify (Graphe de dépendances et d'impact), UX Pilot (Conception d'expérience centrée utilisateur)
  et Motion Standards (Micro-interactions et transitions soignées).
  À activer pour auditer, concevoir, implémenter et valider toute fonctionnalité ou projet.
---

# Super-Skill : Le Méta-Orchestrateur (BMAD + Graphify + UX Pilot + Motion Standards)

Ce guide formalise le cadre d'ingénierie suprême pour concevoir, analyser, auditer et faire évoluer des projets logiciels complexes (tels que StudCash/GNS) sans jamais dégrader le fonctionnement existant ni altérer les règles métier établies.

---

## 🏛️ PILIER 1 : BMAD (Business-Model Architecture & Domain-Driven Design)

Le pilier **BMAD** garantit que chaque ligne de code est l'expression fidèle et inaltérable d'une règle métier et d'une réalité économique.

### Règles d'or :
1. **Vérité Métier Inviolable** :
   * Ne jamais modifier un flux fonctionnel existant (ex: circuit fermé, avance sur créance EWA, mandat de prélèvement, statut KYC) sans spécification expresse.
   * Tout nouveau besoin doit d'abord être formalisé dans le modèle de domaine avant d'écrire la moindre interface.
2. **Cloisonnement Strict en 3-Tiers & DDD** :
   * **Domain / Entities** : Entités pures, règles d'invariants (ex: montant > 0, solde suffisant, statut KYC).
   * **Application / Services** : Orchestration des transactions atomiques (`@Transactional`), DTOs immuables (Records Java), cas d'utilisation précis.
   * **Infrastructure / Repositories & Controllers** : Persistance (PostgreSQL/JPA), protocoles d'exposition (REST), clients externes (Cloudinary, APIs tierces).
3. **Sécurité et Non-Répudiation** :
   * Contrôle d'accès basé sur les rôles (RBAC) étanche à chaque endpoint.
   * Pas de données sensibles stockées en clair (hachage BCrypt pour mots de passe et codes PIN).
   * Jetons cryptographiques JWT sans état (RFC 7518 HMAC-SHA256).

---

## 🕸️ PILIER 2 : GRAPHIFY (Cartographie de Dépendances & Analyse d'Impact)

Le pilier **Graphify** traite le projet comme un graphe orienté de dépendances techniques et fonctionnelles.

### Démarche systématique avant TOUTE modification :
1. **Navigation du Graphe d'Impact** :
   * Avant de toucher à un champ ou une méthode (ex: `User`, `Wallet`, `Transaction`), cartographier tous les nœuds dépendants :
     * *Quels repositories sont impactés ?*
     * *Quels DTOs et contrôleurs exposent cette donnée ?*
     * *Quels composants frontend (Angular/Ionic) consomment cet endpoint ?*
2. **Vérification d'Intégrité Statique & Dynamique** :
   * Compilation du backend (`mvn compile`) pour vérifier qu'aucun contrat d'interface n'est rompu.
   * Validation des types TypeScript (`ng build` ou `tsc --noEmit`) pour assurer la cohérence des modèles frontends.
3. **Zéro Effet de Bord** :
   * Si une méthode doit évoluer, privilégier la surcharge ou l'extension plutôt que la mutation cassante.

---

## 🧭 PILIER 3 : UX PILOT (Expérience Utilisateur & Pilotage des Rôles)

Le pilier **UX Pilot** assure que chaque interface répond parfaitement aux compétences, aux contraintes de terrain et aux attentes ergonomiques de son acteur dédié.

### Principes directeurs :
1. **Cloisonnement des Parcours par Rôle** :
   * **Super Admin / Banque (Web)** : Densité d'information, efficacité des filtres, tableaux paginés, alertes de surveillance et actions de validation claires.
   * **Étudiant (Mobile)** : Clarté absolue du solde disponible, génération instantanée du QR Code éphémère, historique des débits et rassurement permanent.
   * **Marchand (Mobile / Caisse)** : Vitesse d'exécution tactile en boutique, pavé numérique dédié, scan immédiat par caméra, confirmation sonore/visuelle de l'encaissement et suivi du quota.
2. **Accessibilité & Résilience Réseau** :
   * Architecture Progressive Web App (PWA) garantissant l'accès même avec un faible débit réseau (3G/4G sur campus).
   * Gestion explicite des états : chargement (*skeletons*), succès, erreur compréhensible (*toast/banner*), et état vide (*empty state* bienveillant).
3. **Validation Préventive des Formulaires** :
   * Validation synchrone en temps réel avec messages d'erreurs contextuels clairs avant toute soumission.

---

## 🎬 PILIER 4 : FRAMER MOTION & MOTION STANDARDS (Transitions & Micro-Interactions)

Ce pilier donne vie à l'interface pour renforcer la confiance et la clarté visuelle.

### Directives d'animation :
1. **Feedback Transactionnel Immédiat** :
   * Toute action financière critique (débit, crédit, validation KYC, confirmation de scan) doit fournir un retour visuel sans ambiguïté :
     * Transition d'état fluide (vert pour succès, rouge pour échec, pulsation douce pendant le traitement).
     * Compteur de solde animé lors de la mise à jour des fonds.
2. **Transitions Ergonomiques et Douces** :
   * Entrée de modale avec léger zoom et fondu enchaîné (*ease-out* 200-300ms).
   * Ouverture des tiroirs de navigation (*drawers*) et onglets mobiles sans saccade.
3. **Sobriété et Performance** :
   * Pas d'animations superflues ou distrayantes : l'animation doit toujours servir la compréhension de l'état du système financier.

---

## 🔄 PROTOCOLE D'EXÉCUTION DU MÉTA-ORCHESTRATEUR

Pour toute tâche, demande ou échange futur, le méta-orchestrateur applique ce workflow en 4 temps :

```
[1. BMAD]            Vérification de la règle métier & du modèle de données
       │
[2. GRAPHIFY]        Cartographie des dépendances et test d'impact (Back -> Front)
       │
[3. UX PILOT]        Validation du parcours utilisateur et de l'ergonomie par rôle
       │
[4. MOTION & CODE]   Implémentation soignée avec micro-interactions et vérification builds
```
