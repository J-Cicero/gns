# Synthèse du Projet GNS

Ce document fournit une vue d'ensemble actualisée de l'architecture et des fonctionnalités du projet GNS, basée sur une analyse des fichiers clés du codebase.

## 🎯 Objectif du Projet

Le projet GNS semble être une plateforme de gestion d'étudiants, de commerçants, de paiements et de portefeuilles, intégrant des fonctionnalités de KYC (Know Your Customer), de gestion de commandes et de produits. Il vise à offrir une solution backend robuste pour diverses interactions financières et administratives.

## 🚀 Technologies Clés

Le projet est développé avec les technologies suivantes :

*   **Spring Boot 4.0.5 (Parent) :** Framework pour le développement rapide d'applications Java.
*   **Java 17 :** Langage de programmation.
*   **Maven :** Outil de gestion de projet et de construction.
*   **Spring Boot Starter WebMVC :** Pour la création d'applications web et d'API RESTful.
*   **Spring Boot Starter Data JPA :** Pour l'accès aux données avec JPA (Java Persistence API).
*   **PostgreSQL :** Base de données relationnelle (via le pilote `postgresql`).
*   **Spring Boot Starter Validation :** Poubackend robuste pour diverses interactions financières et administratives.r la validation des données.
*   **Springdoc OpenAPI Starter WebMVC UI :** Pour la génération de documentation d'API (Swagger UI).
*   **Spring Modulith Starter Core & Test :** Indique une architecture modulaire en cours d'adoption.
*   **Lombok :** Pour réduire le boilerplate code (constructeurs, getters/setters).
*   **Cloudinary :** Service de gestion d'images et de vidéos (dépendance `cloudinary-http44`).
*   **JJWT (Java JWT) :** Pour la gestion des JSON Web Tokens, suggérant une sécurité basée sur des tokens.
*   **Spotless Maven Plugin :** Pour le formatage automatique du code (Google Java Format).
*   **Gemini API :** Intégration d'un service d'IA (via `gemini.api-key` et `gemini.model` dans `application.properties`).

## 🏗️ Architecture Générale

Le projet GNS suit une architecture en couches (Layered Architecture), typique des applications Spring Boot, avec une indication d'adoption de l'approche **Spring Modulith** pour organiser le code en modules métier.

1.  **Couche `application` (Présentation) :**
    *   **`controllers/` :** Expose les API RESTful pour interagir avec l'application. Chaque contrôleur gère un domaine spécifique (ex: `StudentController` pour les étudiants, `WalletController` pour les portefeuilles).
    *   **`dtos/` :** Data Transfer Objects utilisés pour structurer les requêtes et les réponses des API, assurant une séparation claire entre les modèles de domaine et l'exposition des données.
    *   **`mappers/` :** (Non lu spécifiquement mais implicite) Probablement responsable de la conversion entre DTOs et entités de domaine.

2.  **Couche `domain` (Logique Métier) :**
    *   **`models/` :** Contient les entités JPA (ex: `Student`, `Wallet`, `Commande`), représentant les objets métier persistants. Ces entités définissent la structure des données et leurs relations.
    *   **`services/` :** Interfaces définissant les contrats pour la logique métier de chaque domaine.
    *   **`services/impl/` :** Implémentations concrètes des services, où la logique métier est appliquée, souvent en interagissant avec les repositories.
    *   **`enums/` :** Définit des énumérations pour des états ou des types spécifiques à l'application (ex: `KycStatus`, `WalletType`, `CommandeStatut`).

3.  **Couche `infrastructure` (Persistance) :**
    *   **`repositories/` :** Interfaces héritant de `JpaRepository`, fournissant des méthodes pour l'accès aux données persistantes (CRUD et requêtes personnalisées) pour chaque entité de domaine.

4.  **Couche `Shared` (Composants Partagés) :**
    *   **`ai/` :** Intégration de services d'intelligence artificielle (ex: `GeminiExtractionService`).
    *   **`config/` :** Configurations globales (ex: `CloudinaryConfig`).
    *   **`storage/` :** Services pour la gestion du stockage externe (ex: `CloudinaryStorageService` pour l'upload de fichiers).
    *   **`user/` :** Modules de gestion des utilisateurs, incluant contrôleurs, modèles, services et repositories pour les utilisateurs génériques.
    *   **`utils/` :** Classes utilitaires (ex: `BaseEntity`).

## 🔑 Entités Clés et Relations Détaillées

Cette section décrit en détail chaque entité JPA du projet GNS, y compris ses attributs, leurs types, et les relations établies avec d'autres entités.

### Admin
*   **Hérite de:** `User`
*   **Attributs :**
    *   `numeroCompte`: `String` (nullable)
*   **Relations :** (Hérite des relations de `User` s'il y en a de génériques)

### Bank
*   **Hérite de:** `User`
*   **Attributs :** (Aucun attribut spécifique au-delà de ceux de `User` dans ce fichier)
*   **Relations :** (Hérite des relations de `User` s'il y en a de génériques)

### Boutique
*   **Hérite de:** `BaseEntity`
*   **Attributs :**
    *   `id`: `Long`
    *   `trackingId`: `UUID` (unique, non modifiable)
    *   `nomBoutique`: `String` (longueur 100, non nullable)
    *   `categorieShop`: `String` (longueur 100, non nullable)
    *   `cheminCarteEDJ`: `String` (longueur 255)
    *   `statutKYC`: `KycStatus` (Enum, longueur 20, non nullable)
    *   `latitude`: `Double`
    *   `longitude`: `Double`
*   **Relations :**
    *   `merchant`: `@ManyToOne` avec `Merchant` (clé étrangère `merchant_id`, non nullable, FetchType.LAZY)
    *   `wallet`: `@OneToOne` avec `Wallet` (`mappedBy = "boutique"`)

### Card
*   **Hérite de:** `BaseEntity`
*   **Attributs :**
    *   `id`: `Long`
    *   `trackingId`: `UUID` (unique, non modifiable)
    *   `qrCodeStaticUuid`: `String` (unique, longueur 100, non nullable)
    *   `cardStatus`: `CardStatus` (Enum, longueur 20, non nullable)
*   **Relations :**
    *   `student`: `@ManyToOne` avec `Student` (clé étrangère `student_id`, non nullable, FetchType.LAZY)

### Commande
*   **Hérite de:** `BaseEntity`
*   **Attributs :**
    *   `id`: `Long`
    *   `trackingId`: `UUID` (unique, non modifiable)
    *   `reference`: `String` (unique, longueur 36, non nullable)
    *   `montantTotal`: `BigDecimal` (non nullable)
    *   `dateCommande`: `LocalDateTime`
    *   `statut`: `CommandeStatut` (Enum, longueur 20)
    *   `qrCodeEphemere`: `String` (longueur 100)
    *   `qrExpiresAt`: `LocalDateTime`
*   **Relations :**
    *   `student`: `@ManyToOne` avec `Student` (clé étrangère `student_id`, non nullable)
    *   `boutique`: `@ManyToOne` avec `Boutique` (clé étrangère `boutique_id`, non nullable)

### CommandeLigne
*   **Hérite de:** `BaseEntity`
*   **Attributs :**
    *   `id`: `Long`
    *   `trackingId`: `UUID` (unique, non modifiable)
    *   `quantite`: `int` (non nullable)
    *   `prixUnitaire`: `BigDecimal` (non nullable)
*   **Relations :**
    *   `commande`: `@ManyToOne` avec `Commande` (clé étrangère `commande_id`, non nullable, FetchType.EAGER)
    *   `product`: `@ManyToOne` avec `Product` (clé étrangère `product_id`, non nullable, FetchType.EAGER)

### DocumentEtudiant
*   **Hérite de:** `BaseEntity`
*   **Attributs :**
    *   `id`: `Long`
    *   `trackingId`: `UUID` (unique, non modifiable)
    *   `type`: `TypeDocument` (Enum, longueur 30, non nullable)
    *   `cheminFichier`: `String` (longueur 255, non nullable)
    *   `statut`: `StatutDocument` (Enum, longueur 20, non nullable)
    *   `commentaireRejet`: `String` (longueur 255)
    *   `dateDepot`: `LocalDateTime` (non nullable)
    *   `dateValidation`: `LocalDateTime`
    *   `donneesExtraites`: `String` (TEXT)
*   **Relations :**
    *   `student`: `@ManyToOne` avec `Student` (clé étrangère `student_id`, non nullable, FetchType.LAZY)
    *   `inscription`: `@ManyToOne` avec `InscriptionAnnuelle` (clé étrangère `inscription_id`, non nullable, FetchType.LAZY)

### InscriptionAnnuelle
*   **Hérite de:** `BaseEntity`
*   **Attributs :**
    *   `id`: `Long`
    *   `trackingId`: `UUID` (unique, non modifiable)
    *   `anneeAcademique`: `String` (longueur 20, non nullable)
    *   `niveau`: `StudentNiveau` (Enum, longueur 10, non nullable)
    *   `creditsTotalValides`: `int` (non nullable)
    *   `mentionBac`: `String` (longueur 10)
    *   `estBoursier`: `boolean` (non nullable, valeur par défaut `false`)
    *   `typeBourse`: `TypeBourse` (Enum, longueur 20)
    *   `fraisScolaritePayes`: `boolean` (non nullable, valeur par défaut `false`)
    *   `statut`: `StatutInscription` (Enum, longueur 30, non nullable)
    *   `source`: `SourceVerification` (Enum, longueur 20)
    *   `dateActivation`: `LocalDateTime`
*   **Relations :**
    *   `student`: `@ManyToOne` avec `Student` (clé étrangère `student_id`, non nullable, FetchType.LAZY)

### Merchant
*   **Hérite de:** `User`
*   **Attributs :** (Aucun attribut spécifique au-delà de ceux de `User` dans ce fichier)
*   **Relations :** (Hérite des relations de `User` s'il y en a de génériques)

### Paiement
*   **Hérite de:** `BaseEntity`
*   **Attributs :**
    *   `id`: `Long`
    *   `trackingId`: `UUID` (unique, non modifiable)
    *   `commission`: `BigDecimal` (non nullable)
    *   `montantDebite`: `BigDecimal` (non nullable)
    *   `date`: `LocalDateTime` (non nullable)
    *   `typePaiement`: `PaiementType` (Enum, longueur 20)
    *   `statutPaiement`: `PaiementStatut` (Enum, longueur 20)
*   **Relations :**
    *   `commande`: `@ManyToOne` avec `Commande` (clé étrangère `commande_id`, non nullable)
    *   `student`: `@ManyToOne` avec `Student` (clé étrangère `student_id`, non nullable, FetchType.LAZY)
    *   `wallet`: `@ManyToOne` avec `Wallet` (clé étrangère `wallet_id`, non nullable)

### Product
*   **Hérite de:** `BaseEntity`
*   **Attributs :**
    *   `id`: `Long`
    *   `trackingId`: `UUID` (unique, non modifiable)
    *   `nom`: `String` (longueur 100, non nullable)
    *   `description`: `String` (TEXT)
    *   `prix`: `BigDecimal` (non nullable)
    *   `stock`: `int` (non nullable)
    *   `estDisponible`: `Boolean` (non nullable, valeur par défaut `true`)
    *   `dateAjout`: `LocalDateTime` (non nullable)
*   **Relations :**
    *   `boutique`: `@ManyToOne` avec `Boutique` (clé étrangère `boutique_id`, non nullable)

### Student
*   **Hérite de:** `User`
*   **Attributs :**
    *   `numEtudiantUL`: `String` (longueur 50, unique, non nullable)
    *   `pinCode`: `String` (longueur 255, unique, non nullable)
    *   `RIB`: `String` (longueur 100, unique, non nullable)
    *   `mandatSigne`: `boolean` (non nullable, valeur par défaut `false`)
    *   `mandatTimestamp`: `LocalDateTime`
    *   `mandatIpAddress`: `String` (longueur 45)
    *   `statutKYC`: `KycStatus` (Enum, longueur 20, non nullable)
    *   `banque`: `Banque` (Enum, longueur 20, non nullable)
*   **Relations :**
    *   `wallet`: `@OneToOne` avec `Wallet` (`mappedBy = "student"`)

### Wallet
*   **Hérite de:** `BaseEntity`
*   **Attributs :**
    *   `id`: `Long`
    *   `trackingId`: `UUID` (unique, non modifiable)
    *   `typeWallet`: `WalletType` (Enum, longueur 20, non nullable)
    *   `statutWallet`: `WalletStatus` (Enum, longueur 20, non nullable)
    *   `solde`: `BigDecimal` (non nullable)
    *   `plafond`: `BigDecimal` (non nullable)
    *   `estVerrouille`: `Boolean` (non nullable, valeur par défaut `false`)
    *   `estGele`: `Boolean` (non nullable, valeur par défaut `false`)
    *   `dateCreation`: `LocalDateTime`
*   **Relations :**
    *   (Relation `@OneToOne` avec `Student` via `Student.wallet`)
    *   (Relation `@OneToOne` avec `Boutique` via `Boutique.wallet`)


## 🧩 Adhésion à Spring Modulith

La présence de `spring-modulith-starter-core` et `spring-modulith-starter-test` dans le `pom.xml` indique une intention d'adopter ou une adoption en cours d'une architecture modulaire avec Spring Modulith. Cette approche favorise la décomposition de l'application en modules métier bien définis et faiblement couplés. Cela est en accord avec la structure actuelle des packages (`application`, `domain`, `infrastructure`, `Shared`) qui peut être organisée en modules distincts.

## 💡 Recommandations et Potentiels d'Amélioration

1.  **Documentation des Modules :** Avec Spring Modulith, il serait bénéfique de documenter explicitement les modules, leurs dépendances et leurs interfaces exposées, pour maintenir la clarté de l'architecture modulaire.
2.  **Transactions :** Vérifier que les transactions sont correctement gérées au niveau des services pour assurer l'intégrité des données, surtout dans les opérations impliquant plusieurs entités (ex: commande, paiement, mise à jour du portefeuille).
3.  **Gestion des Erreurs :** Les contrôleurs utilisent des blocs `try-catch` génériques renvoyant `INTERNAL_SERVER_ERROR`. Il serait préférable de mettre en place une gestion d'erreurs plus fine avec des exceptions personnalisées et des `ControllerAdvice` pour des réponses HTTP plus spécifiques et informatives (ex: `400 Bad Request` pour des validations, `409 Conflict` pour des problèmes de concurrence).
4.  **Tests Unitaires et d'Intégration :** L'existence de `GnsApplicationTests.java` et `JpaRelationshipsIntegrationTest.java` est un bon début. Il est crucial de maintenir et d'étendre la couverture des tests, notamment pour la logique métier des services et les contrôleurs.
5.  **Utilisation de DTOs :** Assurez-vous que les DTOs sont utilisés de manière cohérente dans la couche `application` pour éviter d'exposer directement les entités de domaine via l'API, ce qui peut poser des problèmes de sécurité et de flexibilité. Les mappers jouent ici un rôle crucial.
6.  **Sécurité :** Approfondir la configuration de Spring Security avec JWT pour s'assurer que tous les endpoints sont correctement sécurisés et que les rôles et permissions sont bien gérés.
7.  **Configuration des Environnements :** L'utilisation de `application-dev.properties` et `application-test.properties` est une bonne pratique. Assurez-vous que toutes les configurations sensibles sont externalisées et gérées de manière sécurisée (par exemple, via des variables d'environnement en production).

## Conclusion

Le projet GNS présente une architecture solide basée sur Spring Boot et JPA, avec une bonne structuration en couches. L'adoption de Spring Modulith est une excellente direction pour maintenir la modularité et la maintenabilité à mesure que le projet grandit. En se concentrant sur les recommandations ci-dessus, notamment la gestion des erreurs, la couverture des tests et la sécurité, le projet peut continuer à évoluer de manière robuste et performante.
