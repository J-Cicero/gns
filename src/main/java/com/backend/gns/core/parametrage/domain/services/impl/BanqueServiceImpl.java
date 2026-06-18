package com.backend.gns.core.domain.services.impl;

import com.backend.gns.core.application.dtos.requests.BanqueRequest;
import com.backend.gns.core.application.dtos.responses.BanqueResponse;
import com.backend.gns.core.application.mappers.BanqueMapper;
import com.backend.gns.core.domain.services.BanqueService;
import com.backend.gns.core.parametrage.domain.models.Banque;
import com.backend.gns.core.parametrage.infrastructure.repositories.BanqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BanqueServiceImpl implements BanqueService {

  private final BanqueRepository banqueRepository;
  private final BanqueMapper banqueMapper;

  @Override
  public List<BanqueResponse> getAllBanques() {
    return banqueRepository.findAll().stream()
        .map(banqueMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public BanqueResponse createBanque(BanqueRequest request) {
    Banque banque = new Banque();
    banque.setTrackingId(UUID.randomUUID());
    banque.setName(request.name());
    banque.setCode(request.code());
    banque.setLogoUrl(request.logoUrl());
    return banqueMapper.toResponse(banqueRepository.save(banque));
  }
}
