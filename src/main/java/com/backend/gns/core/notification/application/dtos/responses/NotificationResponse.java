package com.backend.gns.core.notification.application.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
    UUID trackingId,
    String title,
    String message,
    String targetRole,
    String type,
    boolean isRead,
    LocalDateTime createdAt
) {}
