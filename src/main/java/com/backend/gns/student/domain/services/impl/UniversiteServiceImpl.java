package com.backend.gns.student.domain.services.impl;

import com.backend.gns.student.application.dtos.requests.UniversiteRequest;
import com.backend.gns.student.application.dtos.responses.UniversiteResponse;
import com.backend.gns.student.application.mappers.UniversiteMapper;
import com.backend.gns.student.domain.models.Universite;
import com.backend.gns.student.domain.services.UniversiteService;

import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.models.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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
  private final StudentRepository studentRepository;


  @Override
  @Transactional
  public UniversiteResponse create(UniversiteRequest request) {
    Universite entity = mapper.toEntity(request);

    // Initialisation du Wallet via Cascade
    Wallet wallet = new Wallet();
    wallet.setTrackingId(UUID.randomUUID());
    wallet.setTypeWallet(WalletType.UNIVERSITY);
    wallet.setStatutWallet(WalletStatus.ACTIF);
    wallet.setSolde(BigDecimal.ZERO);

    wallet.setPlafond(BigDecimal.ZERO);
    wallet.setDateCreation(LocalDateTime.now());

    entity.setWallet(wallet);

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
    Universite entity =
        repository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new RuntimeException("Université non trouvée"));
    repository.delete(entity);
  }

  @Override
  public List<Map<String, Object>> getSummaryStats() {
    return repository.findAll().stream()
        .map(
            u -> {
              Map<String, Object> map = new HashMap<>();
              map.put("trackingId", u.getTrackingId());
              map.put("nom", u.getNom());
              map.put("code", u.getCode());
              map.put("nbEtudiants", studentRepository.countByUniversite(u));
              map.put(
                  "nbEligibles",
                  studentRepository.countByUniversiteAndStatutKYC(
                      u, com.backend.gns.core.domain.enums.KycStatus.VALIDEE));
              return map;
            })
        .collect(Collectors.toList());
  }
}
