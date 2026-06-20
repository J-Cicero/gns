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
        List<DocumentRequis> documentsRequis = documentRequisRepository.findByRequiredTrue();

        List<DocumentRequis> expectedRules = documentsRequis.stream()
                .filter(req -> req.getNiveauRequis() == null || req.getNiveauRequis() == inscription.getStudyLevel())
                .toList();

        List<DocumentEtudiant> documentsFournis = documentEtudiantRepository.findByInscription(inscription);

        Set<TypeDocument> typesFournis = documentsFournis.stream()
                .map(DocumentEtudiant::getDocumentType)
                .collect(Collectors.toSet());

        List<TypeDocument> missingDocuments = expectedRules.stream()
                .map(DocumentRequis::getTypeDocument)
                .filter(type -> !typesFournis.contains(type))
                .toList();

        if (!missingDocuments.isEmpty()) {
            throw new MissingRequiredDocumentsException(
                    "Dossier incomplet. Des documents obligatoires sont manquants.",
                    missingDocuments);
        }
    }

    @Override
    public void reevaluateDossierAfterUpload(UUID inscriptionId) {
        inscriptionAnnuelleRepository.findByTrackingId(inscriptionId).ifPresent(inscription -> {
            try {
                validateDocuments(inscription);
                log.info("Dossier complet pour l'inscription : {}", inscriptionId);
            } catch (MissingRequiredDocumentsException e) {
                log.info("Dossier toujours incomplet pour l'inscription : {}. Documents manquants : {}", inscriptionId, e.getMissingDocuments());
            }
        });
    }

}
