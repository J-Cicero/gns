package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.PaiementRequest;
import com.backend.gns.application.dtos.responses.PaiementResponse;
import com.backend.gns.application.mappers.PaiementMapper;
import com.backend.gns.domain.models.Paiement;
import com.backend.gns.domain.enums.PaiementStatut;
import com.backend.gns.domain.enums.PaiementType;
import com.backend.gns.infrastructure.repositories.PaiementRepository;
import com.backend.gns.domain.services.PaiementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final PaiementMapper paiementMapper;

    public PaiementServiceImpl(PaiementRepository paiementRepository, PaiementMapper paiementMapper) {
        this.paiementRepository = paiementRepository;
        this.paiementMapper = paiementMapper;
    }

    @Override
    @Transactional
    public PaiementResponse create(PaiementRequest request) {
        Paiement paiement = paiementMapper.toEntity(request);
        Paiement savedPaiement = paiementRepository.save(paiement);
        return paiementMapper.toResponse(savedPaiement);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaiementResponse> findByTrackingId(UUID trackingId) {
        return paiementRepository.findByTrackingId(trackingId)
                .map(paiementMapper::toResponse);
    }

    @Override
    @Transactional
    public PaiementResponse update(UUID trackingId, PaiementRequest request) {
        Paiement paiement = paiementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Paiement non trouvé avec l'ID: " + trackingId));
        
        paiement.setMontantProduit(request.montantProduit());
        paiement.setCommission(request.commission());
        paiement.setMontantDebite(request.montantDebite());
        paiement.setDate(request.date());
        paiement.setTypePaiement(request.typePaiement());
        paiement.setStatutPaiement(request.statutPaiement());
        
        Paiement updatedPaiement = paiementRepository.save(paiement);
        return paiementMapper.toResponse(updatedPaiement);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Paiement paiement = paiementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Paiement non trouvé avec l'ID: " + trackingId));
        paiementRepository.delete(paiement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> findByStatutPaiement(PaiementStatut statutPaiement) {
        return paiementRepository.findByStatutPaiement(statutPaiement).stream()
                .map(paiementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> findByTypePaiement(PaiementType typePaiement) {
        return paiementRepository.findByTypePaiement(typePaiement).stream()
                .map(paiementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> findByCommandeTrackingId(UUID commandeTrackingId) {
        return paiementRepository.findByCommandeTrackingId(commandeTrackingId).stream()
                .map(paiementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> findByWalletTrackingId(UUID walletTrackingId) {
        return paiementRepository.findByWalletTrackingId(walletTrackingId).stream()
                .map(paiementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> findAll() {
        return paiementRepository.findAll().stream()
                .map(paiementMapper::toResponse)
                .toList();
    }
}
