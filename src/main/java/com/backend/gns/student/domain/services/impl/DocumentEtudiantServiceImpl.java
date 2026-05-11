package com.backend.gns.student.domain.services.impl;

import com.backend.gns.Shared.ai.GeminiExtractionService;
import com.backend.gns.Shared.ai.GeminiExtractionService.ExtractionResultat;
import com.backend.gns.Shared.storage.CloudinaryStorageService;
import com.backend.gns.student.application.dtos.requests.DocumentEtudiantRequest;
import com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.student.application.mappers.DocumentEtudiantMapper;
import com.backend.gns.student.domain.enums.StatutDocument;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.Shared.domain.enums.TypeDocument;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.services.DocumentEtudiantService;
import com.backend.gns.student.infrastructure.repositories.BanqueEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class DocumentEtudiantServiceImpl implements DocumentEtudiantService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final DocumentEtudiantRepository documentRepository;
  private final DocumentEtudiantMapper documentMapper;
  private final CloudinaryStorageService cloudinaryService;
  private final GeminiExtractionService geminiService;
  private final InscriptionAnnuelleRepository inscriptionRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public DocumentEtudiantServiceImpl(
      DocumentEtudiantRepository documentRepository,
      DocumentEtudiantMapper documentMapper,
      CloudinaryStorageService cloudinaryService,
      GeminiExtractionService geminiService,
      StudentRepository studentRepository,
      InscriptionAnnuelleRepository inscriptionRepository,
      BanqueEtudiantRepository banqueEtudiantRepository) {
    this.documentRepository = documentRepository;
    this.documentMapper = documentMapper;
    this.cloudinaryService = cloudinaryService;
    this.geminiService = geminiService;
    this.inscriptionRepository = inscriptionRepository;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public DocumentEtudiantResponse uploadDocument(
      MultipartFile fichier,
      UUID inscriptionTrackingId,
      TypeDocument typeDocument) {

    log.info("Début upload document - Student: {}, Type: {}", inscriptionTrackingId, typeDocument);

    
    InscriptionAnnuelle inscription = 
        inscriptionRepository
            .findByTrackingId(inscriptionTrackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("Étudiant non trouvé : " + inscriptionTrackingId));

     String urlFichier = cloudinaryService.upload(fichier, inscription.getStudent().getTrackingId().toString());
    log.info("Document uploadé sur Cloudinary : {}", urlFichier);

    // 4. Appel Gemini → extraction des données du document
    ExtractionResultat extraction = geminiService.extraire(urlFichier, typeDocument);
    log.info("Données extraites par Gemini : {}", extraction);

    // 5. Convertir l'extraction en JSON pour stockage
    String donneesJson = toJson(extraction);

    // 6. Créer et sauvegarder le DocumentEtudiant
    DocumentEtudiant document = new DocumentEtudiant();
    document.setTrackingId(UUID.randomUUID());
    document.setInscription(inscription);
    document.setType(typeDocument);
    document.setCheminFichier(urlFichier);
    document.setStatut(StatutDocument.EN_ATTENTE);
    document.setDateDepot(LocalDateTime.now());
    document.setDonneesExtraites(donneesJson);

    documentRepository.save(document);
    log.info("Document enregistré en BDD : {}", document.getTrackingId());

    // 7. Pré-remplir InscriptionAnnuelle avec les données extraites
    // L'admin validera ou corrigera ensuite
    if (inscription != null) {
        boolean inscriptionModifiee = false;

        if (extraction.niveau() != null) {
          try {
            inscription.setNiveau(StudentNiveau.valueOf(extraction.niveau()));
            inscriptionModifiee = true;
            log.info("Niveau mis à jour : {}", extraction.niveau());
          } catch (IllegalArgumentException ignored) {
            log.warn("Valeur niveau invalide retournée par Gemini : {}", extraction.niveau());
          }
        }
        if (extraction.creditsTotalValides() != null) {
          inscription.setCreditsTotalValides(extraction.creditsTotalValides());
          inscriptionModifiee = true;
          log.info("Credits mis à jour : {}", extraction.creditsTotalValides());
        }
        if (extraction.mentionBac() != null) {
          inscription.setMentionBac(extraction.mentionBac());
          inscriptionModifiee = true;
          log.info("Mention BAC mise à jour : {}", extraction.mentionBac());
        }
        if (inscriptionModifiee) {
          inscriptionRepository.save(inscription);
          log.info("Inscription mise à jour avec les données extraites");
        }
    }

    log.info("Upload document terminé avec succès");
    return documentMapper.toResponse(document);
  }


  //crud
  @Override
  @Transactional
  public DocumentEtudiantResponse create(DocumentEtudiantRequest request) {
    DocumentEtudiant document = documentMapper.toEntity(request);
    document.setDateDepot(LocalDateTime.now());
    document.setStatut(StatutDocument.EN_ATTENTE);
    DocumentEtudiant savedDocument = documentRepository.save(document);
    return documentMapper.toResponse(savedDocument);
  }

  @Override
  public Optional<DocumentEtudiantResponse> findByTrackingId(UUID trackingId) {
    return documentRepository.findByTrackingId(trackingId).map(documentMapper::toResponse);
  }

  @Override
  @Transactional
  public DocumentEtudiantResponse update(UUID trackingId, DocumentEtudiantRequest request) {
    DocumentEtudiant document =
        documentRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Document non trouvé avec trackingId: " + trackingId));

    document.setType(request.type());
    document.setCheminFichier(request.cheminFichier());
    document.setDonneesExtraites(request.donneesExtraites());

    DocumentEtudiant updatedDocument = documentRepository.save(document);
    return documentMapper.toResponse(updatedDocument);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    DocumentEtudiant document =
        documentRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Document non trouvé avec trackingId: " + trackingId));
    documentRepository.delete(document);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByStudentTrackingId(
      UUID studentTrackingId, Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return documentRepository
        .findByStudentTrackingId(studentTrackingId, normalized)
        .map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByInscriptionTrackingId(
      UUID inscriptionTrackingId, Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return documentRepository
        .findByInscriptionTrackingId(inscriptionTrackingId, normalized)
        .map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByStudentAndStatut(
      UUID studentTrackingId, StatutDocument statut, Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return documentRepository
        .findByStudentAndStatut(studentTrackingId, statut, normalized)
        .map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByStatut(StatutDocument statut, Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return documentRepository.findByStatut(statut, normalized).map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findAll(Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return documentRepository.findAll(normalized).map(documentMapper::toResponse);
  }

  
  private String toJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      log.error("Erreur conversion JSON : {}", e.getMessage());
      return "{}";
    }
  }
}
