# BACKEND - Pistes d'amélioration et Refactoring

| Module | Classe/Service | Problème identifié | Action suggérée |
| :--- | :--- | :--- | :--- |
| Student | InscriptionAnnuelle | Manque de persistance pour les brouillons d'inscription (IA). | Ajouter statut BROUILLON et champs pour données extraites. |
| Student | InscriptionAnnuelle | Le montant scolarité proposé est volatil. | Ajouter `montantScolaritePropose` et `dateExpirationOffre` à l'entité. |
| Student | InscriptionAnnuelle | Pas de distinction claire entre documents bruts et données extraites. | Créer une entité/table de stockage pour les résultats d'extraction IA. |
| Student | Student | Filière peut changer par année scolaire | Déplacer `filiere` dans `InscriptionAnnuelle`. |
| Student | Card | Retourne une `Map<String, Object>` (typage faible) | Créer `CardResponse` et `CardRequest` DTOs. |
| Student | Student/Inscription | KYC dépend de l'année scolaire | Déplacer `statutKYC` de `Student` vers `InscriptionAnnuelle`. |
| Paiement | PretScolariteServiceImpl | Aucune contrainte sur le plafond de 30 000 FCFA. | Ajouter `if (montant > 30000)` dans `demanderPretScolarite`. |
| Paiement | PaiementServiceImpl | Logique de transaction trop couplée au statut de paiement. | Refactoriser pour valider le statut `VERIFIE_UL` avant traitement. |
