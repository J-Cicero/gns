package com.backend.gns.domain.services.impl;

import com.backend.gns.application.dtos.requests.CommandeLigneRequest;
import com.backend.gns.application.dtos.responses.CommandeLigneResponse;
import com.backend.gns.application.mappers.CommandeLigneMapper;
import com.backend.gns.domain.models.CommandeLigne;
import com.backend.gns.domain.services.CommandeLigneService;
import com.backend.gns.infrastructure.repositories.CommandeLigneRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommandeLigneServiceImpl implements CommandeLigneService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final CommandeLigneRepository commandeLigneRepository;
  private final CommandeLigneMapper commandeLigneMapper;

  public CommandeLigneServiceImpl(
      CommandeLigneRepository commandeLigneRepository, CommandeLigneMapper commandeLigneMapper) {
    this.commandeLigneRepository = commandeLigneRepository;
    this.commandeLigneMapper = commandeLigneMapper;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public CommandeLigneResponse create(CommandeLigneRequest request) {
    CommandeLigne commandeLigne = commandeLigneMapper.toEntity(request);
    CommandeLigne savedCommandeLigne = commandeLigneRepository.save(commandeLigne);
    return commandeLigneMapper.toResponse(savedCommandeLigne);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CommandeLigneResponse> findByTrackingId(UUID trackingId) {
    return commandeLigneRepository
        .findByTrackingId(trackingId)
        .map(commandeLigneMapper::toResponse);
  }

  @Override
  @Transactional
  public CommandeLigneResponse update(UUID trackingId, CommandeLigneRequest request) {
    CommandeLigne commandeLigne =
        commandeLigneRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Ligne de commande non trouvée avec l'ID: " + trackingId));

    commandeLigne.setQuantite(request.quantite());
    commandeLigne.setPrixUnitaire(request.prixUnitaire());

    CommandeLigne updatedCommandeLigne = commandeLigneRepository.save(commandeLigne);
    return commandeLigneMapper.toResponse(updatedCommandeLigne);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    CommandeLigne commandeLigne =
        commandeLigneRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Ligne de commande non trouvée avec l'ID: " + trackingId));
    commandeLigneRepository.delete(commandeLigne);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommandeLigneResponse> findByCommandeTrackingId(
      UUID commandeTrackingId, Pageable pageable) {
    return commandeLigneRepository
        .findByCommandeTrackingId(commandeTrackingId, normalize(pageable))
        .map(commandeLigneMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommandeLigneResponse> findByProductTrackingId(
      UUID productTrackingId, Pageable pageable) {
    return commandeLigneRepository
        .findByProductTrackingId(productTrackingId, normalize(pageable))
        .map(commandeLigneMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommandeLigneResponse> findAll(Pageable pageable) {
    return commandeLigneRepository
        .findAll(normalize(pageable))
        .map(commandeLigneMapper::toResponse);
  }
}
