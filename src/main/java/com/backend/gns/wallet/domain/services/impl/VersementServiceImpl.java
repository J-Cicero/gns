package com.backend.gns.wallet.domain.services.impl;

import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
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
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.models.Versement;
import com.backend.gns.wallet.domain.models.Wallet;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VersementServiceImpl implements VersementService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final VersementRepository versementRepository;
  private final VersementMapper versementMapper;
  private final BoutiqueRepository boutiqueRepository;
  private final WalletService walletService;
  private final ScolariteYearRepository scolariteYearRepository;
  private final InscriptionAnnuelleRepository inscriptionAnnuelleRepository;

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  private void executerVersement(Wallet wallet, BigDecimal montant, VersementType type) {
    if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) return;
    
    try {
      walletService.crediter(wallet.getTrackingId(), montant);
      Versement v = new Versement();
      v.setTrackingId(UUID.randomUUID());
      v.setWallet(wallet);
      v.setMontantVerse(montant);
      v.setDateVersement(LocalDateTime.now());
      v.setStatut(VersementStatut.VALIDEE);
      v.setTypeVersement(type);
      versementRepository.save(v);
    } catch (Exception e) {
      log.error("Erreur lors de l'exécution du versement pour le wallet {}: {}", wallet.getTrackingId(), e.getMessage());
    }
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
    Versement versement = versementRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Versement non trouvé"));

    versement.setMontantVerse(request.amount());
    versement.setTypeVersement(request.paymentType());
    versement.setStatut(request.status());

    return versementMapper.toResponse(versementRepository.save(versement));
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    versementRepository.findByTrackingId(trackingId).ifPresent(versementRepository::delete);
  }

  @Override
  public Page<VersementResponse> findByStatut(VersementStatut statut, Pageable pageable) {
    return versementRepository.findByStatut(statut, normalize(pageable)).map(versementMapper::toResponse);
  }

  @Override
  public Page<VersementResponse> findByTypeVersement(VersementType typeVersement, Pageable pageable) {
    return versementRepository.findByTypeVersement(typeVersement, normalize(pageable)).map(versementMapper::toResponse);
  }

  @Override
  public Page<VersementResponse> findByWalletTrackingId(UUID walletTrackingId, Pageable pageable) {
    return versementRepository.findByWalletTrackingId(walletTrackingId, normalize(pageable)).map(versementMapper::toResponse);
  }

  @Override
  public Page<VersementResponse> findAll(Pageable pageable) {
    return versementRepository.findAll(normalize(pageable)).map(versementMapper::toResponse);
  }

  @Override
  @Transactional
  public void effectuerVersementMasseEtudiants(UUID scolariteYearTrackingId, WalletStatus statutCible, BigDecimal montantFixe) {
    ScolariteYear year = scolariteYearRepository.findByTrackingId(scolariteYearTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Année scolaire non trouvée"));

    List<InscriptionAnnuelle> inscriptions = inscriptionAnnuelleRepository.findAllByScolariteYear(year);

    for (InscriptionAnnuelle ins : inscriptions) {
      Wallet wallet = ins.getStudent().getWallet();
      if (ins.isFullyEnrolled() && wallet != null && (statutCible == null || wallet.getStatus() == statutCible)) {
        BigDecimal montant = (montantFixe != null) ? montantFixe : ins.getAllocatedBudget();
        executerVersement(wallet, montant, VersementType.DOTATION_BOURSE_INITIALE);
      }
    }
  }

  @Override
  @Transactional
  public void effectuerVersementMasseBoutiques(BigDecimal seuil, WalletStatus statutCible, BigDecimal montantQuota) {
    List<Boutique> boutiques = boutiqueRepository.findAll();

    for (Boutique boutique : boutiques) {
      Wallet wallet = boutique.getWallet();
      if (wallet != null && wallet.getBalance().compareTo(seuil) <= 0 && (statutCible == null || wallet.getStatus() == statutCible)) {
        executerVersement(wallet, montantQuota, VersementType.RECHARGE_QUOTA_BOUTIQUE);
      }
    }
  }

  @Override
  public List<String> previewMasseEtudiants(UUID scolariteYearTrackingId) {
    ScolariteYear year = scolariteYearRepository.findByTrackingId(scolariteYearTrackingId).orElseThrow();
    return inscriptionAnnuelleRepository.findAllByScolariteYear(year).stream()
            .filter(ins -> ins.isFullyEnrolled() && ins.getStudent().getWallet() != null)
            .map(ins -> ins.getStudent().getLastName() + " " + ins.getStudent().getFirstName())
            .toList();
  }

  @Override
  public List<String> previewMasseBoutiques(BigDecimal seuil) {
    return boutiqueRepository.findAll().stream()
            .filter(b -> b.getWallet() != null && b.getWallet().getBalance().compareTo(seuil) <= 0)
            .map(Boutique::getName)
            .toList();
  }

  @Override
  @Transactional
  public void remiseAZeroMasseEtudiants(UUID scolariteYearTrackingId) {
    ScolariteYear year = scolariteYearRepository.findByTrackingId(scolariteYearTrackingId).orElseThrow();
    inscriptionAnnuelleRepository.findAllByScolariteYear(year).forEach(ins -> {
      if (ins.getStudent().getWallet() != null) {
        walletService.remettreAZero(ins.getStudent().getWallet().getTrackingId());
      }
    });
  }

  @Override
  @Transactional
  public void remiseAZeroMasseBoutiques() {
    boutiqueRepository.findAll().forEach(b -> {
      if (b.getWallet() != null) {
        walletService.remettreAZero(b.getWallet().getTrackingId());
      }
    });
  }
}
