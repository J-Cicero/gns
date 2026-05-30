package com.backend.gns.parametrage.domain.services.impl;

import com.backend.gns.parametrage.application.dtos.requests.ParametreGnsRequest;
import com.backend.gns.parametrage.application.dtos.responses.ParametreGnsResponse;
import com.backend.gns.parametrage.application.mappers.ParametreGnsMapper;
import com.backend.gns.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.parametrage.domain.models.ParametreGns;
import com.backend.gns.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.parametrage.infrastructure.repositories.ParametreGnsRepository;
import com.backend.gns.core.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParametreGnsServiceImpl implements ParametreGnsService {

    private final ParametreGnsRepository repository;
    private final ParametreGnsMapper mapper;

    @Override
    @Transactional
    public ParametreGnsResponse saveOrUpdate(ParametreGnsRequest request) {
        return repository.findByNomParametreAndEstActifTrue(request.nomParametre())
                .map(existing -> {
                    existing.setValeurParametre(request.valeurParametre());
                    existing.setDescription(request.description());
                    existing.setEstActif(request.estActif());
                    return mapper.toResponse(repository.save(existing));
                })
                .orElseGet(() -> {
                    ParametreGns entity = mapper.toEntity(request);
                    return mapper.toResponse(repository.save(entity));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParametreGnsResponse> findByTrackingId(UUID trackingId) {
        return repository.findByTrackingId(trackingId).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParametreGnsResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParametreGnsResponse> findByNomParametre(TypeParametreGns nom) {
        return repository.findByNomParametreAndEstActifTrue(nom).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public String getValeur(TypeParametreGns type) {
        return repository.findByNomParametreAndEstActifTrue(type)
                .map(ParametreGns::getValeurParametre)
                .orElseThrow(() -> new ResourceNotFoundException("Paramètre GNS non trouvé: " + type));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getValeurAsBigDecimal(TypeParametreGns type) {
        return new BigDecimal(getValeur(type));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getValeurAsInteger(TypeParametreGns type) {
        return Integer.parseInt(getValeur(type));
    }
}
