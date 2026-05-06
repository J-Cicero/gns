package com.backend.gns.domain.services.impl;

import com.backend.gns.application.dtos.requests.CardRequest;
import com.backend.gns.application.dtos.responses.CardResponse;
import com.backend.gns.application.mappers.CardMapper;
import com.backend.gns.domain.enums.CardStatus;
import com.backend.gns.domain.models.Card;
import com.backend.gns.domain.models.Student;
import com.backend.gns.domain.services.CardService;
import com.backend.gns.infrastructure.repositories.CardRepository;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
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

  public CardServiceImpl(
      CardRepository cardRepository,
      CardMapper cardMapper,
      StudentRepository studentRepository) {
    this.cardRepository = cardRepository;
    this.cardMapper = cardMapper;
    this.studentRepository = studentRepository;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public CardResponse create(CardRequest request) {
    Card card = cardMapper.toEntity(request);

    // Validation: une seule carte ACTIVE par étudiant
    if (CardStatus.ACTIVE.equals(card.getCardStatus())) {
      long activeCardCount =
          cardRepository.countByStudentAndCardStatus(card.getStudent(), CardStatus.ACTIVE);
      if (activeCardCount > 0) {
        throw new IllegalStateException(
            "L'étudiant possède déjà une carte active. "
                + "Veuillez déclarer la carte existante comme perdue avant de créer une nouvelle carte active.");
      }
    }

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

    // Validation: si on passe le statut à ACTIVE, vérifier qu'il n'y en a pas d'autre
    if (CardStatus.ACTIVE.equals(request.cardStatus())
        && !CardStatus.ACTIVE.equals(card.getCardStatus())) {
      long activeCardCount =
          cardRepository.countByStudentAndCardStatus(card.getStudent(), CardStatus.ACTIVE);
      if (activeCardCount > 0) {
        throw new IllegalStateException(
            "L'étudiant possède déjà une carte active. "
                + "Impossible de définir cette carte comme active.");
      }
    }

    card.setQrCodeStaticUuid(request.qrCodeStaticUuid());
    card.setCardStatus(request.cardStatus());

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
  public Page<CardResponse> findByCardStatus(CardStatus cardStatus, Pageable pageable) {
    return cardRepository.findByCardStatus(cardStatus, normalize(pageable)).map(cardMapper::toResponse);
  }

  @Override
  @Transactional
  public CardResponse declareCardLost(UUID cardTrackingId) {
    Card card =
        cardRepository
            .findByTrackingId(cardTrackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Carte non trouvée avec l'ID: " + cardTrackingId));

    card.setCardStatus(CardStatus.PERDUE);
    Card updatedCard = cardRepository.save(card);
    return cardMapper.toResponse(updatedCard);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CardResponse> findAll(Pageable pageable) {
    return cardRepository.findAll(normalize(pageable)).map(cardMapper::toResponse);
  }
}
