package com.backend.gns.student.domain.services;

import java.math.BigDecimal;

public interface RegleBourseDbsService {
    BigDecimal getMontantBourse(String codeRegle);
    Integer getAgeMax(String codeRegle);
}
