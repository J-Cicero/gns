package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.PaiementRequest;
import com.backend.gns.domain.dtos.responses.PaiementResponse;
import com.backend.gns.domain.mappers.PaiementMapper;
import com.backend.gns.domain.models.Paiement;
import com.backend.gns.infrastructure.repositories.PaiementRepository;
import com.backend.gns.domain.services.PaiementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final PaiementMapper paiementMapper;

    @Override
    public PaiementResponse create(PaiementRequest request) {
        Paiement paiement = paiementMapper.toEntity(request);
        Paiement savedPaiement = paiementRepository.save(paiement);
        return paiementMapper.toResponse(savedPaiement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementResponse> getAll() {
        List<Paiement> paiements = paiementRepository.findAll();
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    @Transactional(readOnly = true)
    public PaiementResponse getByTrackingId(UUID trackingId) {
        Paiement paiement = paiementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found with trackingId: " + trackingId));
        return paiementMapper.toResponse(paiement);
    }

    @Override
    public PaiementResponse update(UUID trackingId, PaiementRequest request) {
        Paiement paiement = paiementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found with trackingId: " + trackingId));

        paiement.setMontantProduit(request.montantProduit());
        paiement.setCommission(request.commission());
        paiement.setMontantDebite(request.montantDebite());
        paiement.setTypePaiement(com.backend.gns.domain.enums.PaiementType.valueOf(request.typePaiement()));

        Paiement updatedPaiement = paiementRepository.save(paiement);
        return paiementMapper.toResponse(updatedPaiement);
    }

    @Override
    public void delete(UUID trackingId) {
        Paiement paiement = paiementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement not found with trackingId: " + trackingId));
        paiementRepository.delete(paiement);
    }
}
