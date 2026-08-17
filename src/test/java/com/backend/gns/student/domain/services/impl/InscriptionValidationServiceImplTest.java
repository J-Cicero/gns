package com.backend.gns.student.domain.services.impl;


import com.backend.gns.core.parametrage.infrastructure.repositories.DocumentRequisRepository;

import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    void setUp() {
        inscription = new InscriptionAnnuelle();
        inscription.setTrackingId(UUID.randomUUID());
    }

    @Test
    void validateDocuments_ShouldPassWithoutException() {
        assertDoesNotThrow(() -> inscriptionValidationService.validateDocuments(inscription),
                "La validation ne devrait pas lever d'exception car aucun document n'est rattaché à l'inscription annuelle.");
    }
}
