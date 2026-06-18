package com.backend.gns.core.parametrage.domain.services.impl;

import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse;
import com.backend.gns.core.parametrage.application.mappers.DocumentMapper;
import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.models.Document;
import com.backend.gns.core.parametrage.domain.services.DocumentService;
import com.backend.gns.core.parametrage.infrastructure.repositories.DocumentRepository;
import com.backend.gns.core.storage.CloudinaryStorageService; 
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
    private final CloudinaryStorageService cloudinaryStorageService; 

    @Override
    public DocumentResponse uploadDocument(MultipartFile file, UUID ownerTrackingId, ProprietaireType ownerType, TypeDocument documentType) {
        // 1. Upload file to Cloudinary
        String fileUrl;
        String publicId;
        try {
            var uploadResult = cloudinaryStorageService.upload(file, ownerTrackingId.toString());
            fileUrl = uploadResult.get("url").toString(); 
            publicId = uploadResult.get("publicId").toString(); 
        } catch (Exception e) {
            log.error("Failed to upload document to Cloudinary: {}", e.getMessage());
            throw new RuntimeException("Failed to upload document: " + e.getMessage());
        }

        // 2. Save document metadata to database
        Document document = Document.builder()
                .trackingId(UUID.randomUUID())
                .ownerTrackingId(ownerTrackingId)
                .ownerType(ownerType)
                .documentType(documentType)
                .fileUrl(fileUrl)
                .providerPublicId(publicId)
                .status(StatutDocument.EN_ATTENTE) 
                .uploadedAt(LocalDateTime.now())
                .build();

        documentRepository.save(document);
        log.info("Document {} uploaded successfully for owner {} (Type: {})", document.getTrackingId(), ownerTrackingId, documentType);
        return documentMapper.toResponse(document);
    }

    @Override
    public List<DocumentResponse> getDocumentsByOwner(UUID ownerTrackingId) {
        List<Document> documents = documentRepository.findByOwnerTrackingId(ownerTrackingId);
        return documents.stream()
                .map(documentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentResponse getDocumentByTrackingId(UUID trackingId) {
        Document document = documentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with trackingId: " + trackingId));
        return documentMapper.toResponse(document);
    }

    @Override
    public void deleteDocument(UUID trackingId) {
        Document document = documentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with trackingId: " + trackingId));

        // Delete from Cloudinary first
        try {
            cloudinaryStorageService.supprimer(document.getProviderPublicId());
            log.info("Document {} deleted from Cloudinary.", document.getProviderPublicId());
        } catch (Exception e) {
            log.error("Failed to delete document {} from Cloudinary: {}", document.getProviderPublicId(), e.getMessage());
            // Optionally, throw an exception or handle partial failure
        }

        documentRepository.delete(document);
        log.info("Document {} deleted from database.", trackingId);
    }
}
