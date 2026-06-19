package com.backend.gns.core.parametrage.domain.services.impl;

import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse;
import com.backend.gns.core.parametrage.application.mappers.DocumentMapper;
import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.models.Document;
import com.backend.gns.core.parametrage.domain.models.DocumentBanque;
import com.backend.gns.core.parametrage.domain.services.DocumentService;
import com.backend.gns.core.parametrage.infrastructure.repositories.DocumentRepository;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final StudentRepository studentRepository;
    private final CloudinaryStorageService cloudinaryStorageService;

    @Override
    public DocumentResponse uploadDocument(MultipartFile file, UUID ownerTrackingId, ProprietaireType ownerType, TypeDocument documentType) {
        // 1. Upload sur Cloudinary
        var uploadResult = cloudinaryStorageService.upload(file, "doc_" + UUID.randomUUID());
        String fileUrl = uploadResult.get("url").toString();
        String publicId = uploadResult.get("publicId").toString();

        Document document;

        if (ownerType == ProprietaireType.STUDENT) {
            Student student  = studentRepository.findByTrackingId(ownerTrackingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé: " + ownerTrackingId));
            document = DocumentEtudiant.builder()
                    .student(student)
                    .build();
        } else {
            document = DocumentBanque.builder()
                    .build();
        }

        // 3. Remplissage des champs communs (Hérités de Document)
        document.setTrackingId(UUID.randomUUID());
        document.setDocumentType(documentType);
        document.setFileUrl(fileUrl);
        document.setProviderPublicId(publicId);
        document.setStatus(StatutDocument.EN_ATTENTE);
        document.setUploadedAt(LocalDateTime.now());

        documentRepository.save(document);
        log.info("Document {} enregistré avec succès pour {}", document.getTrackingId(), ownerType);

        return documentMapper.toResponse(document);
    }

    @Override
    public List<DocumentResponse> getDocumentsByOwner(UUID ownerTrackingId) {
        return documentRepository.findByOwnerTrackingId(ownerTrackingId).stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentResponse getDocumentByTrackingId(UUID trackingId) {
        Document document = documentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé: " + trackingId));
        return documentMapper.toResponse(document);
    }

    @Override
    public void deleteDocument(UUID trackingId) {
        Document document = documentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé: " + trackingId));

        cloudinaryStorageService.supprimer(document.getProviderPublicId());
        documentRepository.delete(document);
        log.info("Document {} supprimé.", trackingId);
    }
}