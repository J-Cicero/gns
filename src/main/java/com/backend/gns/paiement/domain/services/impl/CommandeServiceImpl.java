package com.backend.gns.paiement.domain.services.impl;

import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.paiement.application.dtos.requests.CommandeRequest;
import com.backend.gns.paiement.application.dtos.responses.CommandeResponse;
import com.backend.gns.paiement.application.mappers.CommandeMapper;
import com.backend.gns.paiement.domain.enums.CommandeStatut;
import com.backend.gns.paiement.domain.models.Commande;
import com.backend.gns.paiement.domain.services.CommandeService;
import com.backend.gns.paiement.infrastructure.repositories.CommandeRepository;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.wallet.domain.services.WalletService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CommandeServiceImpl implements CommandeService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final CommandeRepository commandeRepository;
  private final CommandeMapper commandeMapper;
  private final StudentRepository studentRepository;
  private final BoutiqueRepository boutiqueRepository;
  private final WalletService walletService;
  private final PasswordEncoder passwordEncoder;

  public CommandeServiceImpl(
      CommandeRepository commandeRepository,
      CommandeMapper commandeMapper,
      BoutiqueRepository boutiqueRepository,
      StudentRepository studentRepository,
      WalletService walletService,
      PasswordEncoder passwordEncoder) {
    this.commandeRepository = commandeRepository;
    this.commandeMapper = commandeMapper;
    this.boutiqueRepository = boutiqueRepository;
    this.studentRepository = studentRepository;
    this.walletService = walletService;
    this.passwordEncoder = passwordEncoder;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public CommandeResponse create(CommandeRequest request) {
    Student student =
        studentRepository
            .findByTrackingId(request.studentTrackingId())
            .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));
    Boutique boutique =
        boutiqueRepository
            .findByTrackingId(request.boutiqueTrackingId())
            .orElseThrow(() -> new EntityNotFoundException("Boutique non trouvée"));

    Commande commande = commandeMapper.toEntity(request, student, boutique);
    commande.setStatut(CommandeStatut.EN_ATTENTE);
    commande.setDateCommande(LocalDateTime.now());
    Commande savedCommande = commandeRepository.save(commande);
    return commandeMapper.toResponse(savedCommande);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CommandeResponse> findByTrackingId(UUID trackingId) {
    return commandeRepository.findByTrackingId(trackingId).map(commandeMapper::toResponse);
  }

  @Override
  @Transactional
  public CommandeResponse update(UUID trackingId, CommandeRequest request) {
    Commande commande =
        commandeRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Commande non trouvée avec l'ID: " + trackingId));

    commande.setStatut(request.statut());
    Commande updatedCommande = commandeRepository.save(commande);
    return commandeMapper.toResponse(updatedCommande);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Commande commande =
        commandeRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Commande non trouvée avec l'ID: " + trackingId));
    commandeRepository.delete(commande);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommandeResponse> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable) {
    return commandeRepository
        .findByStudentTrackingId(studentTrackingId, normalize(pageable))
        .map(commandeMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommandeResponse> findByBoutiqueTrackingId(
      UUID boutiqueTrackingId, Pageable pageable) {
    return commandeRepository
        .findByBoutiqueTrackingId(boutiqueTrackingId, normalize(pageable))
        .map(commandeMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommandeResponse> findByMerchantTrackingId(
      UUID merchantTrackingId, Pageable pageable) {
    return commandeRepository
        .findByMerchantTrackingId(merchantTrackingId, normalize(pageable))
        .map(commandeMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommandeResponse> findByCommandeStatut(
      CommandeStatut commandeStatut, Pageable pageable) {
    return commandeRepository
        .findByStatut(commandeStatut, normalize(pageable))
        .map(commandeMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommandeResponse> findByStatut(CommandeStatut statut, Pageable pageable) {
    return commandeRepository
        .findByStatut(statut, normalize(pageable))
        .map(commandeMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommandeResponse> findAll(Pageable pageable) {
    return commandeRepository.findAll(normalize(pageable)).map(commandeMapper::toResponse);
  }

  @Override
  @Transactional
  public void payerCommande(UUID commandeTrackingId, String pinCode) {
    Commande commande =
        commandeRepository
            .findByTrackingId(commandeTrackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Commande non trouvée avec l'ID: " + commandeTrackingId));

    Student student = commande.getStudent();
    if (student == null) {
      throw new IllegalStateException("Étudiant non trouvé pour cette commande");
    }

    // Validation du Code PIN
    if (student.getPinCode() == null) {
      throw new IllegalStateException("Le code PIN n'est pas configuré pour cet étudiant");
    }

    if (!passwordEncoder.matches(pinCode, student.getPinCode())) {
      throw new IllegalArgumentException("Code PIN incorrect");
    }

    Boutique boutique = commande.getBoutique();
    if (boutique == null) {
      throw new EntityNotFoundException(
          "Boutique non trouvée pour la commande ID: " + commandeTrackingId);
    }

    BigDecimal montantTotal = commande.getMontantTotal();

    try {
      // ÉTAPE 1: Débiter le wallet du student (Argent réel)
      walletService.debiter(commande.getStudent().getWallet().getTrackingId(), montantTotal);

      // ÉTAPE 2: Débiter le wallet de la boutique (Consommation du quota de vente)
      walletService.debiter(boutique.getWallet().getTrackingId(), montantTotal);

      // Mettre à jour le statut de la commande
      commande.setStatut(CommandeStatut.VALIDEE);
      commandeRepository.save(commande);

    } catch (IllegalArgumentException | IllegalStateException e) {
      // Solde insuffisant ou autre erreur - annuler l'achat
      commande.setStatut(CommandeStatut.ANNULEE);
      commandeRepository.save(commande);
      throw new RuntimeException("Paiement échoué : " + e.getMessage());
    }
  }
}
