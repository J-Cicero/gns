# Documentation : Dashboard Banque

## Rôle
Ce portail est l'outil opérationnel de la Banque Partenaire pour la gestion de la trésorerie, le suivi des quotas et la validation des règlements financiers.

## Fonctionnalités & Gestion
1. **File d'attente des Liquidations (LiquidationQueue) :**
   - Liste les boutiques ayant atteint leur quota de vente.
   - Permet à la banque de saisir la référence du virement bancaire réel effectué vers la boutique.
2. **Audit des Transactions :**
   - Accès aux détails des transactions étudiants pour réconciliation financière si nécessaire.
3. **État des Portefeuilles :**
   - Vue sur l'ensemble des soldes des boutiques et universités (sous forme d'affichage).

## Note importante : Séparation des Flux
Ce dashboard ne gère pas les paiements au détail, mais uniquement le **règlement des soldes** (Liquidation). Les achats étudiants sont gérés par le flux `Transactions`.
