# PROJET STUDCASH - Documentation Architecturale

## Description Générale
StudCash est une plateforme FinTech visant à sécuriser et optimiser la distribution des bourses d'études et la gestion des frais scolaires. Elle comble le fossé entre les délais de versement de la DBS (Direction des Bourses) et les besoins immédiats des étudiants, tout en garantissant un flux de trésorerie aux partenaires (universités, commerçants).

## Missions
1. **Disponibilité immédiate** : Permettre aux étudiants d'accéder à leurs fonds dès leur inscription.
2. **Financement des besoins** : Autoriser le paiement direct des frais de scolarité et l'achat de produits de première nécessité.
3. **Économie circulaire** : Offrir aux commerçants partenaires une clientèle garantie, fidèle et solvable.

## Acteurs et Rôles
* **Étudiant** : Bénéficiaire de la bourse, gère son wallet, effectue des paiements (scolarité/boutiques).
* **Commerçant** : Partenaire acceptant les paiements via StudCash, bénéficiaire de flux garantis.
* **Université** : Source de vérité pour les inscriptions (Validation initiale).
* **Admin GNS** : Administrateur système, gestionnaire des comptes, vérificateur.

## Flux de travail (Workflows)
1. **Inscription** : 
   - L'Université notifie StudCash de l'inscription effective d'un étudiant.
   - StudCash génère ou met à jour une `InscriptionAnnuelle` (pré-validée par l'Université).
   - L'étudiant complète son dossier (uploads documents) et confirme ses informations.
   - Validation finale de l'éligibilité (standard DBS, crédits validés) et calcul du reste à payer.
   - L'offre de paiement est proposée à l'étudiant pour acceptation.
2. **Réception Bourse** : Les fonds (tranches de bourses) sont injectés sur le wallet de l'étudiant tous les 3 mois.
3. **Consommation** :
   - Paiement Scolarité (Direct à l'Université).
   - Achats (Boutiques partenaires).
4. **Clôture** : Admin GNS gère les fins d'année scolaire et l'archivage.
