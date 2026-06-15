package com.backend.gns.student.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "SCOLARITE_YEAR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScolariteYear extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 36, nullable = false, unique = true, updatable = false)
  private UUID trackingId = UUID.randomUUID();

  @Column(length = 20, nullable = false, unique = true)
  private String label; // e.g., "2025-2026"

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @Column(nullable = false)
  private boolean isOpen = true;

  @Column(nullable = false)
  private boolean isClosed = false;
}
