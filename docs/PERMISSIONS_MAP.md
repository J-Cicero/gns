# Mapping des Permissions Actuelles (À restaurer en production)

Ce document sert de référence pour le contrôle d'accès (RBAC) que nous avons actuellement. Pour la phase d'intégration frontend, ces accès ont été rendus publics. Ils devront être rétablis avant la mise en production.

## Rôles et Accès

| Rôle | URL / Fonctionnalité |
| :--- | :--- |
| **ADMIN_GNS** | Accès total : Stats, Comptes, KYC, Documents, ParamètresGns, Campagnes. |
| **ETUDIANT** | Inscriptions, Documents, Cartes, Portefeuille (Wallets), Paiements. |
| **COMMERCANT** | Commandes, Produits, Boutiques, Paiements. |
| **ADMIN_BANQUE** | Portefeuille, Versements, Surveillance. |

---
*Référence : État du fichier `SecurityConfig.java` au moment de la désactivation temporaire de la sécurité.*
