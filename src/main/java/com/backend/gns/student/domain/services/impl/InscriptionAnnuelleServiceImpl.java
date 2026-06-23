package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.parametrage.domain.services.impl.CloudinaryStorageService;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.student.application.dtos.requests.InscriptionAnnuelleRequest;
import com.backend.gns.student.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.student.application.mappers.InscriptionAnnuelleMapper;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.InscriptionAnnuelleService;
import com.backend.gns.student.domain.services.InscriptionExterneService;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InscriptionAnnuelleServiceImpl implements InscriptionAnnuelleService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final InscriptionAnnuelleRepository inscriptionRepository;
  private final InscriptionAnnuelleMapper inscriptionMapper;
  private final StudentRepository studentRepository;
  private final ScolariteYearRepository scolariteYearRepository;
  private final ParametreGnsService parametreGnsService;
  private final WalletRepository walletRepository;
  private final InscriptionExterneService inscriptionExterneService;
  private final CloudinaryStorageService storageService;
  private final DocumentEtudiantRepository documentRepository;
  private final com.backend.gns.student.domain.services.InscriptionValidationService inscriptionValidationService;

  @Override
  @Transactional
  public InscriptionAnnuelleResponse createWithCarte(InscriptionAnnuelleRequest request, org.springframework.web.multipart.MultipartFile carte) {
    ScolariteYear year = scolariteYearRepository.findByIsOpenTrue()
            .orElseThrow(() -> new IllegalStateException("No open academic year found"));
    
    Student student = studentRepository.findByTrackingId(request.studentTrackingId())
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));

    // Check if an enrollment already exists for this student and year
    Optional<InscriptionAnnuelle> existing = inscriptionRepository.findByStudentTrackingIdAndLabel(student.getTrackingId(), year.getLabel());
    
    InscriptionAnnuelle ins;
    if (existing.isPresent()) {
        ins = existing.get();
        ins.setStudyLevel(request.studyLevel());
    } else {
        ins = inscriptionMapper.toEntity(request);
        ins.setScolariteYear(year);
        ins.setStudent(student);
        ins.setTrackingId(UUID.randomUUID());
        ins.setFullyEnrolled(false);
        ins.setAllocatedBudget(BigDecimal.ZERO);
    }
    
    InscriptionAnnuelle savedIns = inscriptionRepository.save(ins);
    
    if (carte != null && !carte.isEmpty()) {
        try {
            var upload = storageService.upload(carte, "card_" + savedIns.getTrackingId());
            com.backend.gns.student.domain.models.DocumentEtudiant doc = new com.backend.gns.student.domain.models.DocumentEtudiant();
            doc.setTrackingId(UUID.randomUUID());
            doc.setDocumentType(com.backend.gns.core.parametrage.domain.enums.TypeDocument.PIECE_IDENTITE);
            doc.setFileUrl(upload.get("url"));
            doc.setProviderPublicId(upload.get("publicId"));
            doc.setStatus(com.backend.gns.core.parametrage.domain.enums.StatutDocument.EN_ATTENTE);
            doc.setUploadedAt(java.time.LocalDateTime.now());
            doc.setStudent(student);
            doc.setInscription(savedIns);
            documentRepository.save(doc);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload student card", e);
        }
    }
    
    return inscriptionMapper.toResponse(savedIns);
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse synchroniserAvecUniversite(UUID trackingId) {
    InscriptionAnnuelle ins = inscriptionRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));
    
    InscriptionAnnuelle synched = inscriptionExterneService.synchroniserStatutInscription(ins);
    
    // If eligibility is confirmed, update budget
    if (synched.isEligibleForScholarship() && synched.isFullyEnrolled()) {
        updateBudget(synched);
    }
    
    return inscriptionMapper.toResponse(synched);
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse create(InscriptionAnnuelleRequest request) {
    ScolariteYear year =
        scolariteYearRepository
            .findByIsOpenTrue()
            .orElseThrow(() -> new IllegalStateException("No open academic year found"));
    
    Student student = studentRepository.findByTrackingId(request.studentTrackingId())
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));

    Optional<InscriptionAnnuelle> existing = inscriptionRepository.findByStudentTrackingIdAndLabel(student.getTrackingId(), year.getLabel());
    
    InscriptionAnnuelle inscription;
    if (existing.isPresent()) {
        inscription = existing.get();
        inscription.setStudyLevel(request.studyLevel());
    } else {
        inscription = inscriptionMapper.toEntity(request);
        inscription.setScolariteYear(year);
        inscription.setStudent(student);
        inscription.setTrackingId(UUID.randomUUID());
        inscription.setFullyEnrolled(false);
        inscription.setAllocatedBudget(BigDecimal.ZERO);
    }
    
    InscriptionAnnuelle savedInscription = inscriptionRepository.save(inscription);
    return inscriptionMapper.toResponse(savedInscription);
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse validerEtActiverInscription(UUID trackingId) {
    InscriptionAnnuelle ins =
        inscriptionRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));

    // Validation dynamique des documents requis avant validation
    inscriptionValidationService.validateDocuments(ins);

    if (ins.isFullyEnrolled()) {
        updateBudget(ins);
    }

    return inscriptionMapper.toResponse(inscriptionRepository.save(ins));
  }

  private void updateBudget(InscriptionAnnuelle inscription) {
    // Fixed budget application for enrolled students
    BigDecimal newBudget = new BigDecimal("30000"); // To be replaced by ParametreGns if necessary
    inscription.setAllocatedBudget(newBudget);
  }

  @Override
  public Page<InscriptionAnnuelleResponse> findAll(Pageable pageable) {
    return inscriptionRepository.findAll(normalize(pageable)).map(inscriptionMapper::toResponse);
  }

  @Override
  public Optional<InscriptionAnnuelleResponse> findByTrackingId(UUID trackingId) {
    return inscriptionRepository
        .findByTrackingId(trackingId)
        .map(inscriptionMapper::toResponse);
  }

  @Override
  public Page<InscriptionAnnuelleResponse> findByStudentTrackingId(
      UUID studentTrackingId, Pageable pageable) {
    return inscriptionRepository
        .findByStudentTrackingId(studentTrackingId, normalize(pageable))
        .map(inscriptionMapper::toResponse);
  }

  @Override
  public Page<InscriptionAnnuelleResponse> findByUniversiteTrackingId(
      UUID universiteTrackingId, Pageable pageable) {
    return inscriptionRepository
        .findByStudentUniversiteTrackingId(universiteTrackingId, normalize(pageable))
        .map(inscriptionMapper::toResponse);
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse update(UUID trackingId, InscriptionAnnuelleRequest request) {
    InscriptionAnnuelle inscription = inscriptionRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with ID: " + trackingId));

    inscription.setStudyLevel(request.studyLevel());
    
    InscriptionAnnuelle updatedInscription = inscriptionRepository.save(inscription);
    return inscriptionMapper.toResponse(updatedInscription);
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse updateStatus(UUID trackingId, com.backend.gns.student.domain.enums.StatutInscription statut) {
      throw new UnsupportedOperationException("This method is obsolete in the new architecture.");
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse updateDefinitif(UUID trackingId, boolean estInscritDefinitif) {
    InscriptionAnnuelle inscription = inscriptionRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with ID: " + trackingId));
    
    inscription.setFullyEnrolled(estInscritDefinitif);
    if (estInscritDefinitif) {
        updateBudget(inscription);
    }
    
    return inscriptionMapper.toResponse(inscriptionRepository.save(inscription));
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    InscriptionAnnuelle inscription = inscriptionRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with ID: " + trackingId));
    inscriptionRepository.delete(inscription);
  }

  @Override
  public Optional<InscriptionAnnuelleResponse> findByStudentAndAnnee(
      UUID studentTrackingId, String anneeAcademique) {
    return inscriptionRepository
        .findByStudentTrackingIdAndLabel(studentTrackingId, anneeAcademique)
        .map(inscriptionMapper::toResponse);
  }
}
