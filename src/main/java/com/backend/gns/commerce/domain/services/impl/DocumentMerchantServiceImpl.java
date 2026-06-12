package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.domain.models.DocumentMerchant;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.domain.services.DocumentMerchantService;
import com.backend.gns.commerce.infrastructure.repositories.DocumentMerchantRepository;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.core.storage.CloudinaryStorageService;
import com.backend.gns.student.application.dtos.responses.DocumentResponse;
import com.backend.gns.student.application.mappers.DocumentMapper;
import com.backend.gns.student.domain.enums.StatutDocument;
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

        DocumentMerchant document = DocumentMerchant.builder()
                .trackingId(UUID.randomUUID())
                .merchant(merchant)
                .type(typeDocument)
                .urlFichier(uploadResult.get("url"))
                .publicIdCloudinary(uploadResult.get("publicId"))
                .statut(StatutDocument.EN_ATTENTE)
                .dateDepot(LocalDateTime.now())
                .build();

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
            cloudinaryService.supprimer(doc.getPublicIdCloudinary());
            documentRepository.delete(doc);
        });
    }
}
