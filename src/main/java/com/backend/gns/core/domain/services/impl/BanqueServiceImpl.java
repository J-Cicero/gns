package com.backend.gns.core.domain.services.impl;

import com.backend.gns.core.application.dtos.responses.BanqueResponse;
import com.backend.gns.core.application.mappers.BanqueMapper;
import com.backend.gns.core.domain.services.BanqueService;
import com.backend.gns.core.infrastructure.repositories.BanqueRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
