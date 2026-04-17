package com.backend.gns.application.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.backend.gns.domain.enums.WalletStatus;
import com.backend.gns.domain.enums.WalletType;

import lombok.Builder;

@Builder
public record WalletResponse(

	UUID trackingId,

	WalletType typeWallet,

	WalletStatus statutWallet,

	 BigDecimal solde,

	 BigDecimal plafond,

	 Boolean estVerrouille,

     LocalDateTime dateCreation


){}
