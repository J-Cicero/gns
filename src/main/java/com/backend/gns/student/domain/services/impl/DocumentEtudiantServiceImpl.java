package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.services.impl.CloudinaryStorageService;
import com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.student.application.mappers.DocumentEtudiantMapper;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.DocumentEtudiantService;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.student.domain.services.InscriptionValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentEtudiantServiceImpl implements DocumentEtudiantService {

    private final DocumentEtudiantRepository documentRepository;
    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final StudentRepository studentRepository;
    private final DocumentEtudiantMapper documentMapper;
    private final CloudinaryStorageService cloudinaryService;
    private final InscriptionValidationService inscriptionValidationService;

    @Override
    @Transactional
    public DocumentEtudiantResponse uploadDocument(MultipartFile fichier, UUID studentTrackingId, UUID inscriptionTrackingId, TypeDocument typeDocument) {

        Student student = studentRepository.findByTrackingId(studentTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        InscriptionAnnuelle inscription = null;
        if (inscriptionTrackingId != null) {
            inscription = inscriptionRepository.findByTrackingId(inscriptionTrackingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Inscription non trouvée"));
        }

        if (inscription != null) {
            documentRepository.findByInscriptionTrackingId(inscriptionTrackingId, Pageable.unpaged()).stream()
                    .filter(d -> d.getDocumentType() == typeDocument)
                    .forEach(d -> {
                        cloudinaryService.supprimer(d.getProviderPublicId());
                        documentRepository.delete(d);
                    });
        } else {
            documentRepository.findByStudentTrackingId(studentTrackingId).stream()
                    .filter(d -> d.getDocumentType() == typeDocument && d.getInscription() == null)
                    .forEach(d -> {
                        cloudinaryService.supprimer(d.getProviderPublicId());
                        documentRepository.delete(d);
                    });
        }

        Map<String, String> uploadResult = cloudinaryService.upload(fichier, studentTrackingId.toString());

        DocumentEtudiant document = DocumentEtudiant.builder()
                .trackingId(UUID.randomUUID())
                .documentType(typeDocument)
                .fileUrl(uploadResult.get("url"))
                .providerPublicId(uploadResult.get("publicId"))
                .status(StatutDocument.EN_ATTENTE)
                .uploadedAt(LocalDateTime.now())
                .student(student)
                .inscription(inscription)
                .build();

        DocumentEtudiant savedDocument = documentRepository.save(document);

        // Réévaluer le dossier après upload
        if (inscriptionTrackingId != null) {
            inscriptionValidationService.reevaluateDossierAfterUpload(inscriptionTrackingId);
        }

        return documentMapper.toEtudiantResponse(savedDocument);
    }

    @Override
    public Optional<DocumentEtudiantResponse> findByTrackingId(UUID trackingId) {
        return documentRepository.findByTrackingId(trackingId)
                .map(documentMapper::toEtudiantResponse);
    }

    @Override
    public Page<DocumentEtudiantResponse> findByUserTrackingId(UUID userTrackingId, Pageable pageable) {
        return documentRepository.findByStudentTrackingId(userTrackingId, pageable)
                .map(documentMapper::toEtudiantResponse);
    }

    @Override
    public Page<DocumentEtudiantResponse> findByInscriptionId(UUID inscriptionId, Pageable pageable) {
        return documentRepository.findByInscriptionTrackingId(inscriptionId, pageable)
                .map(documentMapper::toEtudiantResponse);
    }

    @Override
    public void delete(UUID trackingId) {
        documentRepository.findByTrackingId(trackingId).ifPresent(doc -> {
            cloudinaryService.supprimer(doc.getProviderPublicId());
            documentRepository.delete(doc);
        });
    }

    @Override
    public List<DocumentEtudiantResponse> getDocumentsByStudent(UUID studentTrackingId) {
        return documentRepository.findByStudentTrackingId(studentTrackingId).stream()
                .map(documentMapper::toEtudiantResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DocumentEtudiantResponse updateDocumentStatus(UUID trackingId, StatutDocument status, String rejectionReason) {
        DocumentEtudiant document = documentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));

        document.setStatus(status);
        if (status == StatutDocument.REJETE) {
            document.setRejectionReason(rejectionReason);
        } else {
            document.setRejectionReason(null);
        }

        DocumentEtudiant saved = documentRepository.save(document);
        
        // Si c'est un document d'inscription annuelle, on réévalue
        if (saved.getInscription() != null) {
            inscriptionValidationService.reevaluateDossierAfterUpload(saved.getInscription().getTrackingId());
        }

        return documentMapper.toEtudiantResponse(saved);
    }
}