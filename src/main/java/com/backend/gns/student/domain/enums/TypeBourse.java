package com.backend.gns.student.domain.enums;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TypeBourse {
  BOURSE_EXCELLENCE(new BigDecimal("54000")),
  BOURSE_MERITE(new BigDecimal("36000"));

  private final BigDecimal montant;
}
