package com.backend.gns.application.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.backend.gns.domain.enums.WalletType;

import lombok.Builder;

@Builder
public record WalletRequest(

	UUID trackingStudentId,

	WalletType typeWallet,

	 BigDecimal solde,

	 BigDecimal plafond,

	 Boolean estVerrouille,

     LocalDateTime dateCreation

) {
}
