package com.backend.gns.student.domain.services;

import com.backend.gns.student.domain.models.InscriptionAnnuelle;

public interface InscriptionValidationService {
    
    /**
     * Vérifie dynamiquement si l'étudiant a fourni tous les documents requis 
     * pour son niveau d'étude et son profil d'inscription.
     * 
     * @param inscription L'inscription en cours de soumission/vérification
     * @throws com.backend.gns.student.domain.exceptions.MissingRequiredDocumentsException si des documents obligatoires manquent
     */
    void validateDocuments(InscriptionAnnuelle inscription);
    
    /**
     * Évalue si l'ajout d'un nouveau document permet de débloquer ou valider 
     * le dossier d'un étudiant.
     */
    void reevaluateDossierAfterUpload(java.util.UUID inscriptionId);
    
}
