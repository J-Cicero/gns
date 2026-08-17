package com.backend.gns.core.notification.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "NOTIFICATION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, updatable = false)
  private UUID trackingId;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false, length = 1000)
  private String message;

  @Column(nullable = false, length = 50)
  private String targetRole; // e.g. "ADMIN_GNS", "ADMIN_BANQUE", "ALL", "COMMERCANT"

  @Column(nullable = false, length = 50)
  private String type; // e.g. "SCOLARITE_YEAR", "INSCRIPTION", "LIQUIDATION_MERCHANT", "LIQUIDATION_STUDENT"

  @Column(nullable = false)
  private boolean isRead = false;

  @Column(nullable = false)
  private LocalDateTime createdAt;
}
