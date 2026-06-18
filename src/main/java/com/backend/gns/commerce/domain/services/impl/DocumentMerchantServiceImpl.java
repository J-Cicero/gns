package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.domain.models.DocumentMerchant;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.domain.services.DocumentMerchantService;
import com.backend.gns.commerce.infrastructure.repositories.DocumentMerchantRepository;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.storage.CloudinaryStorageService;
import com.backend.gns.student.application.dtos.responses.DocumentResponse;
import com.backend.gns.student.application.mappers.DocumentMapper;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import lombok.RequiredArgsConstructor;
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
public class DocumentMerchantServiceImpl implements DocumentMerchantService {

    private final DocumentMerchantRepository documentRepository;
    private final MerchantRepository merchantRepository;
    private final DocumentMapper documentMapper;
    private final CloudinaryStorageService cloudinaryService;

    @Override
    @Transactional
    public DocumentResponse uploadDocument(MultipartFile fichier, UUID merchantTrackingId, TypeDocument typeDocument) {
        Merchant merchant = merchantRepository.findByTrackingId(merchantTrackingId)
                .orElseThrow(() -> new RuntimeException("Marchand non trouvé"));

        Map<String, String> uploadResult = cloudinaryService.upload(fichier, merchantTrackingId.toString());

        DocumentMerchant document = new DocumentMerchant();
        document.setTrackingId(UUID.randomUUID());
        document.setMerchant(merchant);
        document.setOwnerTrackingId(merchantTrackingId);
        document.setOwnerType(com.backend.gns.core.parametrage.domain.enums.ProprietaireType.MERCHANT);
        document.setDocumentType(typeDocument);
        document.setFileUrl(uploadResult.get("url"));
        document.setProviderPublicId(uploadResult.get("publicId"));
        document.setStatus(StatutDocument.EN_ATTENTE);
        document.setUploadedAt(LocalDateTime.now());

        document = documentRepository.save(document);

        return documentMapper.toResponse(document);
    }

    @Override
    public Optional<DocumentResponse> findByTrackingId(UUID trackingId) {
        return documentRepository.findByTrackingId(trackingId).map(documentMapper::toResponse);
    }

    @Override
    public Page<DocumentResponse> findByMerchantTrackingId(UUID merchantTrackingId, Pageable pageable) {
        return Page.empty(); // À implémenter avec méthode repo
    }

    @Override
    public void delete(UUID trackingId) {
        documentRepository.findByTrackingId(trackingId).ifPresent(doc -> {
            cloudinaryService.supprimer(doc.getProviderPublicId());
            documentRepository.delete(doc);
        });
    }
}
