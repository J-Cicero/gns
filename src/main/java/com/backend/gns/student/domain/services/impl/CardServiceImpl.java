package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.student.application.dtos.requests.CardRequest;
import com.backend.gns.student.application.dtos.responses.CardResponse;
import com.backend.gns.student.application.mappers.CardMapper;
import com.backend.gns.student.domain.enums.CardStatut;
import com.backend.gns.student.domain.models.Card;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.CardService;
import com.backend.gns.student.infrastructure.repositories.CardRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.wallet.domain.services.WalletService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardServiceImpl implements CardService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final CardRepository cardRepository;
  private final CardMapper cardMapper;
  private final StudentRepository studentRepository;
  private final WalletService walletService;
  private final ParametreGnsService parametreGnsService;

  public CardServiceImpl(
      CardRepository cardRepository,
      CardMapper cardMapper,
      StudentRepository studentRepository,
      WalletService walletService,
      ParametreGnsService parametreGnsService) {
    this.cardRepository = cardRepository;
    this.cardMapper = cardMapper;
    this.studentRepository = studentRepository;
    this.walletService = walletService;
    this.parametreGnsService = parametreGnsService;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public CardResponse create(CardRequest request) {
    Card card = cardMapper.toEntity(request);

    if (CardStatut.ACTIVE.equals(card.getStatut())) {
      long activeCardCount =
          cardRepository.countByStudentAndStatut(card.getStudent(), CardStatut.ACTIVE);
      if (activeCardCount > 0) {
        throw new IllegalStateException(
            "L'étudiant possède déjà une carte active. "
                + "Veuillez déclarer la carte existante comme perdue avant de créer une nouvelle carte active.");
      }
    }

    // Génération automatique du code QR si non fourni
    if (card.getQrCodeStatique() == null || card.getQrCodeStatique().isEmpty()) {
      String uniqueRef =
          "STC-"
              + card.getStudent().getTrackingId().toString().substring(0, 8).toUpperCase()
              + "-"
              + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
      card.setQrCodeStatique(uniqueRef);
    }

    card.setDateEmission(LocalDateTime.now());

    Card savedCard = cardRepository.save(card);
    return cardMapper.toResponse(savedCard);
  }

  @Override
  @Transactional
  public CardResponse demanderCarte(UUID studentTrackingId) {
    Student student =
        studentRepository
            .findByTrackingId(studentTrackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Étudiant non trouvé avec l'ID: " + studentTrackingId));

    long existingCardCount =
        cardRepository.countByStudentAndStatut(student, CardStatut.ACTIVE)
            + cardRepository.countByStudentAndStatut(student, CardStatut.EN_ATTENTE)
            + cardRepository.countByStudentAndStatut(student, CardStatut.EN_CONFECTION)
            + cardRepository.countByStudentAndStatut(student, CardStatut.PRETE);

    if (existingCardCount > 0) {
      throw new IllegalStateException(
          "Vous avez déjà une carte active ou en cours de préparation.");
    }

    BigDecimal frais =
        parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.FRAIS_CREATION_CARTE);
    if (frais == null) {
      frais = new BigDecimal("4000"); // fallback
    }

    if (student.getWallet() == null) {
      throw new IllegalStateException(
          "L'étudiant ne possède pas de portefeuille pour payer les frais.");
    }

    // Débit des frais de création de carte
    walletService.debiter(student.getWallet().getTrackingId(), frais);

    Card card = new Card();
    card.setTrackingId(UUID.randomUUID());
    card.setStudent(student);
    card.setStatut(CardStatut.EN_ATTENTE);
    card.setDateEmission(LocalDateTime.now());

    String uniqueRef =
        "REQ-"
            + student.getTrackingId().toString().substring(0, 8).toUpperCase()
            + "-"
            + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    card.setQrCodeStatique(uniqueRef);

    Card savedCard = cardRepository.save(card);
    return cardMapper.toResponse(savedCard);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CardResponse> findByTrackingId(UUID trackingId) {
    return cardRepository.findByTrackingId(trackingId).map(cardMapper::toResponse);
  }

  @Override
  @Transactional
  public CardResponse update(UUID trackingId, CardRequest request) {
    Card card =
        cardRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Carte non trouvée avec l'ID: " + trackingId));

    if (CardStatut.ACTIVE.equals(request.cardStatus())
        && !CardStatut.ACTIVE.equals(card.getStatut())) {
      long activeCardCount =
          cardRepository.countByStudentAndStatut(card.getStudent(), CardStatut.ACTIVE);
      if (activeCardCount > 0) {
        throw new IllegalStateException(
            "L'étudiant possède déjà une carte active. "
                + "Impossible de définir cette carte comme active.");
      }
    }

    card.setQrCodeStatique(request.qrCodeStatique());
    card.setStatut(request.cardStatus());

    if (request.studentTrackingId() != null) {
      Student student =
          studentRepository
              .findByTrackingId(request.studentTrackingId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Étudiant non trouvé avec l'ID: " + request.studentTrackingId()));
      card.setStudent(student);
    }

    Card updatedCard = cardRepository.save(card);
    return cardMapper.toResponse(updatedCard);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Card card =
        cardRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Carte non trouvée avec l'ID: " + trackingId));
    cardRepository.delete(card);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CardResponse> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable) {
    Student student =
        studentRepository
            .findByTrackingId(studentTrackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Étudiant non trouvé avec l'ID: " + studentTrackingId));
    return cardRepository.findByStudent(student, normalize(pageable)).map(cardMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CardResponse> findByCardStatus(CardStatut cardStatus, Pageable pageable) {
    return cardRepository.findByStatut(cardStatus, normalize(pageable)).map(cardMapper::toResponse);
  }

  @Override
  @Transactional
  public CardResponse declareCardLost(UUID cardTrackingId) {
    Card card =
        cardRepository
            .findByTrackingId(cardTrackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("Carte non trouvée avec l'ID: " + cardTrackingId));

    card.setStatut(CardStatut.INACTIVE);
    Card updatedCard = cardRepository.save(card);
    return cardMapper.toResponse(updatedCard);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CardResponse> findAll(Pageable pageable) {
    return cardRepository.findAll(normalize(pageable)).map(cardMapper::toResponse);
  }
}
