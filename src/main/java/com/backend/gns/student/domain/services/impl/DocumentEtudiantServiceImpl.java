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
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.services.DocumentEtudiantService;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Map;
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
  private final StudentRepository studentRepository;
  private final InscriptionAnnuelleRepository inscriptionRepository;
  private final ScolariteYearRepository scolariteYearRepository;
  private final ObjectMapper objectMapper;

  public DocumentEtudiantServiceImpl(
      DocumentEtudiantRepository documentRepository,
      DocumentEtudiantMapper documentMapper,
      CloudinaryStorageService cloudinaryService,
      GeminiExtractionService geminiService,
      StudentRepository studentRepository,
      InscriptionAnnuelleRepository inscriptionRepository,
      ScolariteYearRepository scolariteYearRepository,
      ObjectMapper objectMapper) {
    this.documentRepository = documentRepository;
    this.documentMapper = documentMapper;
    this.cloudinaryService = cloudinaryService;
    this.geminiService = geminiService;
    this.studentRepository = studentRepository;
    this.inscriptionRepository = inscriptionRepository;
    this.scolariteYearRepository = scolariteYearRepository;
    this.objectMapper = objectMapper;
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

    log.info("Début upload document - Inscription: {}, Type: {}", inscriptionTrackingId, typeDocument);

    InscriptionAnnuelle inscription = inscriptionRepository
            .findByTrackingId(inscriptionTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Inscription non trouvée"));

    Student student = inscription.getStudent();

    Map<String, String> uploadResult = cloudinaryService.upload(fichier, student.getTrackingId().toString());
    String urlFichier = uploadResult.get("url");
    String publicId = uploadResult.get("publicId");

    ExtractionResultat extraction = geminiService.extraire(urlFichier, typeDocument);
    log.info("Données extraites : {}", extraction);

    DocumentEtudiant document = new DocumentEtudiant();
    document.setTrackingId(UUID.randomUUID());
    document.setStudent(student);
    document.setInscription(inscription);
    document.setType(typeDocument);
    document.setUrlFichier(urlFichier);
    document.setPublicIdCloudinary(publicId);
    document.setStatut(StatutDocument.EN_ATTENTE);
    document.setDateDepot(LocalDateTime.now());
    document.setScoreFiabilite(extraction.scoreFiabilite());
    
    try {
        document.setDonneesExtraites(objectMapper.writeValueAsString(extraction));
    } catch (Exception e) {
        log.error("Erreur conversion JSON : {}", e.getMessage());
    }

    DocumentEtudiant saved = documentRepository.save(document);

    // Mise à jour automatique de l'inscription
    updateInscriptionFromExtraction(inscription, extraction);

    return documentMapper.toResponse(saved);
  }

  private void updateInscriptionFromExtraction(InscriptionAnnuelle ins, ExtractionResultat extraction) {
    boolean modifie = false;
    if (extraction.creditsTotalValides() != null) {
        ins.setCreditsTotalValides(extraction.creditsTotalValides());
        modifie = true;
    }
    if (extraction.moyenneBac() != null) {
        ins.setMoyenneBac(extraction.moyenneBac());
        modifie = true;
    }
    if (modifie) {
        inscriptionRepository.save(ins);
        log.info("Inscription {} mise à jour via OCR", ins.getTrackingId());
    }
  }

  @Override
  @Transactional
  public DocumentEtudiantResponse create(DocumentEtudiantRequest request) {
    DocumentEtudiant document = documentMapper.toEntity(request);
    document.setDateDepot(LocalDateTime.now());
    document.setStatut(StatutDocument.EN_ATTENTE);
    return documentMapper.toResponse(documentRepository.save(document));
  }

  @Override
  public Optional<DocumentEtudiantResponse> findByTrackingId(UUID trackingId) {
    return documentRepository.findByTrackingId(trackingId).map(documentMapper::toResponse);
  }

  @Override
  @Transactional
  public DocumentEtudiantResponse update(UUID trackingId, DocumentEtudiantRequest request) {
    DocumentEtudiant document = documentRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Document non trouvé"));

    document.setType(request.type());
    document.setUrlFichier(request.cheminFichier()); // Assuming cheminFichier is the URL in DTO
    document.setDonneesExtraites(request.donneesExtraites());

    return documentMapper.toResponse(documentRepository.save(document));
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    DocumentEtudiant document = documentRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Document non trouvé"));
    
    try {
        if (document.getPublicIdCloudinary() != null) {
            cloudinaryService.supprimer(document.getPublicIdCloudinary());
        }
    } catch (Exception e) {
        log.warn("Erreur suppression Cloudinary : {}", e.getMessage());
    }
    
    documentRepository.delete(document);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable) {
    return documentRepository.findByStudentTrackingId(studentTrackingId, normalize(pageable)).map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByInscriptionTrackingId(UUID inscriptionTrackingId, Pageable pageable) {
    return documentRepository.findByInscriptionTrackingId(inscriptionTrackingId, normalize(pageable)).map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByStudentAndStatut(UUID studentTrackingId, StatutDocument statut, Pageable pageable) {
    return documentRepository.findByStudentAndStatut(studentTrackingId, statut, normalize(pageable)).map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByStatut(StatutDocument statut, Pageable pageable) {
    return documentRepository.findByStatut(statut, normalize(pageable)).map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findAll(Pageable pageable) {
    return documentRepository.findAll(normalize(pageable)).map(documentMapper::toResponse);
  }
}
