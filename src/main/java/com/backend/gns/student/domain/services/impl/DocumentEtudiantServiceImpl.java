package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.core.storage.CloudinaryStorageService;
import com.backend.gns.student.application.dtos.responses.DocumentResponse;
import com.backend.gns.student.application.mappers.DocumentMapper;
import com.backend.gns.student.domain.enums.StatutDocument;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.services.DocumentEtudiantService;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentEtudiantServiceImpl implements DocumentEtudiantService {

    private final DocumentEtudiantRepository documentRepository;
    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final DocumentMapper documentMapper;
    private final CloudinaryStorageService cloudinaryService;

    @Override
    @Transactional
    public DocumentResponse uploadDocument(MultipartFile fichier, UUID studentTrackingId, UUID inscriptionTrackingId, TypeDocument typeDocument) {
        
        InscriptionAnnuelle inscription = inscriptionRepository.findByTrackingId(inscriptionTrackingId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        Map<String, String> uploadResult = cloudinaryService.upload(fichier, studentTrackingId.toString());
        
        DocumentEtudiant document = DocumentEtudiant.builder()
                .trackingId(UUID.randomUUID())
                .ownerTrackingId(inscriptionTrackingId) // Corrected from .inscription(inscription)
                .documentType(typeDocument)
                .fileUrl(uploadResult.get("url"))
                .providerPublicId(uploadResult.get("publicId"))
                .status(StatutDocument.EN_ATTENTE)
                .uploadedAt(LocalDateTime.now())
                .build();
        
        return documentMapper.toResponse(documentRepository.save(document));
    }

    @Override
    public Optional<DocumentResponse> findByTrackingId(UUID trackingId) {
        return documentRepository.findByTrackingId(trackingId).map(documentMapper::toResponse);
    }

    @Override
    public Page<DocumentResponse> findByUserTrackingId(UUID userTrackingId, Pageable pageable) {
        // Logique à adapter pour chercher par student
        return Page.empty(); 
    }

    @Override
    public Page<DocumentResponse> findByInscriptionId(UUID inscriptionId, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public void delete(UUID trackingId) {
        documentRepository.findByTrackingId(trackingId).ifPresent(doc -> {
            cloudinaryService.supprimer(doc.getProviderPublicId());
            documentRepository.delete(doc);
        });
    }

    @Override
    public java.util.List<com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse> getDocumentsByStudent(UUID studentTrackingId) {
        return documentRepository.findByOwnerTrackingId(studentTrackingId).stream()
            .map(doc -> new com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse(
                doc.getDocumentType(),
                doc.getFileUrl(),
                doc.getStatus(),
                doc.getUploadedAt()
            ))
            .collect(java.util.stream.Collectors.toList());
    }
}
