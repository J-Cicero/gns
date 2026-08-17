package com.backend.gns.core.notification.application.controllers;

import com.backend.gns.core.notification.application.dtos.responses.NotificationResponse;
import com.backend.gns.core.notification.domain.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Gestion des notifications système inter-interfaces")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping
  @Operation(summary = "Récupérer la liste des notifications pour un rôle donné")
  public ResponseEntity<List<NotificationResponse>> getNotifications(
      @RequestParam(required = false, defaultValue = "ALL") String role,
      @RequestParam(required = false, defaultValue = "20") int limit) {
    return ResponseEntity.ok(notificationService.getNotificationsForRole(role, limit));
  }

  @GetMapping("/unread-count")
  @Operation(summary = "Récupérer le nombre de notifications non lues pour un rôle")
  public ResponseEntity<Map<String, Object>> getUnreadCount(
      @RequestParam(required = false, defaultValue = "ALL") String role) {
    long count = notificationService.countUnreadForRole(role);
    return ResponseEntity.ok(Map.of("unreadCount", count));
  }

  @PutMapping("/{trackingId}/read")
  @Operation(summary = "Marquer une notification comme lue")
  public ResponseEntity<Void> markAsRead(@PathVariable UUID trackingId) {
    notificationService.markAsRead(trackingId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/read-all")
  @Operation(summary = "Marquer toutes les notifications comme lues pour un rôle")
  public ResponseEntity<Void> markAllAsRead(
      @RequestParam(required = false, defaultValue = "ALL") String role) {
    notificationService.markAllAsReadForRole(role);
    return ResponseEntity.noContent().build();
  }
}
