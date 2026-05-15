package com.backend.gns.Shared.domain.services.impl;

import com.backend.gns.Shared.domain.models.ParametreGns;
import com.backend.gns.Shared.domain.services.ParametreGnsService;
import com.backend.gns.Shared.infrastructure.repositories.ParametreGnsRepository;
import com.backend.gns.Shared.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParametreGnsServiceImpl implements ParametreGnsService {

    private final ParametreGnsRepository repository;

    public ParametreGnsServiceImpl(ParametreGnsRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public String getValeur(String cle) {
        return repository.findByCleAndEstActifTrue(cle)
                .map(ParametreGns::getValeur)
                .orElseThrow(() -> new ResourceNotFoundException("Paramètre GNS non trouvé: " + cle));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getValeurAsBigDecimal(String cle) {
        return new BigDecimal(getValeur(cle));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getValeurAsInteger(String cle) {
        return Integer.parseInt(getValeur(cle));
    }
}
