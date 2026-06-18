package com.backend.gns.student.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum TypeBourse {
  BOURSE_EXCELLENCE(new BigDecimal("54000")),
  BOURSE_MERITE(new BigDecimal("36000"));

  private final BigDecimal montant;
}
