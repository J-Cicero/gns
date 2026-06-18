package com.backend.gns.core.parametrage.domain.services.impl;

import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.application.dtos.requests.ParametreGnsRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.ParametreGnsResponse;
import com.backend.gns.core.parametrage.application.mappers.ParametreGnsMapper;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.models.ParametreGns;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.core.parametrage.infrastructure.repositories.ParametreGnsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParametreGnsServiceImpl implements ParametreGnsService {

  private final ParametreGnsRepository repository;
  private final ParametreGnsMapper mapper;


@Override
@Transactional
public ParametreGnsResponse create(ParametreGnsRequest request) {
    // Check for existing by nomParametre to prevent duplicates (since nomParametre is unique)
    repository.findByNomParametre(request.nomParametre())
        .ifPresent(existing -> {
            throw new IllegalArgumentException("ParametreGns avec nomParametre '" + request.nomParametre() + "' existe déjà.");
        });

    ParametreGns entity = mapper.toEntity(request); // Mapper now generates trackingId
    ParametreGns savedEntity = repository.save(entity);
    return mapper.toResponse(savedEntity);
}

@Override
@Transactional
public ParametreGnsResponse update(UUID trackingId, ParametreGnsRequest request) {
    ParametreGns existing = repository.findByTrackingId(trackingId)
        .orElseThrow(() -> new ResourceNotFoundException("ParametreGns non trouvé avec trackingId: " + trackingId));

    // Update fields from request
    existing.setNomParametre(request.nomParametre());
    existing.setValeurParametre(request.valeurParametre());
    existing.setDescription(request.description());

    ParametreGns updatedEntity = repository.save(existing);
    return mapper.toResponse(updatedEntity);
}

@Override
@Transactional(readOnly = true)
public Optional<ParametreGnsResponse> findByNomParametre(TypeParametreGns nom) {
  return repository.findByNomParametre(nom).map(mapper::toResponse);
}

@Override
@Transactional(readOnly = true)
public Page<ParametreGnsResponse> findAll(Pageable pageable) {
  return repository.findAll(pageable).map(mapper::toResponse);
}

@Override
@Transactional(readOnly = true)
public Optional<ParametreGnsResponse> findByTrackingId(UUID trackingId) {
  return repository.findByTrackingId(trackingId).map(mapper::toResponse);
}

@Override
@Transactional(readOnly = true)
public String getValeur(TypeParametreGns type) {
  return repository
      .findByNomParametre(type)
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
