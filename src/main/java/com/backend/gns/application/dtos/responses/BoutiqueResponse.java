package com.backend.gns.application.dtos.responses;

import java.util.UUID;

import com.backend.gns.domain.enums.KycStatus;

import lombok.Builder;

@Builder
public record BoutiqueResponse(

		UUID trackingId,

		UUID merchantTrackingId,

		UUID walletTrackingId,

		String nomBoutique,

		String categorieShop,

		KycStatus statutKYC,

		Double latitude,

		Double longitude

) {
}
