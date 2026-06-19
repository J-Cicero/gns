package com.backend.gns.core.parametrage.domain.services.impl;

import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.application.dtos.requests.CompteBancaireRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.CompteBancaireResponse;
import com.backend.gns.core.parametrage.application.mappers.CompteBancaireMapper;
import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.models.CompteBancaire;
import com.backend.gns.core.parametrage.domain.models.DocumentBanque;
import com.backend.gns.core.parametrage.domain.services.CompteBancaireService;
import com.backend.gns.core.parametrage.infrastructure.repositories.CompteBancaireRepository;
import com.backend.gns.core.parametrage.infrastructure.repositories.DocumentBanqueRepository;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.user.domain.models.User;
import com.backend.gns.user.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompteBancaireServiceImpl implements CompteBancaireService {

    private final CompteBancaireRepository compteBancaireRepository;
    private final CompteBancaireMapper compteBancaireMapper;
    private final UserRepository userRepository;
    private final DocumentBanqueRepository documentBanqueRepository;
    private final CloudinaryStorageService storageService;

    /**
     * ÉTAPE 1 : Création du compte pur (Sans fichier)
     */
    @Override
    @Transactional
    public CompteBancaireResponse createAccount(UUID ownerTrackingId, CompteBancaireRequest request) {
        log.info("Création d'un compte bancaire pour l'utilisateur: {}", ownerTrackingId);

        User proprietaire = userRepository.findByTrackingId(ownerTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + ownerTrackingId));

        CompteBancaire compte = compteBancaireMapper.toEntity(request);

        compte.setProprietaire(proprietaire);
        ProprietaireType ownerType = switch (proprietaire.getRole()) {
            case ETUDIANT -> ProprietaireType.STUDENT;
            case COMMERCANT -> ProprietaireType.MERCHANT;
            case ADMIN_GNS -> ProprietaireType.GNS;
            case ADMIN_BANQUE -> ProprietaireType.BANQUE;
            default -> throw new IllegalArgumentException("Rôle utilisateur non autorisé pour un compte bancaire: " + proprietaire.getRole());
        };
        compte.setOwnerType(ownerType);
        compte.setMainScholarshipAccount(ownerType == ProprietaireType.STUDENT);

        CompteBancaire savedCompte = compteBancaireRepository.save(compte);

        return compteBancaireMapper.toResponse(savedCompte, null);
    }

    /**
     * ÉTAPE 2 : Upload du RIB et création du DocumentBanque
     */
    @Override
    @Transactional
    public CompteBancaireResponse uploadRib(UUID compteTrackingId, MultipartFile file) {
        log.info("Upload du RIB pour le compte: {}", compteTrackingId);

        CompteBancaire compte = compteBancaireRepository.findByTrackingId(compteTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte bancaire non trouvé avec l'ID: " + compteTrackingId));

        // 1. Upload sur Cloudinary
        var uploadResult = storageService.upload(file, "rib_" + compte.getTrackingId());

        // 2. Création de l'entité DocumentBanque (Type RIB)
        DocumentBanque ribDoc = new DocumentBanque();
        ribDoc.setTrackingId(UUID.randomUUID());
        ribDoc.setDocumentType(TypeDocument.RIB);
        ribDoc.setFileUrl((String) uploadResult.get("url"));
        ribDoc.setProviderPublicId((String) uploadResult.get("publicId"));
        ribDoc.setStatus(StatutDocument.EN_ATTENTE);
        ribDoc.setUploadedAt(LocalDateTime.now());
        ribDoc.setCompteBancaire(compte);

        documentBanqueRepository.save(ribDoc);

        // On retourne le compte mis à jour AVEC les infos du RIB !
        return compteBancaireMapper.toResponse(compte, ribDoc);
    }

    /**
     * ÉTAPE 3 : Upload du Mandat de prélèvement DBS (Uniquement pour les étudiants)
     */
    @Override
    @Transactional
    public void uploadMandat(UUID compteTrackingId, MultipartFile file) {
        log.info("Upload du Mandat pour le compte: {}", compteTrackingId);

        CompteBancaire compte = compteBancaireRepository.findByTrackingId(compteTrackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte bancaire non trouvé avec l'ID: " + compteTrackingId));

        // Sécurité métier : Un commerçant (Merchant) n'a pas de Mandat DBS !
        if (compte.getOwnerType() != ProprietaireType.STUDENT) {
            throw new IllegalArgumentException("Seul un compte étudiant peut être lié à un mandat de prélèvement DBS.");
        }

        // 1. Upload sur Cloudinary
        var uploadResult = storageService.upload(file, "mandat_" + compte.getTrackingId());

        // 2. Création de l'entité DocumentBanque (Type MANDAT)
        DocumentBanque mandatDoc = new DocumentBanque();
        mandatDoc.setTrackingId(UUID.randomUUID());
        mandatDoc.setDocumentType(TypeDocument.MANDAT);
        mandatDoc.setFileUrl((String) uploadResult.get("url"));
        mandatDoc.setProviderPublicId((String) uploadResult.get("publicId"));
        mandatDoc.setStatus(StatutDocument.EN_ATTENTE);
        mandatDoc.setUploadedAt(LocalDateTime.now());
        mandatDoc.setCompteBancaire(compte);

        documentBanqueRepository.save(mandatDoc);


    }

    @Override
    @Transactional(readOnly = true)
    public List<CompteBancaireResponse> findAll() {
        return compteBancaireRepository.findAll().stream()
                .map(compte -> {
                    DocumentBanque ribDoc = documentBanqueRepository
                            .findByCompteBancaireTrackingIdAndDocumentType(compte.getTrackingId(), TypeDocument.RIB)
                            .orElse(null);
                    return compteBancaireMapper.toResponse(compte, ribDoc);
                })
                .toList();
    }

    /**
     * Récupère le compte bancaire spécifique à un utilisateur
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CompteBancaireResponse> findByOwnerTrackingId(UUID ownerTrackingId) {
        return compteBancaireRepository.findByProprietaireTrackingId(ownerTrackingId)
                .map(compte -> {
                    // On récupère le RIB pour l'envoyer au Frontend
                    DocumentBanque ribDoc = documentBanqueRepository
                            .findByCompteBancaireTrackingIdAndDocumentType(compte.getTrackingId(), TypeDocument.RIB)
                            .orElse(null);
                    return compteBancaireMapper.toResponse(compte, ribDoc);
                });
    }

    /**
     * Supprime un compte bancaire
     */
    @Override
    @Transactional
    public void delete(UUID trackingId) {
        log.info("Suppression du compte bancaire: {}", trackingId);
        CompteBancaire compte = compteBancaireRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte bancaire non trouvé avec l'ID: " + trackingId));

        compteBancaireRepository.delete(compte);
        // Note: Grâce au CascadeType (s'il est configuré) ou via une tâche de nettoyage,
        // les DocumentBanque liés seront aussi supprimés.
    }
}