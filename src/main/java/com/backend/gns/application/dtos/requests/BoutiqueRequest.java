package com.backend.gns.application.dtos.requests;

import java.util.UUID;

import com.backend.gns.domain.enums.KycStatus;

import lombok.Builder;

@Builder
public record BoutiqueRequest(

		UUID merchantTrackingId,

        UUID walletTrackingId,

		String nomBoutique,

		String categorieShop,

		KycStatus statutKYC,

		Double latitude,

		Double longitude

) {
}
