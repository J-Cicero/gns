# Défis Architecturaux Spring Modulith

Ce document détaille les problèmes rencontrés lors de la mise en place d'une architecture modulaire avec Spring Modulith, ainsi que les tentatives de résolution et l'état actuel.

## Contexte Initial

Le projet a été initialement trouvé avec de nombreuses erreurs de compilation dues principalement à :
1.  Des déclarations de package incorrectes dans presque tous les fichiers Java, ne correspondant pas à la structure des répertoires.
2.  Une configuration Lombok défaillante dans le `pom.xml`, empêchant la génération des getters/setters et causant des erreurs "cannot find symbol".

Ces problèmes ont été résolus de manière systématique, permettant au projet de compiler.

## Configuration Spring Modulith

Pour faire respecter une architecture modulaire, des fichiers `package-info.java` ont été introduits pour chaque module (`admin`, `commerce`, `paiement`, `student`, `wallet`, `Shared`) avec des règles de dépendances explicites (`allowedDependencies`). Un test (`ArchitectureModulesTest`) a été créé pour vérifier la conformité de l'architecture via `ApplicationModules.of(GnsApplication.class).verify()`.

## Problèmes de Cycles et Dépendances Non Autorisées (Phases Précédentes)

Lors des premières exécutions du test Modulith, des violations architecturales majeures ont été détectées :
*   **Cycles de dépendances :** Par exemple, `admin -> student -> wallet -> admin`, `student <-> wallet`, `commerce <-> wallet`.
*   **Dépendances non autorisées :** Des modules utilisaient des types d'autres modules sans que cela soit déclaré dans leurs `allowedDependencies`. Par exemple, le module `Shared` utilisait `TypeDocument` du module `student`.

Pour résoudre ces problèmes et respecter les règles strictes de Modulith et les directives de l'utilisateur ("pas de relations bidirectionnelles"), plusieurs actions ont été entreprises :
1.  **Déplacement d'Enums partagées :** `Banque.java`, `KycStatus.java`, `TypeDocument.java` ont été déplacés vers le module `Shared` pour les rendre accessibles à tous sans créer de dépendances croisées.
2.  **Refactorisation du Code pour Casser les Cycles :**
    *   Les méthodes `versementAusTousEtudiants` et `versementAusToutesBoutiques` de `VersementServiceImpl` (dans `wallet`) ont été supprimées car elles créaient des dépendances non désirées vers `student` et `commerce`.
    *   La dépendance à `AdminRepository` a été supprimée de `VersementMapper` (dans `wallet`).
    *   La méthode `listPendingMandates` et la dépendance à `StudentService` ont été supprimées de `BankOperatorController` (dans `admin`).

## Évolution de l'Intention Architecturale de l'Utilisateur

Face aux difficultés persistantes avec les cycles, l'utilisateur a émis une nouvelle directive cruciale :
*   **Intégrer le module `wallet` dans `Shared` :** L'idée est de faire de `wallet` une partie du noyau `Shared`, puisque de nombreux autres modules l'utilisent.
*   **Restaurer les fonctions précédemment supprimées :** Les méthodes et logiques supprimées de `VersementServiceImpl`, `VersementMapper`, `VersementService` et `BankOperatorController` devaient être restaurées dans leurs emplacements d'origine.

Ces changements ont été mis en œuvre :
1.  Le répertoire `src/main/java/com/backend/gns/wallet` a été déplacé vers `src/main/java/com/backend/gns/Shared/wallet`.
2.  Toutes les déclarations de package et les imports internes/externes ont été mis à jour de `com.backend.gns.wallet` à `com.backend.gns.Shared.wallet`.
3.  Le fichier `package-info.java` du module `wallet` a été supprimé, car `wallet` n'est plus un module indépendant.
4.  Les fonctions et dépendances supprimées ont été restaurées.

## Problème Actuel : `org.springframework.modulith.core.Violations` Persistantes

Malgré toutes ces modifications et le fait que le projet compile avec succès, le test Modulith `modules.verify()` continue d'échouer avec des `org.springframework.modulith.core.Violations`.

Ce qui est particulièrement déroutant est que le rapport d'erreur liste des dépendances qui sont **explicitement autorisées** dans le `package-info.java` du module `paiement` (par exemple, `paiement` dépend de `commerce` ou `student`, et ces dépendances sont listées dans `allowedDependencies`). Le rapport n'affiche plus non plus de messages clairs comme "Cycle detected".

Cela suggère que :
1.  **Des cycles implicites subsistent :** Malgré les refactorisations, Modulith détecte toujours des dépendances bidirectionnelles ou des cycles plus subtils qui ne sont pas cassés.
2.  **Interprétation de Modulith :** Il est possible que même si les dépendances sont individuellement 'Allowed', leur combinaison ou la structure globale du projet viole une règle Modulith non évidente (par exemple, une "acyclicité" plus stricte que ce que les `allowedDependencies` peuvent exprimer).

## Prochaine Étape : Analyse Approfondie ou Assouplissement des Règles

Pour que le test Modulith `modules.verify()` passe tout en respectant votre souhait d'une architecture sans relations bidirectionnelles strictes, il faudrait :
1.  **Entreprendre une analyse architecturale fonctionnelle plus profonde :** Identifier les dépendances restantes et envisager des refactorisations plus radicales (extraction d'interfaces, utilisation d'événements) pour éliminer toute forme de couplage implicite ou de cycle. Cela dépasse le cadre des corrections structurelles et nécessiterait une compréhension approfondie de la logique métier.
2.  **Accepter que le test échoue :** Si la refactorisation du code métier est jugée trop complexe ou hors de portée à ce stade, il faudrait accepter que le test Modulith échoue et que l'architecture actuelle, bien que fonctionnelle, n'est pas entièrement validée par Modulith.
3.  **Assouplir les règles du test Modulith (si possible) :** En modifiant le test lui-même pour qu'il ignore certains types de violations (par exemple, des cycles spécifiques), bien que cela aille à l'encontre de l'objectif d'une architecture Modulith stricte.

Nous sommes à un point où la résolution de l'échec du test Modulith nécessite soit une refonte de la logique métier, soit une acceptation de l'échec du test.

---
**END OF REPORT**
