package com.backend.gns.Shared.application.services;

import com.backend.gns.Shared.domain.models.ConfigurationGns;
import java.util.List;

public interface ConfigurationService {
    ConfigurationGns getConfiguration(String cle);
    ConfigurationGns updateConfiguration(String cle, String valeur);
    List<ConfigurationGns> getAllConfigurations();
}
