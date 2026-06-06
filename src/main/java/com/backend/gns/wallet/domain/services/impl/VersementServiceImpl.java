package com.backend.gns.wallet.domain.services.impl;

import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.paiement.domain.services.PretScolariteService;
import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.enums.TypeBourse;
import com.backend.gns.student.domain.models.InscriptionAnnuelle;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import com.backend.gns.wallet.application.dtos.requests.VersementRequest;
import com.backend.gns.wallet.application.dtos.responses.VersementResponse;
import com.backend.gns.wallet.application.mappers.VersementMapper;
import com.backend.gns.wallet.domain.enums.VersementStatut;
import com.backend.gns.wallet.domain.enums.VersementType;
import com.backend.gns.wallet.domain.models.Versement;
import com.backend.gns.wallet.domain.services.VersementService;
import com.backend.gns.wallet.domain.services.WalletService;
import com.backend.gns.wallet.infrastructure.repositories.VersementRepository;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VersementServiceImpl implements VersementService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final VersementRepository versementRepository;
  private final VersementMapper versementMapper;
  private final BoutiqueRepository boutiqueRepository;
  private final WalletService walletService;
  private final ScolariteYearRepository scolariteYearRepository;
  private final InscriptionAnnuelleRepository inscriptionAnnuelleRepository;
  private final PretScolariteService pretScolariteService;

  public VersementServiceImpl(
      VersementRepository versementRepository,
      VersementMapper versementMapper,
      BoutiqueRepository boutiqueRepository,
      WalletService walletService,
      ScolariteYearRepository scolariteYearRepository,
      InscriptionAnnuelleRepository inscriptionAnnuelleRepository,
      PretScolariteService pretScolariteService) {
    this.versementRepository = versementRepository;
    this.versementMapper = versementMapper;
    this.boutiqueRepository = boutiqueRepository;
    this.walletService = walletService;
    this.scolariteYearRepository = scolariteYearRepository;
    this.inscriptionAnnuelleRepository = inscriptionAnnuelleRepository;
    this.pretScolariteService = pretScolariteService;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public VersementResponse create(VersementRequest request) {
    Versement versement = versementMapper.toEntity(request);
    Versement savedVersement = versementRepository.save(versement);
    return versementMapper.toResponse(savedVersement);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<VersementResponse> findByTrackingId(UUID trackingId) {
    return versementRepository.findByTrackingId(trackingId).map(versementMapper::toResponse);
  }

  @Override
  @Transactional
  public VersementResponse update(UUID trackingId, VersementRequest request) {
    Versement versement =
        versementRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Versement non trouvé avec l'ID: " + trackingId));

    versement.setMontantVerse(request.montantVerse());
    versement.setTypeVersement(request.typeVersement());
    versement.setDateVersement(
        request.dateVersement() != null ? request.dateVersement() : LocalDateTime.now());
    versement.setStatut(request.statut());

    Versement updatedVersement = versementRepository.save(versement);
    return versementMapper.toResponse(updatedVersement);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Versement versement =
        versementRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Versement non trouvé avec l'ID: " + trackingId));
    versementRepository.delete(versement);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<VersementResponse> findByStatut(VersementStatut statut, Pageable pageable) {
    return versementRepository
        .findByStatut(statut, normalize(pageable))
        .map(versementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<VersementResponse> findByTypeVersement(
      VersementType typeVersement, Pageable pageable) {
    return versementRepository
        .findByTypeVersement(typeVersement, normalize(pageable))
        .map(versementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<VersementResponse> findByWalletTrackingId(UUID walletTrackingId, Pageable pageable) {
    return versementRepository
        .findByWalletTrackingId(walletTrackingId, normalize(pageable))
        .map(versementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<VersementResponse> findAll(Pageable pageable) {
    return versementRepository.findAll(normalize(pageable)).map(versementMapper::toResponse);
  }

  @Override
  @Transactional
  public void effectuerVersementMasseEtudiants(
      UUID scolariteYearTrackingId, BigDecimal montantFixe) {
    ScolariteYear year =
        scolariteYearRepository
            .findByTrackingId(scolariteYearTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Année scolaire non trouvée"));

    List<InscriptionAnnuelle> inscriptions =
        inscriptionAnnuelleRepository.findAllByScolariteYear(year);

    for (InscriptionAnnuelle ins : inscriptions) {
      if (ins.getStatut() == StatutInscription.ACTIVE && ins.isEstBoursier()) {
        BigDecimal montantAVerser = (montantFixe != null) ? montantFixe : ins.getPlafondAccorde();
        Student student = ins.getStudent();

        if (student != null && student.getWallet() != null) {
          try {
            // 1. Créditer le wallet
            walletService.crediter(student.getWallet().getTrackingId(), montantAVerser);

            // 2. Créer l'historique de versement
            Versement v = new Versement();
            v.setWallet(student.getWallet());
            v.setMontantVerse(montantAVerser);
            v.setDateVersement(LocalDateTime.now());
            v.setStatut(VersementStatut.VALIDEE);
            v.setTypeVersement(
                ins.getTypeBourse() == TypeBourse.BOURSE_DBS_54k
                    ? VersementType.BOURSE_DBS_54k
                    : VersementType.BOURSE_DBS_36k);
            versementRepository.save(v);

            // 3. Rembourser automatiquement les dettes de scolarité
            pretScolariteService.rembourserPretsEnAttente(student.getTrackingId(), montantAVerser);

            log.info("Versement réussi pour {}", student.getNom());
          } catch (Exception e) {
            log.error("Échec versement pour {}: {}", student.getNom(), e.getMessage());
          }
        }
      }
    }
  }

  @Override
  @Transactional
  public void effectuerVersementMasseBoutiques(BigDecimal seuil, BigDecimal montantQuota) {
    List<Boutique> boutiques = boutiqueRepository.findAll();

    for (Boutique boutique : boutiques) {
      if (boutique.getWallet() != null && boutique.getWallet().getSolde().compareTo(seuil) <= 0) {
        try {
          walletService.crediter(boutique.getWallet().getTrackingId(), montantQuota);

          Versement v = new Versement();
          v.setWallet(boutique.getWallet());
          v.setMontantVerse(montantQuota);
          v.setDateVersement(LocalDateTime.now());
          v.setStatut(VersementStatut.VALIDEE);
          v.setTypeVersement(VersementType.RECHARGE_QUOTA_BOUTIQUE);
          versementRepository.save(v);

          log.info("Quota rechargé pour boutique {}", boutique.getNomBoutique());
        } catch (Exception e) {
          log.error("Échec recharge boutique {}: {}", boutique.getNomBoutique(), e.getMessage());
        }
      }
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<String> previewMasseEtudiants(UUID scolariteYearTrackingId) {
    ScolariteYear year =
        scolariteYearRepository
            .findByTrackingId(scolariteYearTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Année scolaire non trouvée"));

    List<InscriptionAnnuelle> inscriptions =
        inscriptionAnnuelleRepository.findAllByScolariteYear(year);

    List<String> eligibleNames = new java.util.ArrayList<>();
    for (InscriptionAnnuelle ins : inscriptions) {
      if (ins.getStatut() == StatutInscription.ACTIVE && ins.isEstBoursier()) {
        Student student = ins.getStudent();
        if (student != null && student.getWallet() != null) {
          eligibleNames.add(student.getNom() + " " + student.getPrenom());
        }
      }
    }
    return eligibleNames;
  }

  @Override
  @Transactional(readOnly = true)
  public List<String> previewMasseBoutiques(BigDecimal seuil) {
    List<Boutique> boutiques = boutiqueRepository.findAll();
    List<String> eligibleNames = new java.util.ArrayList<>();
    for (Boutique boutique : boutiques) {
      if (boutique.getWallet() != null && boutique.getWallet().getSolde().compareTo(seuil) <= 0) {
        eligibleNames.add(boutique.getNomBoutique());
      }
    }
    return eligibleNames;
  }

  @Override
  @Transactional
  public void remiseAZeroMasseEtudiants(UUID scolariteYearTrackingId) {
    ScolariteYear year =
        scolariteYearRepository
            .findByTrackingId(scolariteYearTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Année scolaire non trouvée"));

    List<InscriptionAnnuelle> inscriptions =
        inscriptionAnnuelleRepository.findAllByScolariteYear(year);

    for (InscriptionAnnuelle ins : inscriptions) {
      if (ins.getStudent().getWallet() != null) {
        try {
          walletService.remettreAZero(ins.getStudent().getWallet().getTrackingId());
          log.info("Portefeuille remis à zéro pour l'étudiant {}", ins.getStudent().getNom());
        } catch (Exception e) {
          log.error("Échec remise à zéro pour {}: {}", ins.getStudent().getNom(), e.getMessage());
        }
      }
    }
  }

  @Override
  @Transactional
  public void remiseAZeroMasseBoutiques() {
    List<Boutique> boutiques = boutiqueRepository.findAll();
    for (Boutique boutique : boutiques) {
      if (boutique.getWallet() != null) {
        try {
          walletService.remettreAZero(boutique.getWallet().getTrackingId());
          log.info("Quota remis à zéro pour la boutique {}", boutique.getNomBoutique());
        } catch (Exception e) {
          log.error(
              "Échec remise à zéro boutique {}: {}", boutique.getNomBoutique(), e.getMessage());
        }
      }
    }
  }
}
