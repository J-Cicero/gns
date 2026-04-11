package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.exceptions.ResourceNotFoundException;
import com.backend.gns.domain.dtos.requests.VersementRequest;
import com.backend.gns.domain.dtos.responses.VersementResponse;
import com.backend.gns.domain.mappers.VersementMapper;
import com.backend.gns.domain.models.Versement;
import com.backend.gns.infrastructure.repositories.VersementRepository;
import com.backend.gns.domain.services.VersementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class VersementServiceImpl implements VersementService {

    private final VersementRepository versementRepository;
    private final VersementMapper versementMapper;

    @Override
    public VersementResponse create(VersementRequest request) {
        Versement versement = versementMapper.toEntity(request);
        Versement savedVersement = versementRepository.save(versement);
        return versementMapper.toResponse(savedVersement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VersementResponse> getAll() {
        List<Versement> versements = versementRepository.findAll();
        return versementMapper.toResponseList(versements);
    }

    @Override
    @Transactional(readOnly = true)
    public VersementResponse getByTrackingId(UUID trackingId) {
        Versement versement = versementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Versement not found with trackingId: " + trackingId));
        return versementMapper.toResponse(versement);
    }

    @Override
    public VersementResponse update(UUID trackingId, VersementRequest request) {
        Versement versement = versementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Versement not found with trackingId: " + trackingId));

        versement.setMontantVerse(request.montantVerse());
        versement.setTypeVersement(com.backend.gns.domain.enums.VersementType.valueOf(request.typeVersement()));
        versement.setDatePrevue(request.datePrevue());

        Versement updatedVersement = versementRepository.save(versement);
        return versementMapper.toResponse(updatedVersement);
    }

    @Override
    public void delete(UUID trackingId) {
        Versement versement = versementRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new ResourceNotFoundException("Versement not found with trackingId: " + trackingId));
        versementRepository.delete(versement);
    }
}
