package com.backend.gns.student.domain.services;

import com.backend.gns.student.application.dtos.responses.InscriptionAnnuelleResponse;
import java.util.UUID;

/**
 * Orchestrateur pour le cycle de vie de l'étudiant.
 * Gère les transitions complexes entre inscription, éligibilité et activation du portefeuille.
 */
public interface StudentWorkflowService {

    /**
     * Valide une inscription annuelle et déclenche le calcul d'éligibilité à la bourse.
     * Si éligible, le portefeuille de l'étudiant est activé avec le plafond correspondant.
     *
     * @param inscriptionTrackingId L'ID de l'inscription à valider.
     * @return L'inscription mise à jour avec le résultat de l'éligibilité.
     */
    InscriptionAnnuelleResponse validerEtActiverInscription(UUID inscriptionTrackingId);
}
