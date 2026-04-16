package com.backend.gns.domain.services.impl;

import jakarta.persistence.EntityNotFoundException;
import com.backend.gns.application.dtos.requests.VersementRequest;
import com.backend.gns.application.dtos.responses.VersementResponse;
import com.backend.gns.application.mappers.VersementMapper;
import com.backend.gns.domain.models.Versement;
import com.backend.gns.domain.enums.VersementStatut;
import com.backend.gns.domain.enums.VersementType;
import com.backend.gns.infrastructure.repositories.VersementRepository;
import com.backend.gns.domain.services.VersementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VersementServiceImpl implements VersementService {

    private final VersementRepository versementRepository;
    private final VersementMapper versementMapper;

    public VersementServiceImpl(VersementRepository versementRepository, VersementMapper versementMapper) {
        this.versementRepository = versementRepository;
        this.versementMapper = versementMapper;
    }

    @Override
    @Transactional
    public VersementResponse create(VersementRequest request) {
        Versement versement = versementMapper.toEntity(request);
        Versement savedVersement = versementRepository.save(versement);
        return versementMapper.toResponse(savedVersement);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VersementResponse> findByTrackingId(UUID trackingId) {
        return versementRepository.findByTrackingId(trackingId)
                .map(versementMapper::toResponse);
    }

    @Override
    @Transactional
    public VersementResponse update(UUID trackingId, VersementRequest request) {
        Versement versement = versementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Versement non trouvé avec l'ID: " + trackingId));
        
        versement.setMontantVerse(request.montantVerse());
        versement.setTypeVersement(request.typeVersement());
        versement.setDateVersement(request.dateVersement() != null ? request.dateVersement() : LocalDateTime.now());
        versement.setStatut(request.statut());
        
        Versement updatedVersement = versementRepository.save(versement);
        return versementMapper.toResponse(updatedVersement);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Versement versement = versementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Versement non trouvé avec l'ID: " + trackingId));
        versementRepository.delete(versement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VersementResponse> findByStatut(VersementStatut statut) {
        return versementRepository.findByStatut(statut).stream()
                .map(versementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VersementResponse> findByTypeVersement(VersementType typeVersement) {
        return versementRepository.findByTypeVersement(typeVersement).stream()
                .map(versementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VersementResponse> findByWalletTrackingId(UUID walletTrackingId) {
        return versementRepository.findByWalletTrackingId(walletTrackingId).stream()
                .map(versementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VersementResponse> findAll() {
        return versementRepository.findAll().stream()
                .map(versementMapper::toResponse)
                .toList();
    }
}
