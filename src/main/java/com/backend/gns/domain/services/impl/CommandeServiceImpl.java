package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.CommandeRequest;
import com.backend.gns.domain.dtos.responses.CommandeResponse;
import com.backend.gns.domain.mappers.CommandeMapper;
import com.backend.gns.domain.models.Commande;
import com.backend.gns.infrastructure.repositories.CommandeRepository;
import com.backend.gns.domain.services.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final CommandeMapper commandeMapper;

    @Override
    public CommandeResponse create(CommandeRequest request) {
        Commande commande = commandeMapper.toEntity(request);
        Commande savedCommande = commandeRepository.save(commande);
        return commandeMapper.toResponse(savedCommande);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeResponse> getAll() {
        List<Commande> commandes = commandeRepository.findAll();
        return commandeMapper.toResponseList(commandes);
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeResponse getByTrackingId(UUID trackingId) {
        Commande commande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with trackingId: " + trackingId));
        return commandeMapper.toResponse(commande);
    }

    @Override
    public CommandeResponse update(UUID trackingId, CommandeRequest request) {
        Commande commande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with trackingId: " + trackingId));

        commande.setMontantTotal(request.montantTotal());

        Commande updatedCommande = commandeRepository.save(commande);
        return commandeMapper.toResponse(updatedCommande);
    }

    @Override
    public void delete(UUID trackingId) {
        Commande commande = commandeRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande not found with trackingId: " + trackingId));
        commandeRepository.delete(commande);
    }
}
