package com.backend.gns.core.notification.domain.services;

import com.backend.gns.core.notification.application.dtos.responses.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

  NotificationResponse createNotification(String title, String message, String targetRole, String type);

  List<NotificationResponse> getNotificationsForRole(String role, int limit);

  long countUnreadForRole(String role);

  void markAsRead(UUID trackingId);

  void markAllAsReadForRole(String role);
}
