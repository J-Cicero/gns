package com.backend.gns.application.dtos.requests;

import com.backend.gns.domain.enums.CardStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CardRequest(
    String qrCodeStaticUuid,
    CardStatus cardStatus,
    UUID studentTrackingId) {}
