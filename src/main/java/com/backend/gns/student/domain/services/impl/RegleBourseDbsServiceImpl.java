package com.backend.gns.student.domain.services.impl;

import com.backend.gns.student.domain.models.RegleBourseDbs;
import com.backend.gns.student.domain.services.RegleBourseDbsService;
import com.backend.gns.student.infrastructure.repositories.RegleBourseDbsRepository;
import com.backend.gns.Shared.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegleBourseDbsServiceImpl implements RegleBourseDbsService {

    private final RegleBourseDbsRepository repository;

    public RegleBourseDbsServiceImpl(RegleBourseDbsRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getMontantBourse(String codeRegle) {
        return repository.findByCodeUniqueAndEstActifTrue(codeRegle)
                .map(RegleBourseDbs::getValeurNumerique)
                .orElseThrow(() -> new ResourceNotFoundException("Règle de bourse DBS non trouvée: " + codeRegle));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAgeMax(String codeRegle) {
        return repository.findByCodeUniqueAndEstActifTrue(codeRegle)
                .map(r -> r.getValeurNumerique().intValue())
                .orElseThrow(() -> new ResourceNotFoundException("Règle d'âge DBS non trouvée: " + codeRegle));
    }
}
