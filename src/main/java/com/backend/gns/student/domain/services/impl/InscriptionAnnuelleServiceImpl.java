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
  public Page<InscriptionAnnuelleResponse> findByStatut(
      com.backend.gns.student.domain.enums.StatutInscription statut, Pageable pageable) {
    // A adapter ou supprimer
    return inscriptionRepository.findAll(normalize(pageable)).map(inscriptionMapper::toResponse);
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
