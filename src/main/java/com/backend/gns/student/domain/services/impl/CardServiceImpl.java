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
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.domain.services.WalletService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
    Wallet wallet = card.getWallet();

    if (CardStatut.ACTIVE.equals(card.getStatus())) {
      long activeCardCount =
          cardRepository.countByWalletAndStatus(wallet, CardStatut.ACTIVE);
      if (activeCardCount > 0) {
        throw new IllegalStateException(
            "The wallet already has an active card. "
                + "Please report the existing card as lost before creating a new active card.");
      }
    }

    // Secure QR code generation
    if (card.getQrCodeData() == null || card.getQrCodeData().isEmpty()) {
      String secureToken = generateSecureQrToken(wallet.getTrackingId());
      card.setQrCodeData(secureToken);
    }

    card.setEmissionDate(LocalDateTime.now());
    card.setExpirationDate(LocalDateTime.now().plusYears(1));

    Card savedCard = cardRepository.save(card);
    return cardMapper.toResponse(savedCard);
  }

  private String generateSecureQrToken(UUID walletTrackingId) {
    // Logic for generating a secure token (e.g., a signed JWT or a random secure string)
    // This token is used for terminal verification without exposing balance
    return "SEC-" + UUID.randomUUID().toString().toUpperCase() + "-" + walletTrackingId.toString().substring(0, 8).toUpperCase();
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
                        "Student not found with ID: " + studentTrackingId));

    Wallet wallet = student.getWallet();
    if (wallet == null) {
        throw new IllegalStateException("Student does not have a wallet.");
    }

    long existingCardCount =
        cardRepository.countByWalletAndStatus(wallet, CardStatut.ACTIVE)
            + cardRepository.countByWalletAndStatus(wallet, CardStatut.EN_ATTENTE)
            + cardRepository.countByWalletAndStatus(wallet, CardStatut.EN_CONFECTION)
            + cardRepository.countByWalletAndStatus(wallet, CardStatut.PRETE);

    if (existingCardCount > 0) {
      throw new IllegalStateException(
          "You already have an active card or one in preparation.");
    }

    BigDecimal fees =
        parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.FRAIS_CREATION_CARTE);
    if (fees == null) {
      fees = new BigDecimal("4000"); // fallback
    }

    // Debit card creation fees
    walletService.debiter(wallet.getTrackingId(), fees);

    Card card = new Card();
    card.setTrackingId(UUID.randomUUID());
    card.setWallet(wallet);
    card.setStatus(CardStatut.EN_ATTENTE);
    card.setEmissionDate(LocalDateTime.now());
    card.setExpirationDate(LocalDateTime.now().plusYears(1));

    String cardNumber = "STC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    card.setCardNumber(cardNumber);
    card.setQrCodeData(generateSecureQrToken(wallet.getTrackingId()));

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
            .orElseThrow(() -> new EntityNotFoundException("Card not found"));
    
    // Update logic with English attributes
    card.setStatus(request.status());
    // ... other updates
    
    return cardMapper.toResponse(cardRepository.save(card));
  }

  @Override
  public Page<CardResponse> findAll(Pageable pageable) {
    return cardRepository.findAll(normalize(pageable)).map(cardMapper::toResponse);
  }

  public Page<CardResponse> findByWalletTrackingId(UUID walletTrackingId, Pageable pageable) {
    Wallet wallet = walletService.findByTrackingId(walletTrackingId)
            .map(w -> {
              Wallet wal = new Wallet();
              wal.setTrackingId(walletTrackingId);
              return wal;
            })
            .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

    return cardRepository.findByWallet(wallet, normalize(pageable)).map(cardMapper::toResponse);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Card card =
        cardRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Card not found with ID: " + trackingId));
    cardRepository.delete(card);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CardResponse> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable) {
    Student student =
        studentRepository
            .findByTrackingId(studentTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentTrackingId));
    return cardRepository.findByWallet(student.getWallet(), normalize(pageable)).map(cardMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CardResponse> findByCardStatus(CardStatut cardStatus, Pageable pageable) {
    return cardRepository.findByStatus(cardStatus, normalize(pageable)).map(cardMapper::toResponse);
  }

  @Override
  @Transactional
  public CardResponse declareCardLost(UUID cardTrackingId) {
    Card card =
        cardRepository
            .findByTrackingId(cardTrackingId)
            .orElseThrow(() -> new EntityNotFoundException("Card not found with ID: " + cardTrackingId));

    card.setStatus(CardStatut.INACTIVE);
    Card updatedCard = cardRepository.save(card);
    return cardMapper.toResponse(updatedCard);
  }
}
