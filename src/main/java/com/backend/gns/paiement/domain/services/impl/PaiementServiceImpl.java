package com.backend.gns.paiement.domain.services.impl;

import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.paiement.application.dtos.requests.PaiementRequest;
import com.backend.gns.paiement.application.dtos.responses.PaiementResponse;
import com.backend.gns.paiement.application.mappers.PaiementMapper;
import com.backend.gns.paiement.domain.enums.PaiementStatut;
import com.backend.gns.paiement.domain.enums.PaiementType;
import com.backend.gns.paiement.domain.models.Commande;
import com.backend.gns.paiement.domain.models.Paiement;
import com.backend.gns.paiement.domain.services.PaiementService;
import com.backend.gns.paiement.infrastructure.repositories.CommandeRepository;
import com.backend.gns.paiement.infrastructure.repositories.PaiementRepository;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.paiement.application.dtos.requests.QrPaymentRequest;
import com.backend.gns.paiement.domain.enums.CommandeStatut;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.wallet.domain.services.WalletService;
import java.time.LocalDateTime;


@Service
public class PaiementServiceImpl implements PaiementService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final PaiementRepository paiementRepository;
  private final CommandeRepository commandeRepository;
  private final WalletRepository walletRepository;
  private final PaiementMapper paiementMapper;
  private final ParametreGnsService parametreGnsService;
  private final StudentRepository studentRepository;
  private final BoutiqueRepository boutiqueRepository;
  private final WalletService walletService;


  public PaiementServiceImpl(
      PaiementRepository paiementRepository,
      CommandeRepository commandeRepository,
      WalletRepository walletRepository,
      PaiementMapper paiementMapper,
      ParametreGnsService parametreGnsService,
      StudentRepository studentRepository,
      BoutiqueRepository boutiqueRepository,
      WalletService walletService) {
    this.paiementRepository = paiementRepository;
    this.commandeRepository = commandeRepository;
    this.walletRepository = walletRepository;
    this.paiementMapper = paiementMapper;
    this.parametreGnsService = parametreGnsService;
    this.studentRepository = studentRepository;
    this.boutiqueRepository = boutiqueRepository;
    this.walletService = walletService;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public PaiementResponse processQrPayment(QrPaymentRequest request) {
    // 1. Récupérer la boutique
    Boutique boutique = boutiqueRepository.findByTrackingId(request.boutiqueTrackingId())
        .orElseThrow(() -> new EntityNotFoundException("Boutique non trouvée avec l'ID: " + request.boutiqueTrackingId()));

    // 2. Récupérer l'étudiant via le QR Token (ici on suppose que c'est le trackingId pour l'instant)
    UUID studentId;
    try {
        studentId = UUID.fromString(request.studentQrToken());
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Token QR invalide");
    }
    
    Student student = studentRepository.findByTrackingId(studentId)
        .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé avec ce QR Code"));

    if (student.getWallet() == null) {
        throw new IllegalStateException("Le wallet de l'étudiant est introuvable");
    }

    if (boutique.getWallet() == null) {
        throw new IllegalStateException("Le wallet de la boutique est introuvable");
    }

    BigDecimal montantTotal = request.montantTotal();

    // 3. Effectuer la transaction financière (Wallet)
    try {
        // Débiter l'étudiant (vérifie le solde automatiquement)
        walletService.debiter(student.getWallet().getTrackingId(), montantTotal);
        
        // Débiter la boutique (Consommation quota comme demandé)
        walletService.debiter(boutique.getWallet().getTrackingId(), montantTotal);
    } catch (Exception e) {
        throw new RuntimeException("Paiement échoué : " + e.getMessage());
    }

    // 4. Créer la Commande (Validation automatique)
    Commande commande = new Commande();
    commande.setTrackingId(UUID.randomUUID());
    commande.setReference("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    commande.setStudent(student);
    commande.setBoutique(boutique);
    commande.setMontantTotal(montantTotal);
    commande.setDateCommande(LocalDateTime.now());
    commande.setStatut(CommandeStatut.VALIDEE);
    commande = commandeRepository.save(commande);

    // 5. Créer l'entité Paiement
    BigDecimal taux = parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.TAUX_COMMISSION_PAIEMENT);
    BigDecimal commission = montantTotal.multiply(taux);
    BigDecimal montantNetBoutique = montantTotal.subtract(commission);

    Paiement paiement = new Paiement();
    paiement.setTrackingId(UUID.randomUUID());
    paiement.setCommande(commande);
    paiement.setStudent(student);
    paiement.setWallet(student.getWallet());
    paiement.setMontantDebite(montantTotal);
    paiement.setCommission(commission);
    paiement.setMontantNetBoutique(montantNetBoutique);
    paiement.setDate(LocalDateTime.now());
    paiement.setTypePaiement(PaiementType.ACHAT);
    paiement.setStatutPaiement(PaiementStatut.VALIDE);
    
    Paiement savedPaiement = paiementRepository.save(paiement);

    return paiementMapper.toResponse(savedPaiement);
  }

  @Override
  @Transactional
  public PaiementResponse create(PaiementRequest request) {
    Commande commande = null;
    if (request.commandeTrackingId() != null) {
      commande =
          commandeRepository
              .findByTrackingId(request.commandeTrackingId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Commande non trouvée avec l'ID: " + request.commandeTrackingId()));
    }

    Wallet wallet = null;
    if (request.walletTrackingId() != null) {
      wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
    }

    Paiement paiement = paiementMapper.toEntity(request, commande, wallet);
    Paiement savedPaiement = paiementRepository.save(paiement);
    return paiementMapper.toResponse(savedPaiement);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PaiementResponse> findByTrackingId(UUID trackingId) {
    return paiementRepository.findByTrackingId(trackingId).map(paiementMapper::toResponse);
  }

  @Override
  @Transactional
  public PaiementResponse update(UUID trackingId, PaiementRequest request) {
    Paiement paiement =
        paiementRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Paiement non trouvé avec l'ID: " + trackingId));

    paiement.setMontantDebite(request.montantDebite());
    // Recalculate commission and montantNetBoutique dynamically
    BigDecimal taux =
        parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.TAUX_COMMISSION_PAIEMENT);
    BigDecimal commission = request.montantDebite().multiply(taux);
    paiement.setCommission(commission);
    paiement.setMontantNetBoutique(request.montantDebite().subtract(commission));
    paiement.setDate(request.date());
    paiement.setTypePaiement(request.typePaiement());
    paiement.setStatutPaiement(request.statutPaiement());

    Paiement updatedPaiement = paiementRepository.save(paiement);
    return paiementMapper.toResponse(updatedPaiement);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Paiement paiement =
        paiementRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Paiement non trouvé avec l'ID: " + trackingId));
    paiementRepository.delete(paiement);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByStatutPaiement(
      PaiementStatut statutPaiement, Pageable pageable) {
    return paiementRepository
        .findByStatutPaiementOrderByDateDesc(statutPaiement, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByPaiementStatut(
      PaiementStatut paiementStatut, Pageable pageable) {
    return paiementRepository
        .findByStatutPaiementOrderByDateDesc(paiementStatut, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByTypePaiement(PaiementType typePaiement, Pageable pageable) {
    return paiementRepository
        .findByTypePaiementOrderByDateDesc(typePaiement, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByPaiementType(PaiementType paiementType, Pageable pageable) {
    return paiementRepository
        .findByTypePaiementOrderByDateDesc(paiementType, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByCommandeTrackingId(
      UUID commandeTrackingId, Pageable pageable) {
    return paiementRepository
        .findByCommandeTrackingId(commandeTrackingId, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByWalletTrackingId(UUID walletTrackingId, Pageable pageable) {
    return paiementRepository
        .findByWalletTrackingId(walletTrackingId, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findAll(Pageable pageable) {
    return paiementRepository.findAll(normalize(pageable)).map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByUniversiteTrackingId(
      UUID universiteTrackingId, Pageable pageable) {
    return paiementRepository
        .findByUniversiteTrackingId(universiteTrackingId, normalize(pageable))
        .map(paiementMapper::toResponse);
  }
}
