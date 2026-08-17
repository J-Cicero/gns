package com.backend.gns.core.notification.infrastructure.repositories;

import com.backend.gns.core.notification.domain.models.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Optional<Notification> findByTrackingId(UUID trackingId);

  List<Notification> findByTargetRoleInOrderByCreatedAtDesc(List<String> targetRoles, Pageable pageable);

  long countByTargetRoleInAndIsReadFalse(List<String> targetRoles);

  List<Notification> findByTargetRoleInAndIsReadFalse(List<String> targetRoles);
}
