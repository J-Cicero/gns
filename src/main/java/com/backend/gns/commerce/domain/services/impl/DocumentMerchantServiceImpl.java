package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.mappers.DocumentMerchantMapper;
import com.backend.gns.commerce.domain.models.DocumentMerchant;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.services.DocumentMerchantService;
import com.backend.gns.commerce.infrastructure.repositories.DocumentMerchantRepository;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.services.impl.CloudinaryStorageService;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.core.parametrage.domain.enums.KycStatus;
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
    private final BoutiqueRepository boutiqueRepository;

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

    private void reevaluateMerchantKyc(Merchant merchant) {
        java.util.List<DocumentMerchant> docs = documentRepository.findByMerchantTrackingId(merchant.getTrackingId());
        
        boolean hasValideRib = docs.stream()
                .anyMatch(d -> d.getDocumentType() == TypeDocument.RIB && d.getStatus() == StatutDocument.VALIDE);
                
        if (hasValideRib) {
            merchant.setKycStatus(KycStatus.VALIDE);
            // Validate and activate all boutiques of this merchant
            java.util.List<Boutique> boutiques = boutiqueRepository.findByMerchant(merchant);
            for (Boutique boutique : boutiques) {
                boutique.setKycStatus(KycStatus.VALIDE);
                if (boutique.getWallet() != null) {
                    boutique.getWallet().setStatus(com.backend.gns.wallet.domain.enums.WalletStatus.ACTIF);
                    // Reset quota to limitAmount if current balance is 0
                    if (boutique.getWallet().getBalance().compareTo(java.math.BigDecimal.ZERO) == 0) {
                        boutique.getWallet().setBalance(boutique.getWallet().getLimitAmount());
                    }
                }
                boutiqueRepository.save(boutique);
            }
        } else {
            boolean hasRejeteRib = docs.stream()
                    .anyMatch(d -> d.getDocumentType() == TypeDocument.RIB && d.getStatus() == StatutDocument.REJETE);
            if (hasRejeteRib) {
                merchant.setKycStatus(KycStatus.REJETE);
            } else {
                merchant.setKycStatus(KycStatus.EN_ATTENTE);
            }
            java.util.List<Boutique> boutiques = boutiqueRepository.findByMerchant(merchant);
            for (Boutique boutique : boutiques) {
                boutique.setKycStatus(KycStatus.EN_ATTENTE);
                if (boutique.getWallet() != null) {
                    boutique.getWallet().setStatus(com.backend.gns.wallet.domain.enums.WalletStatus.INACTIF);
                }
                boutiqueRepository.save(boutique);
            }
        }
        merchantRepository.save(merchant);
    }

    @Override
    @Transactional
    public DocumentResponse updateDocumentStatus(UUID trackingId, StatutDocument status, String rejectionReason) {
        DocumentMerchant document = documentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        document.setStatus(status);
        if (status == StatutDocument.REJETE) {
            document.setRejectionReason(rejectionReason);
        } else {
            document.setRejectionReason(null);
        }

        DocumentMerchant saved = documentRepository.save(document);
        log.info("Statut du document marchand {} mis à jour: {}", trackingId, status);

        if (saved.getMerchant() != null) {
            reevaluateMerchantKyc(saved.getMerchant());
        }

        return documentMerchantMapper.toResponse(saved);
    }
}