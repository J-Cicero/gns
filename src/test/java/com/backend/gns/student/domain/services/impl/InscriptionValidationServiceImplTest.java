package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.models.DocumentRequis;
import com.backend.gns.core.parametrage.infrastructure.repositories.DocumentRequisRepository;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.exceptions.MissingRequiredDocumentsException;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscriptionValidationServiceImplTest {

    @Mock
    private DocumentRequisRepository documentRequisRepository;

    @Mock
    private DocumentEtudiantRepository documentEtudiantRepository;

    @Mock
    private InscriptionAnnuelleRepository inscriptionAnnuelleRepository;

    @InjectMocks
    private InscriptionValidationServiceImpl inscriptionValidationService;

    private InscriptionAnnuelle inscription;
    private DocumentRequis reqPieceIdentite;
    private DocumentRequis reqReleveNotes;

    @BeforeEach
    void setUp() {
        inscription = new InscriptionAnnuelle();
        inscription.setTrackingId(UUID.randomUUID());
        inscription.setStudyLevel(StudentNiveau.L1_ANNEE);

        reqPieceIdentite = new DocumentRequis();
        reqPieceIdentite.setTypeDocument(TypeDocument.PIECE_IDENTITE);
        reqPieceIdentite.setRequired(true);
        reqPieceIdentite.setNiveauRequis(null); // Applicable à tous

        reqReleveNotes = new DocumentRequis();
        reqReleveNotes.setTypeDocument(TypeDocument.RELEVE_NOTES);
        reqReleveNotes.setRequired(true);
        reqReleveNotes.setNiveauRequis(StudentNiveau.L1_ANNEE); // Spécifique L1
    }

    @Test
    void validateDocuments_WhenDossierComplet_ShouldPassWithoutException() {
        // Arrange
        when(documentRequisRepository.findByRequiredTrue()).thenReturn(List.of(reqPieceIdentite, reqReleveNotes));

        DocumentEtudiant doc1 = new DocumentEtudiant();
        doc1.setDocumentType(TypeDocument.PIECE_IDENTITE);

        DocumentEtudiant doc2 = new DocumentEtudiant();
        doc2.setDocumentType(TypeDocument.RELEVE_NOTES);

        when(documentEtudiantRepository.findByInscription(inscription)).thenReturn(List.of(doc1, doc2));

        // Act & Assert
        assertDoesNotThrow(() -> inscriptionValidationService.validateDocuments(inscription),
                "La validation ne devrait pas lever d'exception pour un dossier complet");
    }

    @Test
    void validateDocuments_WhenDossierIncomplet_ShouldThrowExceptionWithMissingDocuments() {
        // Arrange
        when(documentRequisRepository.findByRequiredTrue()).thenReturn(List.of(reqPieceIdentite, reqReleveNotes));

        DocumentEtudiant doc1 = new DocumentEtudiant();
        doc1.setDocumentType(TypeDocument.PIECE_IDENTITE);
        // Le relevé de notes manque

        when(documentEtudiantRepository.findByInscription(inscription)).thenReturn(List.of(doc1));

        // Act & Assert
        MissingRequiredDocumentsException exception = assertThrows(MissingRequiredDocumentsException.class,
                () -> inscriptionValidationService.validateDocuments(inscription));

        assertEquals("Dossier incomplet. Des documents obligatoires sont manquants.", exception.getMessage());
        assertTrue(exception.getMissingDocuments().contains(TypeDocument.RELEVE_NOTES));
        assertFalse(exception.getMissingDocuments().contains(TypeDocument.PIECE_IDENTITE));
    }
}
