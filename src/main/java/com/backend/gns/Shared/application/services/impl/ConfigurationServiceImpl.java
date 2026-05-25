package com.backend.gns.Shared.application.services.impl;

import com.backend.gns.Shared.application.services.ConfigurationService;
import com.backend.gns.Shared.domain.models.ConfigurationGns;
import com.backend.gns.Shared.infrastructure.repositories.ConfigurationGnsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {

    private final ConfigurationGnsRepository configurationGnsRepository;

    @Override
    public ConfigurationGns getConfiguration(String cle) {
        return configurationGnsRepository.findByCle(cle)
                .orElseThrow(() -> new EntityNotFoundException("Configuration non trouvée : " + cle));
    }

    @Override
    @Transactional
    public ConfigurationGns updateConfiguration(String cle, String valeur) {
        ConfigurationGns config = getConfiguration(cle);
        if (!config.isEstModifiable()) {
            throw new IllegalStateException("Cette configuration ne peut pas être modifiée durant l'année scolaire.");
        }
        config.setValeur(valeur);
        return configurationGnsRepository.save(config);
    }

    @Override
    public List<ConfigurationGns> getAllConfigurations() {
        return configurationGnsRepository.findAll();
    }
}
