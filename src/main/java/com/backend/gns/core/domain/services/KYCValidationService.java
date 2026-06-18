package com.backend.gns.core.domain.services;

import com.backend.gns.commerce.infrastructure.repositories.DocumentMerchantRepository;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.domain.models.DocumentRequis;
import com.backend.gns.core.infrastructure.repositories.DocumentRequisRepository;
import com.backend.gns.student.domain.enums.TargetType;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KYCValidationService {

    private final DocumentRequisRepository documentRequisRepository;
    private final DocumentEtudiantRepository documentEtudiantRepository;
    private final DocumentMerchantRepository documentMerchantRepository;

    public List<TypeDocument> getMissingDocuments(UUID ownerTrackingId, TargetType target) {
        List<DocumentRequis> requiredDocs = documentRequisRepository.findByTargetTypeAndEstActifTrueAndObligatoireTrue(target);
        
        List<TypeDocument> requiredTypes = requiredDocs.stream()
                .map(DocumentRequis::getTypeDocument)
                .collect(Collectors.toList());

        List<TypeDocument> uploadedTypes;
        if (target == TargetType.STUDENT) {
            uploadedTypes = documentEtudiantRepository.findByOwnerTrackingId(ownerTrackingId).stream()
                    .map(doc -> doc.getDocumentType())
                    .collect(Collectors.toList());
        } else if (target == TargetType.MERCHANT) {
            uploadedTypes = documentMerchantRepository.findByMerchantTrackingId(ownerTrackingId).stream()
                    .map(doc -> doc.getDocumentType())
                    .collect(Collectors.toList());
        } else {
            uploadedTypes = List.of();
        }

        return requiredTypes.stream()
                .filter(type -> !uploadedTypes.contains(type))
                .collect(Collectors.toList());
    }

    public boolean isProfileComplete(UUID ownerTrackingId, TargetType target) {
        return getMissingDocuments(ownerTrackingId, target).isEmpty();
    }
}
