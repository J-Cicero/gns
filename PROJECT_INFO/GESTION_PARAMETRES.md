# ⚙️ GESTION DES PARAMÈTRES ET RÈGLES MÉTIER

Ce document explique l'architecture mise en place pour gérer dynamiquement les paramètres du système et les règles d'éligibilité (bourses) sans avoir à modifier le code source.

L'architecture repose sur une séparation stricte des responsabilités entre deux acteurs principaux :
1. **L'Administrateur GNS (Global)** : Gère le système, les frais, et la configuration technique.
2. **L'Administrateur DBS (Direction des Bourses et Stages)** : Gère uniquement les critères académiques, les âges limites et les montants accordés aux étudiants.

---

## 1. 🛡️ Module GNS (Global) : Entité `ParametreGns`

Cette entité centralise toutes les configurations financières et techniques globales du projet. 
**Seul l'Admin GNS a le droit de modifier ces valeurs.**

*   **Localisation** : `com.backend.gns.Shared.domain.models.ParametreGns`
*   **Cas d'usage** : Taux de commission, frais de création de carte, montants par défaut des portefeuilles, etc.

### Description des attributs :

| Attribut | Type | Description / Responsabilité |
| :--- | :--- | :--- |
| `id` | `Long` | Identifiant technique auto-incrémenté pour la base de données. |
| `trackingId` | `UUID` | Identifiant unique et immuable exposé aux APIs front-end. Protège l'ID technique. |
| `cle` | `String` | **(Unique)** La clé utilisée par le code pour retrouver la valeur. *Ex: `TAUX_COMMISSION_PAIEMENT`, `FRAIS_REVIENT_CARTE`*. Le code (ex: `PaiementServiceImpl`) utilise cette clé précise. |
| `valeur` | `String` | La valeur du paramètre stockée sous forme de texte. Le service se charge de la convertir (en `BigDecimal` ou `Integer`) selon le besoin. *Ex: `0.01`*. |
| `estActif` | `boolean` | Permet de désactiver un paramètre (Soft Delete) sans le supprimer de la base de données. Si `false`, le paramètre est ignoré. |
| `description` | `String` | Un texte explicatif lisible par l'humain pour aider l'Admin GNS dans le back-office à comprendre l'impact de ce paramètre. |

---

## 2. 🎓 Module Étudiant : Entité `RegleBourseDbs`

Cette entité centralise toutes les règles d'éligibilité académiques définies par l'État ou la Direction des Bourses.
**Seul l'Admin DBS a le droit de modifier ces valeurs.**

*   **Localisation** : `com.backend.gns.student.domain.models.RegleBourseDbs`
*   **Cas d'usage** : Âge maximum par cycle, montant de la bourse selon la mention (L1), montant de la bourse selon les crédits validés (L2, L3, Master).

### Description des attributs :

| Attribut | Type | Description / Responsabilité |
| :--- | :--- | :--- |
| `id` | `Long` | Identifiant technique auto-incrémenté pour la base de données. |
| `trackingId` | `UUID` | Identifiant unique et immuable exposé aux APIs front-end. |
| `codeUnique` | `String` | **(Unique)** Le code "en dur" que le service `EligibiliteServiceImpl` va appeler. *Ex: `AGE_MAX_LICENCE`, `L1_STANDARD`, `LICENCE_60_CREDITS`*. Ne doit pas être modifié par l'utilisateur. |
| `libelle` | `String` | Le nom "lisible" de la règle affiché dans le back-office de l'Admin DBS. *Ex: "Plafond Licence - 60 crédits et plus"*. |
| `valeurNumerique` | `BigDecimal` | La valeur principale de la règle. S'il s'agit d'un âge, ce sera `25.00` (lu comme entier). S'il s'agit d'un montant, ce sera `54000.00`. |
| `valeurTextuelle` | `String` | *(Optionnel)* Utilisé si une règle nécessite une condition textuelle complexe dans le futur. Actuellement prévu pour l'évolutivité. |
| `estActif` | `boolean` | Permet d'activer/désactiver une règle (ex: si l'État supprime temporairement la bourse de mérite). |
| `description` | `String` | Explications détaillées sur les conditions d'application de cette règle. |

---

## 💡 Exemple concret du fonctionnement

Avant, le code calculait la commission du paiement avec un `0.01` codé en dur :
```java
// AVANT (Mauvais)
BigDecimal commission = request.montantDebite().multiply(new BigDecimal("0.01"));
```

Désormais, le code demande au service (qui lit la table `PARAMETRE_GNS`) :
```java
// APRÈS (Bonne pratique)
BigDecimal taux = parametreGnsService.getValeurAsBigDecimal("TAUX_COMMISSION_PAIEMENT");
BigDecimal commission = request.montantDebite().multiply(taux);
```

Si le Taux de commission doit passer à 2% demain, l'Admin GNS se connecte à son dashboard, cherche la clé `TAUX_COMMISSION_PAIEMENT` et change sa `valeur` de `0.01` à `0.02`. **La modification est instantanée sur le prochain paiement, sans aucun redéploiement du serveur.**
