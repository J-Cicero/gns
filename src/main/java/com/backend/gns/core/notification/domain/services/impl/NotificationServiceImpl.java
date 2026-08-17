package com.backend.gns.core.notification.domain.services.impl;

import com.backend.gns.core.notification.application.dtos.responses.NotificationResponse;
import com.backend.gns.core.notification.domain.models.Notification;
import com.backend.gns.core.notification.domain.services.NotificationService;
import com.backend.gns.core.notification.infrastructure.repositories.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;

  public NotificationServiceImpl(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Override
  @Transactional
  public NotificationResponse createNotification(String title, String message, String targetRole, String type) {
    Notification notification = new Notification();
    notification.setTrackingId(UUID.randomUUID());
    notification.setTitle(title);
    notification.setMessage(message);
    notification.setTargetRole(targetRole);
    notification.setType(type);
    notification.setRead(false);
    notification.setCreatedAt(LocalDateTime.now());

    Notification saved = notificationRepository.save(notification);
    return mapToResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<NotificationResponse> getNotificationsForRole(String role, int limit) {
    List<String> targetRoles = getRolesToFetch(role);
    Pageable pageable = PageRequest.of(0, limit > 0 ? limit : 20);
    return notificationRepository.findByTargetRoleInOrderByCreatedAtDesc(targetRoles, pageable)
        .stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public long countUnreadForRole(String role) {
    List<String> targetRoles = getRolesToFetch(role);
    return notificationRepository.countByTargetRoleInAndIsReadFalse(targetRoles);
  }

  @Override
  @Transactional
  public void markAsRead(UUID trackingId) {
    Notification notification = notificationRepository.findByTrackingId(trackingId)
        .orElseThrow(() -> new EntityNotFoundException("Notification non trouvée"));
    notification.setRead(true);
    notificationRepository.save(notification);
  }

  @Override
  @Transactional
  public void markAllAsReadForRole(String role) {
    List<String> targetRoles = getRolesToFetch(role);
    List<Notification> unread = notificationRepository.findByTargetRoleInAndIsReadFalse(targetRoles);
    for (Notification n : unread) {
      n.setRead(true);
    }
    notificationRepository.saveAll(unread);
  }

  private List<String> getRolesToFetch(String role) {
    if (role == null || role.isBlank()) {
      return List.of("ALL");
    }
    return Arrays.asList(role, "ALL");
  }

  private NotificationResponse mapToResponse(Notification n) {
    return new NotificationResponse(
        n.getTrackingId(),
        n.getTitle(),
        n.getMessage(),
        n.getTargetRole(),
        n.getType(),
        n.isRead(),
        n.getCreatedAt()
    );
  }
}
