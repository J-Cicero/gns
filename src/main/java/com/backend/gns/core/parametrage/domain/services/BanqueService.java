package com.backend.gns.core.parametrage.domain.services;

import com.backend.gns.core.parametrage.application.dtos.requests.BanqueRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.BanqueResponse;
import java.util.List;

public interface BanqueService {
  List<BanqueResponse> getAllBanques();
  BanqueResponse createBanque(BanqueRequest request);
}
