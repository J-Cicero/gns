package com.backend.gns.core.domain.services;

import com.backend.gns.core.application.dtos.responses.BanqueResponse;
import java.util.List;

public interface BanqueService {
  List<BanqueResponse> getAllBanques();
}
