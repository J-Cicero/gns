package com.backend.gns.parametrage.domain.services.impl;

import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.parametrage.application.dtos.requests.ParametreDbsRequest;
import com.backend.gns.parametrage.application.dtos.responses.ParametreDbsResponse;
import com.backend.gns.parametrage.application.mappers.ParametreDbsMapper;
import com.backend.gns.parametrage.domain.enums.TypeParametreDbs;
import com.backend.gns.parametrage.domain.models.ParametreDbs;
import com.backend.gns.parametrage.domain.services.ParametreDbsService;
import com.backend.gns.parametrage.infrastructure.repositories.ParametreDbsRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParametreDbsServiceImpl implements ParametreDbsService {

    private final ParametreDbsRepository repository;
    private final ParametreDbsMapper mapper;

    @Override
    @Transactional
    public ParametreDbsResponse saveOrUpdate(ParametreDbsRequest request) {
        ParametreDbs parametre = repository.findByNomParametre(request.nomParametre())
                .orElse(new ParametreDbs());

        parametre.setNomParametre(request.nomParametre());
        parametre.setValeurParametre(request.valeurParametre());
        parametre.setEstActif(request.estActif());
        parametre.setDescription(request.description());

        ParametreDbs saved = repository.save(parametre);
        log.info("Paramètre DBS sauvegardé: {}", saved.getNomParametre());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParametreDbsResponse> findByTrackingId(UUID trackingId) {
        return repository.findByTrackingId(trackingId).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParametreDbsResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ParametreDbsResponse> findByNomParametre(TypeParametreDbs nom) {
        return repository.findByNomParametre(nom).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public String getValeur(TypeParametreDbs type) {
        return repository.findByNomParametre(type)
                .map(ParametreDbs::getValeurParametre)
                .orElseThrow(() -> new ResourceNotFoundException("Paramètre DBS non trouvé: " + type));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getValeurAsBigDecimal(TypeParametreDbs type) {
        String val = getValeur(type);
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            log.error("Impossible de convertir le paramètre DBS {} en BigDecimal. Valeur: {}", type, val);
            throw new IllegalArgumentException("Format invalide pour le paramètre " + type);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getValeurAsInteger(TypeParametreDbs type) {
        String val = getValeur(type);
        try {
            return Integer.valueOf(val);
        } catch (NumberFormatException e) {
            log.error("Impossible de convertir le paramètre DBS {} en Integer. Valeur: {}", type, val);
            throw new IllegalArgumentException("Format invalide pour le paramètre " + type);
        }
    }
}
