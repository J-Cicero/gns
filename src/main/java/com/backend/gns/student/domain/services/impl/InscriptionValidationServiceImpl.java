package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.models.DocumentRequis;
import com.backend.gns.core.parametrage.infrastructure.repositories.DocumentRequisRepository;
import com.backend.gns.student.domain.exceptions.MissingRequiredDocumentsException;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.services.InscriptionValidationService;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InscriptionValidationServiceImpl implements InscriptionValidationService {

    private final DocumentRequisRepository documentRequisRepository;
    private final DocumentEtudiantRepository documentEtudiantRepository;
    private final InscriptionAnnuelleRepository inscriptionAnnuelleRepository;

    @Override
    public void validateDocuments(InscriptionAnnuelle inscription) {
        // Aucune exigence de document pour l'inscription annuelle.
        // Les documents (RIB, Mandat) sont rattachés uniquement au profil de l'étudiant à la création de compte.
    }

    @Override
    public void reevaluateDossierAfterUpload(UUID inscriptionId) {
        log.info("Évaluation automatique ignorée : les documents sont gérés au niveau du compte étudiant.");
    }
}
