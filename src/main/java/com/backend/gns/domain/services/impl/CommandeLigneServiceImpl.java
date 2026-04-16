package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.CommandeLigneRequest;
import com.backend.gns.application.dtos.responses.CommandeLigneResponse;
import com.backend.gns.application.mappers.CommandeLigneMapper;
import com.backend.gns.domain.models.CommandeLigne;
import com.backend.gns.infrastructure.repositories.CommandeLigneRepository;
import com.backend.gns.domain.services.CommandeLigneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommandeLigneServiceImpl implements CommandeLigneService {

    private final CommandeLigneRepository commandeLigneRepository;
    private final CommandeLigneMapper commandeLigneMapper;

    public CommandeLigneServiceImpl(CommandeLigneRepository commandeLigneRepository, CommandeLigneMapper commandeLigneMapper) {
        this.commandeLigneRepository = commandeLigneRepository;
        this.commandeLigneMapper = commandeLigneMapper;
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
        return commandeLigneRepository.findByTrackingId(trackingId)
                .map(commandeLigneMapper::toResponse);
    }

    @Override
    @Transactional
    public CommandeLigneResponse update(UUID trackingId, CommandeLigneRequest request) {
        CommandeLigne commandeLigne = commandeLigneRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Ligne de commande non trouvée avec l'ID: " + trackingId));
        
        commandeLigne.setQuantite(request.quantite());
        commandeLigne.setPrixUnitaire(request.prixUnitaire());
        
        CommandeLigne updatedCommandeLigne = commandeLigneRepository.save(commandeLigne);
        return commandeLigneMapper.toResponse(updatedCommandeLigne);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        CommandeLigne commandeLigne = commandeLigneRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Ligne de commande non trouvée avec l'ID: " + trackingId));
        commandeLigneRepository.delete(commandeLigne);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeLigneResponse> findByCommandeTrackingId(UUID commandeTrackingId) {
        return commandeLigneRepository.findByCommandeTrackingId(commandeTrackingId).stream()
                .map(commandeLigneMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeLigneResponse> findByProductTrackingId(UUID productTrackingId) {
        return commandeLigneRepository.findByProductTrackingId(productTrackingId).stream()
                .map(commandeLigneMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeLigneResponse> findAll() {
        return commandeLigneRepository.findAll().stream()
                .map(commandeLigneMapper::toResponse)
                .toList();
    }
}
