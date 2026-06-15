package com.backend.gns.student.domain.models;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.enums.StatutDocument;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DOCUMENT_ETUDIANT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEtudiant extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, updatable = false)
  private UUID trackingId;

  @Column(nullable = false)
  private UUID ownerTrackingId;

  @Enumerated(EnumType.STRING)
  @Column(length = 30, nullable = false)
  private TypeDocument documentType;

  @Column(length = 500, nullable = false)
  private String fileUrl;

  @Column(length = 100)
  private String providerPublicId;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private StatutDocument status;

  @Column(nullable = false)
  private LocalDateTime uploadedAt;
}
