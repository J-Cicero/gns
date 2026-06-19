package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.mappers.DocumentMerchantMapper;
import com.backend.gns.commerce.domain.models.DocumentMerchant;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.domain.services.DocumentMerchantService;
import com.backend.gns.commerce.infrastructure.repositories.DocumentMerchantRepository;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.services.impl.CloudinaryStorageService;
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
public class DocumentMerchantServiceImpl implements DocumentMerchantService {

    private final DocumentMerchantRepository documentRepository;
    private final MerchantRepository merchantRepository;
    private final DocumentMerchantMapper documentMerchantMapper; // Mapper dédié aux marchands
    private final CloudinaryStorageService cloudinaryService;

    @Override
    @Transactional
    public DocumentResponse uploadDocument(MultipartFile fichier, UUID merchantTrackingId, TypeDocument typeDocument) {

        Merchant merchant = merchantRepository.findByTrackingId(merchantTrackingId)
                .orElseThrow(() -> new RuntimeException("Marchand non trouvé"));

        Map<String, String> uploadResult = cloudinaryService.upload(fichier, merchantTrackingId.toString());

        // Création d'un DocumentMerchant
        DocumentMerchant document = DocumentMerchant.builder()
                .trackingId(UUID.randomUUID())
                .merchant(merchant)
                .documentType(typeDocument)
                .fileUrl(uploadResult.get("url"))
                .providerPublicId(uploadResult.get("publicId"))
                .status(StatutDocument.EN_ATTENTE)
                .uploadedAt(LocalDateTime.now())
                .build();

        document = documentRepository.save(document);
        log.info("Document marchand {} enregistré avec succès.", document.getTrackingId());

        return documentMerchantMapper.toResponse(document);
    }

    @Override
    public Optional<DocumentResponse> findByTrackingId(UUID trackingId) {
        return documentRepository.findByTrackingId(trackingId)
                .map(documentMerchantMapper::toResponse);
    }


    @Override
    public Page<DocumentResponse> findByMerchantTrackingId(UUID merchantTrackingId, Pageable pageable) {
        return documentRepository.findByMerchantTrackingId(merchantTrackingId, pageable)
                .map(documentMerchantMapper::toResponse);
    }

    @Override
    public void delete(UUID trackingId) {
        documentRepository.findByTrackingId(trackingId).ifPresent(doc -> {
            cloudinaryService.supprimer(doc.getProviderPublicId());
            documentRepository.delete(doc);
            log.info("Document marchand {} supprimé.", trackingId);
        });
    }

    @Override
    public java.util.List<DocumentResponse> getDocumentsByMerchant(UUID merchantTrackingId) {
        return documentRepository.findByMerchantTrackingId(merchantTrackingId)
                .stream()
                .map(documentMerchantMapper::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }
}