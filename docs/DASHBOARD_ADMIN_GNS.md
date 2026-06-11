# Documentation : Dashboard Admin GNS

## Rôle
Ce dashboard est l'outil central de supervision, de configuration et de contrôle de conformité pour l'administrateur GNS.

## Fonctionnalités & Gestion
1. **Tableau de bord (KPIs) :**
   - Affiche les volumes globaux, les commissions générées et les statistiques d'utilisation (étudiants, boutiques).
2. **Gestion des Utilisateurs :**
   - Création des comptes `ADMIN_BANQUE`.
   - Activation/Désactivation des comptes étudiants et commerçants.
3. **Paramétrage :**
   - Configuration des taux financiers (`TAUX_COMMISSION_PAIEMENT`, `PART_COMMISSION_GNS`, `FRAIS_CREATION_CARTE`).
4. **Audit (Transactions) :**
   - Consultation uniquement. Permet de vérifier les dépenses des étudiants en boutique en temps réel.

## Note importante : Suppression d'interface
La page "Versement" (qui gérait les paiements de manière globale et imprécise) est **supprimée**. Elle est remplacée par la séparation stricte entre les `Transactions` et les `Liquidations` pour une clarté comptable absolue.
