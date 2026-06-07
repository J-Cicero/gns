package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.student.application.dtos.requests.InscriptionAnnuelleRequest;
import com.backend.gns.student.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.student.application.mappers.InscriptionAnnuelleMapper;
import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.enums.TypeParametreDbs;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.InscriptionAnnuelleService;
import com.backend.gns.student.domain.services.ParametreDbsService;
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
  private final ParametreDbsService parametreDbsService;
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

    updatePlafondIfBoursier(inscription);

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

    // Logique d'éligibilité simplifiée
    if (ins.getStudent().getStatutKYC() == com.backend.gns.core.domain.enums.KycStatus.VALIDEE) {
      ins.setStatut(StatutInscription.ACTIVE);
      updatePlafondIfBoursier(ins);
    }

    return inscriptionMapper.toResponse(inscriptionRepository.save(ins));
  }

  private void updatePlafondIfBoursier(InscriptionAnnuelle inscription) {
    // Application du plafond fixe de 30 000 FCFA pour tous les étudiants inscrits
    BigDecimal nouveauPlafond = new BigDecimal("30000");
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
    inscription.setCreditsTotalValides(request.creditsTotalValides());
    inscription.setMoyenneBac(request.moyenneBac());
    inscription.setEstBoursier(request.estBoursier());
    inscription.setTypeBourse(request.typeBourse());
    inscription.setStatut(request.statut());
    inscription.setSource(request.source());

    updatePlafondIfBoursier(inscription);

    InscriptionAnnuelle updatedInscription = inscriptionRepository.save(inscription);
    return inscriptionMapper.toResponse(updatedInscription);
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse updateStatus(UUID trackingId, StatutInscription statut) {
    InscriptionAnnuelle inscription =
        inscriptionRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Inscription non trouvée avec l'ID: " + trackingId));
    inscription.setStatut(statut);

    // Synchronize Student KYC status with DBS Decision
    if (statut == StatutInscription.ACTIVE) {
      Student student = inscription.getStudent();
      if (student != null) {
        student.setStatutKYC(com.backend.gns.core.domain.enums.KycStatus.VALIDEE);
        studentRepository.save(student);
      }
    } else if (statut == StatutInscription.REJETEE) {
      Student student = inscription.getStudent();
      if (student != null) {
        student.setStatutKYC(com.backend.gns.core.domain.enums.KycStatus.REJETE);
        studentRepository.save(student);
      }
    }

    return inscriptionMapper.toResponse(inscriptionRepository.save(inscription));
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
      StatutInscription statut, Pageable pageable) {
    return inscriptionRepository
        .findByStatut(statut, normalize(pageable))
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
