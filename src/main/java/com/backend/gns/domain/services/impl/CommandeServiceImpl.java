package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.CommandeRequest;
import com.backend.gns.application.dtos.responses.CommandeResponse;
import com.backend.gns.application.mappers.CommandeMapper;
import com.backend.gns.domain.models.Commande;
import com.backend.gns.domain.enums.CommandeStatut;
import com.backend.gns.infrastructure.repositories.CommandeRepository;
import com.backend.gns.domain.services.CommandeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class CommandeServiceImpl implements CommandeService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final CommandeRepository commandeRepository;
    private final CommandeMapper commandeMapper;

    public CommandeServiceImpl(CommandeRepository commandeRepository, CommandeMapper commandeMapper) {
        this.commandeRepository = commandeRepository;
        this.commandeMapper = commandeMapper;
    }

    private Pageable normalize(Pageable pageable) {
        int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }

    @Override
    @Transactional
    public CommandeResponse create(CommandeRequest request) {
        Commande commande = commandeMapper.toEntity(request);
        Commande savedCommande = commandeRepository.save(commande);
        return commandeMapper.toResponse(savedCommande);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommandeResponse> findByTrackingId(UUID trackingId) {
        return commandeRepository.findByTrackingId(trackingId)
                .map(commandeMapper::toResponse);
    }

    @Override
    @Transactional
    public CommandeResponse update(UUID trackingId, CommandeRequest request) {
        Commande commande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée avec l'ID: " + trackingId));
        
        commande.setReference(request.reference());
        commande.setDateCommande(request.dateCommande());
        commande.setMontantTotal(request.montantTotal());
        commande.setStatut(request.statut());
        
        Commande updatedCommande = commandeRepository.save(commande);
        return commandeMapper.toResponse(updatedCommande);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Commande commande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Commande non trouvée avec l'ID: " + trackingId));
        commandeRepository.delete(commande);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommandeResponse> findByStatut(CommandeStatut statut, Pageable pageable) {
        return commandeRepository.findByStatutOrderByDateCommandeDesc(statut, normalize(pageable))
                .map(commandeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommandeResponse> findByCommandeStatut(CommandeStatut commandeStatut, Pageable pageable) {
        return commandeRepository.findByStatutOrderByDateCommandeDesc(commandeStatut, normalize(pageable))
                .map(commandeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommandeResponse> findByMerchantTrackingId(UUID merchantTrackingId, Pageable pageable) {
        return commandeRepository.findByMerchantTrackingId(merchantTrackingId, normalize(pageable))
                .map(commandeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommandeResponse> findByCommandeStatut(CommandeStatut commandeStatut, Pageable pageable) {
        return commandeRepository.findByStatut(commandeStatut, normalize(pageable))
                .map(commandeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommandeResponse> findAll(Pageable pageable) {
        return commandeRepository.findAll(normalize(pageable))
                .map(commandeMapper::toResponse);
    }
}
