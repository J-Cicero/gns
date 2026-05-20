package com.backend.gns.student.domain.services.impl;

import com.backend.gns.student.application.dtos.requests.RegleBourseDbsRequest;
import com.backend.gns.student.application.dtos.responses.RegleBourseDbsResponse;
import com.backend.gns.student.application.mappers.RegleBourseDbsMapper;
import com.backend.gns.student.domain.enums.TypeRegleBourse;
import com.backend.gns.student.domain.models.RegleBourseDbs;
import com.backend.gns.student.domain.services.RegleBourseDbsService;
import com.backend.gns.student.infrastructure.repositories.RegleBourseDbsRepository;
import com.backend.gns.Shared.exception.ResourceNotFoundException;
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
public class RegleBourseDbsServiceImpl implements RegleBourseDbsService {

    private final RegleBourseDbsRepository repository;
    private final RegleBourseDbsMapper mapper;

    @Override
    @Transactional
    public RegleBourseDbsResponse saveOrUpdate(RegleBourseDbsRequest request) {
        return repository.findByTypeRegleAndEstActifTrue(request.typeRegle())
                .map(existing -> {
                    existing.setValeurCritere(request.valeurCritere());
                    existing.setDescription(request.description());
                    existing.setEstActif(request.estActif());
                    return mapper.toResponse(repository.save(existing));
                })
                .orElseGet(() -> {
                    RegleBourseDbs entity = mapper.toEntity(request);
                    return mapper.toResponse(repository.save(entity));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RegleBourseDbsResponse> findByTrackingId(UUID trackingId) {
        return repository.findByTrackingId(trackingId).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegleBourseDbsResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RegleBourseDbsResponse> findByTypeRegle(TypeRegleBourse type) {
        return repository.findByTypeRegleAndEstActifTrue(type).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getValeurCritere(TypeRegleBourse type) {
        return repository.findByTypeRegleAndEstActifTrue(type)
                .map(RegleBourseDbs::getValeurCritere)
                .orElseThrow(() -> new ResourceNotFoundException("Règle de bourse DBS non trouvée: " + type));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getValeurCritereAsInteger(TypeRegleBourse type) {
        return getValeurCritere(type).intValue();
    }
}
