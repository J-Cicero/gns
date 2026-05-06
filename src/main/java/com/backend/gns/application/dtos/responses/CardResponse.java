package com.backend.gns.application.dtos.responses;

import com.backend.gns.domain.enums.CardStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CardResponse(
    UUID trackingId,
    String qrCodeStaticUuid,
    CardStatus cardStatus,
    UUID studentTrackingId) {}
