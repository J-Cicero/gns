package com.backend.gns.Shared.domain.services;

import java.math.BigDecimal;

public interface ParametreGnsService {
    String getValeur(String cle);
    BigDecimal getValeurAsBigDecimal(String cle);
    Integer getValeurAsInteger(String cle);
}
