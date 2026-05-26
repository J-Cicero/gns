package com.backend.gns.Shared.domain.services.impl;

import com.backend.gns.Shared.application.dtos.requests.UniversiteRequest;
import com.backend.gns.Shared.application.dtos.responses.UniversiteResponse;
import com.backend.gns.Shared.application.mappers.UniversiteMapper;
import com.backend.gns.Shared.domain.enums.TypeParametreGns;
import com.backend.gns.Shared.domain.services.ParametreGnsService;
import com.backend.gns.Shared.domain.models.Universite;
import com.backend.gns.Shared.wallet.domain.enums.WalletStatus;
import com.backend.gns.Shared.wallet.domain.enums.WalletType;
import com.backend.gns.Shared.wallet.domain.models.Wallet;
import com.backend.gns.Shared.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.Shared.domain.services.UniversiteService;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.student.infrastructure.repositories.InscriptionAnnuelleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversiteServiceImpl implements UniversiteService {

    private final UniversiteRepository repository;
    private final UniversiteMapper mapper;
    private final StudentRepository studentRepository;
    private final InscriptionAnnuelleRepository inscriptionRepository;
    private final ParametreGnsService parametreGnsService;

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
        
        BigDecimal plafondDefaut = parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.MONTANT_DEFAUT_WALLET);
        wallet.setPlafond(plafondDefaut);
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
        Universite entity = repository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Université non trouvée"));
        repository.delete(entity);
    }

    @Override
    public List<Map<String, Object>> getSummaryStats() {
        return repository.findAll().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("trackingId", u.getTrackingId());
            map.put("nom", u.getNom());
            map.put("code", u.getCode());
            map.put("nbEtudiants", studentRepository.countByUniversite(u));
            return map;
        }).collect(Collectors.toList());
    }
}
