package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.services.impl.CloudinaryStorageService;
import com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.student.application.mappers.DocumentEtudiantMapper;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.DocumentEtudiantService;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;

import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.wallet.domain.enums.WalletStatus;
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
    private final com.backend.gns.core.parametrage.infrastructure.repositories.DocumentBanqueRepository documentBanqueRepository;
    private final com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository inscriptionRepository;
    private final StudentRepository studentRepository;
    private final DocumentEtudiantMapper documentMapper;
    private final CloudinaryStorageService cloudinaryService;


    @Override
    @Transactional
    public DocumentEtudiantResponse uploadDocument(MultipartFile fichier, UUID studentTrackingId, UUID inscriptionTrackingId, TypeDocument typeDocument) {

        Student student = studentRepository.findByTrackingId(studentTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        documentRepository.findByStudentTrackingId(studentTrackingId).stream()
                .filter(d -> d.getDocumentType() == typeDocument)
                .forEach(d -> {
                    cloudinaryService.supprimer(d.getProviderPublicId());
                    documentRepository.delete(d);
                });

        Map<String, String> uploadResult = cloudinaryService.upload(fichier, studentTrackingId.toString());

        DocumentEtudiant document = DocumentEtudiant.builder()
                .trackingId(UUID.randomUUID())
                .documentType(typeDocument)
                .fileUrl(uploadResult.get("url"))
                .providerPublicId(uploadResult.get("publicId"))
                .status(StatutDocument.EN_ATTENTE)
                .uploadedAt(LocalDateTime.now())
                .student(student)
                .build();

        DocumentEtudiant savedDocument = documentRepository.save(document);

        return documentMapper.toEtudiantResponse(savedDocument);
    }

    @Override
    public Optional<DocumentEtudiantResponse> findByTrackingId(UUID trackingId) {
        return documentRepository.findByTrackingId(trackingId)
                .map(documentMapper::toEtudiantResponse);
    }

    @Override
    public Page<DocumentEtudiantResponse> findByUserTrackingId(UUID userTrackingId, Pageable pageable) {
        List<DocumentEtudiantResponse> allDocs = getDocumentsByStudent(userTrackingId);
        return new org.springframework.data.domain.PageImpl<>(allDocs, pageable, allDocs.size());
    }

    @Override
    public Page<DocumentEtudiantResponse> findByInscriptionId(UUID inscriptionId, Pageable pageable) {
        return inscriptionRepository.findByTrackingId(inscriptionId)
                .filter(ins -> ins.getStudent() != null)
                .map(ins -> {
                    List<DocumentEtudiantResponse> allDocs = getDocumentsByStudent(ins.getStudent().getTrackingId());
                    return (Page<DocumentEtudiantResponse>) new org.springframework.data.domain.PageImpl<>(allDocs, pageable, allDocs.size());
                })
                .orElseGet(Page::empty);
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
        java.util.Map<String, DocumentEtudiantResponse> map = new java.util.LinkedHashMap<>();

        documentRepository.findByStudentTrackingId(studentTrackingId).stream()
                .map(documentMapper::toEtudiantResponse)
                .forEach(r -> {
                    if (r.fileUrl() != null) {
                        map.putIfAbsent(r.fileUrl(), r);
                    }
                });

        documentBanqueRepository.findByCompteBancaireProprietaireTrackingIdOrderByUploadedAtDesc(studentTrackingId)
                .forEach(db -> {
                    if (db.getFileUrl() != null) {
                        map.putIfAbsent(db.getFileUrl(), new DocumentEtudiantResponse(
                                db.getTrackingId(),
                                db.getDocumentType(),
                                db.getFileUrl(),
                                db.getStatus(),
                                db.getUploadedAt(),
                                db.getRejectionReason(),
                                null,
                                "ETUDIANT"
                        ));
                    }
                });

        return new java.util.ArrayList<>(map.values());
    }

    private void reevaluateStudentKyc(Student student) {
        List<DocumentEtudiant> docs = documentRepository.findByStudentTrackingId(student.getTrackingId());
        List<com.backend.gns.core.parametrage.domain.models.DocumentBanque> bankDocs =
                documentBanqueRepository.findByCompteBancaireProprietaireTrackingIdOrderByUploadedAtDesc(student.getTrackingId());
        
        boolean hasValideRib = docs.stream()
                .anyMatch(d -> d.getDocumentType() == TypeDocument.RIB && d.getStatus() == StatutDocument.VALIDE)
                || bankDocs.stream()
                .anyMatch(d -> d.getDocumentType() == TypeDocument.RIB && d.getStatus() == StatutDocument.VALIDE);
                
        boolean hasValideMandat = docs.stream()
                .anyMatch(d -> d.getDocumentType() == TypeDocument.MANDAT && d.getStatus() == StatutDocument.VALIDE)
                || bankDocs.stream()
                .anyMatch(d -> d.getDocumentType() == TypeDocument.MANDAT && d.getStatus() == StatutDocument.VALIDE);
                        
        if (hasValideRib && hasValideMandat) {
            student.setKycStatus(KycStatus.VALIDE);
            if (student.getWallet() != null) {
                student.getWallet().setStatus(WalletStatus.ACTIF);
            }
        } else {
            boolean hasRejeteCritical = docs.stream()
                    .anyMatch(d -> (d.getDocumentType() == TypeDocument.RIB || d.getDocumentType() == TypeDocument.MANDAT) 
                            && d.getStatus() == StatutDocument.REJETE)
                    || bankDocs.stream()
                    .anyMatch(d -> (d.getDocumentType() == TypeDocument.RIB || d.getDocumentType() == TypeDocument.MANDAT)
                            && d.getStatus() == StatutDocument.REJETE);
            if (hasRejeteCritical) {
                student.setKycStatus(KycStatus.REJETE);
            } else {
                student.setKycStatus(KycStatus.EN_ATTENTE);
            }
            if (student.getWallet() != null) {
                student.getWallet().setStatus(WalletStatus.INACTIF);
            }
        }
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public DocumentEtudiantResponse updateDocumentStatus(UUID trackingId, StatutDocument status, String rejectionReason) {
        Optional<DocumentEtudiant> optEtudiantDoc = documentRepository.findByTrackingId(trackingId);
        if (optEtudiantDoc.isPresent()) {
            DocumentEtudiant document = optEtudiantDoc.get();
            document.setStatus(status);
            if (status == StatutDocument.REJETE) {
                document.setRejectionReason(rejectionReason);
            } else {
                document.setRejectionReason(null);
            }

            DocumentEtudiant saved = documentRepository.save(document);

            if (saved.getStudent() != null) {
                reevaluateStudentKyc(saved.getStudent());
            }

            return documentMapper.toEtudiantResponse(saved);
        }

        com.backend.gns.core.parametrage.domain.models.DocumentBanque bankDoc = documentBanqueRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));

        bankDoc.setStatus(status);
        if (status == StatutDocument.REJETE) {
            bankDoc.setRejectionReason(rejectionReason);
        } else {
            bankDoc.setRejectionReason(null);
        }
        com.backend.gns.core.parametrage.domain.models.DocumentBanque savedBankDoc = documentBanqueRepository.save(bankDoc);

        if (savedBankDoc.getCompteBancaire() != null && savedBankDoc.getCompteBancaire().getProprietaire() instanceof Student student) {
            reevaluateStudentKyc(student);
        }

        return new DocumentEtudiantResponse(
                savedBankDoc.getTrackingId(),
                savedBankDoc.getDocumentType(),
                savedBankDoc.getFileUrl(),
                savedBankDoc.getStatus(),
                savedBankDoc.getUploadedAt(),
                savedBankDoc.getRejectionReason(),
                null,
                "ETUDIANT"
        );
    }
}