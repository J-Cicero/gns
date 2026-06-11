package com.backend.gns.student.domain.services;

import com.backend.gns.student.domain.models.InscriptionAnnuelle;

/**
 * Service dédié à l'intégration avec le système d'information externe de l'Université.
 */
public interface InscriptionExterneService {

    /**
     * Appelle l'API externe pour vérifier le statut d'inscription et l'éligibilité de l'étudiant.
     * Met à jour l'entité InscriptionAnnuelle avec les données reçues.
     */
    InscriptionAnnuelle synchroniserStatutInscription(InscriptionAnnuelle inscriptionAnnuelle);
}
