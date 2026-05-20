package com.backend.gns.student.domain.enums;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TypeBourse {
    BOURSE_DBS_36k(new BigDecimal("36000")),
    BOURSE_DBS_54k(new BigDecimal("54000"));

    private final BigDecimal montant;
}
