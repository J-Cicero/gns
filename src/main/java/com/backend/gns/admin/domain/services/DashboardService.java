package com.backend.gns.admin.domain.services;

import java.util.List;
import java.util.Map;

public interface DashboardService {
  Map<String, Object> getGlobalStats();

  List<Map<String, Object>> getFluxMensuel();
}
