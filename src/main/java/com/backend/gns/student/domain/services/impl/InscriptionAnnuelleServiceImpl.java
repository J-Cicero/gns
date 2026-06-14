package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.student.application.dtos.requests.InscriptionAnnuelleRequest;
import com.backend.gns.student.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.student.application.mappers.InscriptionAnnuelleMapper;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.InscriptionAnnuelleService;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  private final com.backend.gns.student.domain.services.InscriptionExterneService inscriptionExterneService;
  private final com.backend.gns.core.storage.CloudinaryStorageService storageService;
  private final com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository documentRepository;

  @Override
  @Transactional
  public InscriptionAnnuelleResponse createWithCarte(InscriptionAnnuelleRequest request, org.springframework.web.multipart.MultipartFile carte) {
    ScolariteYear year = scolariteYearRepository.findByEstOuverteTrue()
            .orElseThrow(() -> new IllegalStateException("Aucune année scolaire ouverte"));
    
    Student student = studentRepository.findByTrackingId(request.studentTrackingId())
            .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));

    // Vérifier si une inscription existe déjà pour cet étudiant et cette année
    Optional<InscriptionAnnuelle> existing = inscriptionRepository.findByStudentTrackingIdAndAnnee(student.getTrackingId(), year.getLibelle());
    
    InscriptionAnnuelle ins;
    if (existing.isPresent()) {
        ins = existing.get();
        ins.setNiveau(request.niveau());
    } else {
        ins = inscriptionMapper.toEntity(request);
        ins.setScolariteYear(year);
        ins.setStudent(student);
        ins.setTrackingId(UUID.randomUUID());
        ins.setEstInscritDefinitif(false);
        ins.setPlafondAccorde(BigDecimal.ZERO);
    }
    
    InscriptionAnnuelle savedIns = inscriptionRepository.save(ins);
    
    if (carte != null && !carte.isEmpty()) {
        try {
            var upload = storageService.upload(carte, "carte_" + savedIns.getTrackingId());
            com.backend.gns.student.domain.models.DocumentEtudiant doc = com.backend.gns.student.domain.models.DocumentEtudiant.builder()
                .trackingId(UUID.randomUUID())
                .type(com.backend.gns.core.domain.enums.TypeDocument.PIECE_IDENTITE)
                .urlFichier(upload.get("url"))
                .statut(com.backend.gns.student.domain.enums.StatutDocument.EN_ATTENTE)
                .dateDepot(java.time.LocalDateTime.now())
                .inscription(savedIns)
                .build();
            documentRepository.save(doc);
        } catch (Exception e) {
            throw new RuntimeException("Échec de l'upload de la carte étudiant", e);
        }
    }
    
    return inscriptionMapper.toResponse(savedIns);
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse synchroniserAvecUniversite(UUID trackingId) {
    InscriptionAnnuelle ins = inscriptionRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Inscription non trouvée"));
    
    InscriptionAnnuelle synched = inscriptionExterneService.synchroniserStatutInscription(ins);
    
    // Si l'éligibilité est confirmée, on met à jour le plafond
    if (synched.isEstEligibleBourse() && synched.isEstInscritDefinitif()) {
        updatePlafond(synched);
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
    InscriptionAnnuelle inscription = inscriptionMapper.toEntity(request);

    ScolariteYear year =
        scolariteYearRepository
            .findByEstOuverteTrue()
            .orElseThrow(() -> new IllegalStateException("Aucune année scolaire ouverte"));
    inscription.setScolariteYear(year);

    // Initialisation par défaut selon la refonte
    inscription.setEstInscritDefinitif(false);
    
    InscriptionAnnuelle savedInscription = inscriptionRepository.save(inscription);
    return inscriptionMapper.toResponse(savedInscription);
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse validerEtActiverInscription(UUID trackingId) {
    InscriptionAnnuelle ins =
        inscriptionRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Inscription non trouvée"));

    // La logique de validation est désormais pilotée par l'API externe (InscriptionExterneService)
    // Ici nous nous assurons que le wallet est prêt si l'inscription est définie comme définitive
    if (ins.isEstInscritDefinitif()) {
        updatePlafond(ins);
    }

    return inscriptionMapper.toResponse(inscriptionRepository.save(ins));
  }

  private void updatePlafond(InscriptionAnnuelle inscription) {
    // Application du plafond fixe pour les étudiants inscrits
    BigDecimal nouveauPlafond = new BigDecimal("30000"); // A remplacer par une lecture dans ParametreGns si nécessaire
    inscription.setPlafondAccorde(nouveauPlafond);

    Student student = inscription.getStudent();
    if (student != null && student.getWallet() != null) {
      Wallet wallet = student.getWallet();
      wallet.setPlafond(nouveauPlafond);
      walletRepository.save(wallet);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<InscriptionAnnuelleResponse> findByTrackingId(UUID trackingId) {
    return inscriptionRepository.findByTrackingId(trackingId).map(inscriptionMapper::toResponse);
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse update(UUID trackingId, InscriptionAnnuelleRequest request) {
    InscriptionAnnuelle inscription =
        inscriptionRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Inscription non trouvée avec l'ID: " + trackingId));

    inscription.setNiveau(request.niveau());
    // Les champs obsolètes (crédits, moyenne, statut, source) ont été supprimés
    
    InscriptionAnnuelle updatedInscription = inscriptionRepository.save(inscription);
    return inscriptionMapper.toResponse(updatedInscription);
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse updateStatus(UUID trackingId, com.backend.gns.student.domain.enums.StatutInscription statut) {
      // Cette méthode est devenue obsolète dans la nouvelle architecture
      throw new UnsupportedOperationException("Cette méthode est obsolète dans la nouvelle architecture.");
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse updateDefinitif(UUID trackingId, boolean estInscritDefinitif) {
    InscriptionAnnuelle inscription =
        inscriptionRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Inscription non trouvée avec l'ID: " + trackingId));
    inscription.setEstInscritDefinitif(estInscritDefinitif);
    
    if (estInscritDefinitif) {
        updatePlafond(inscription);
    }
    
    return inscriptionMapper.toResponse(inscriptionRepository.save(inscription));
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    InscriptionAnnuelle inscription =
        inscriptionRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Inscription non trouvée avec l'ID: " + trackingId));
    inscriptionRepository.delete(inscription);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<InscriptionAnnuelleResponse> findByStudentTrackingId(
      UUID studentTrackingId, Pageable pageable) {
    return inscriptionRepository
        .findByStudentTrackingId(studentTrackingId, normalize(pageable))
        .map(inscriptionMapper::toResponse);
  }

  @Override
  public Optional<InscriptionAnnuelleResponse> findByStudentAndAnnee(
      UUID studentTrackingId, String anneeAcademique) {
    return inscriptionRepository
        .findByStudentTrackingIdAndAnnee(studentTrackingId, anneeAcademique)
        .map(inscriptionMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<InscriptionAnnuelleResponse> findAll(Pageable pageable) {
    return inscriptionRepository.findAll(normalize(pageable)).map(inscriptionMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<InscriptionAnnuelleResponse> findByUniversiteTrackingId(
      UUID universiteTrackingId, Pageable pageable) {
    return inscriptionRepository
        .findByStudentUniversiteTrackingId(universiteTrackingId, normalize(pageable))
        .map(inscriptionMapper::toResponse);
  }
}
