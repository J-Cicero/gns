package com.backend.gns.core.domain.services.impl;

import com.backend.gns.core.application.dtos.requests.CompteBancaireRequest;
import com.backend.gns.core.application.dtos.responses.CompteBancaireResponse;
import com.backend.gns.core.application.mappers.CompteBancaireMapper;
import com.backend.gns.core.domain.enums.ProprietaireType;
import com.backend.gns.core.domain.models.Banque;
import com.backend.gns.core.domain.models.CompteBancaire;
import com.backend.gns.core.domain.services.CompteBancaireService;
import com.backend.gns.core.infrastructure.repositories.BanqueRepository;
import com.backend.gns.core.infrastructure.repositories.CompteBancaireRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompteBancaireServiceImpl implements CompteBancaireService {
    private final BanqueRepository banqueRepository;
    private final CompteBancaireRepository repository;
    private final CompteBancaireMapper mapper;

    @Override
    @Transactional
    public CompteBancaireResponse create(CompteBancaireRequest request) {
        log.info("Requête de création de compte bancaire reçue: {}", request);
        
        List<Banque> allBanques = banqueRepository.findAll();
        log.info("Banques en base de données: {}", allBanques.stream().map(Banque::getTrackingId).collect(Collectors.toList()));
        
        Banque banque = banqueRepository.findByTrackingId(request.banqueTrackingId())
                .orElseThrow(() -> new EntityNotFoundException("Banque non trouvée pour le trackingId: " + request.banqueTrackingId()));

        CompteBancaire compte = new CompteBancaire();
        compte.setTrackingId(UUID.randomUUID());
        compte.setBank(banque);
        compte.setAccountNumber(request.accountNumber());
        compte.setRibDocumentTrackingId(request.ribDocumentTrackingId());

        UUID proprietaireId = request.ownerTrackingId();
        if (proprietaireId == null && "GNS".equals(request.typeProprietaire())) {
            proprietaireId = UUID.randomUUID();
        }
        compte.setOwnerTrackingId(proprietaireId);
        compte.setOwnerType(ProprietaireType.valueOf(request.typeProprietaire()));

        return mapper.toResponse(repository.save(compte));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompteBancaireResponse> findByTrackingId(UUID trackingId) {
        return repository.findByTrackingId(trackingId).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompteBancaireResponse> findAll() {
        return repository.findByOwnerType(ProprietaireType.GNS).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        repository.findByTrackingId(trackingId).ifPresent(repository::delete);
    }
}
