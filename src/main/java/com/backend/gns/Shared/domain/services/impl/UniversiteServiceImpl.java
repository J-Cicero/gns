package com.backend.gns.Shared.domain.services.impl;

import com.backend.gns.Shared.application.dtos.requests.UniversiteRequest;
import com.backend.gns.Shared.application.dtos.responses.UniversiteResponse;
import com.backend.gns.Shared.application.mappers.UniversiteMapper;
import com.backend.gns.Shared.domain.models.Universite;
import com.backend.gns.Shared.domain.services.UniversiteService;
import com.backend.gns.Shared.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.Shared.exception.ResourceNotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UniversiteServiceImpl implements UniversiteService {

    private final UniversiteRepository repository;
    private final UniversiteMapper mapper;

    @Override
    @Transactional
    public UniversiteResponse create(UniversiteRequest request) {
        Universite entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UniversiteResponse> findByTrackingId(UUID trackingId) {
        return repository.findByTrackingId(trackingId).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UniversiteResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional
    public void delete(UUID trackingId) {
        Universite entity = repository.findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException("Université non trouvée"));
        repository.delete(entity);
    }
}
