package com.backend.gns.core.domain.models;

import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.enums.MandatStatut;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "COMPTE_BANCAIRE")
public class CompteBancaire extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 36, nullable = false, unique = true, updatable = false)
  private UUID trackingId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "banque_id", nullable = false)
  private Banque bank;

  @Column(nullable = false)
  private UUID ownerTrackingId;

  @Column
  private UUID ribDocumentTrackingId;

  @Column(length = 50, nullable = false)
  private String accountNumber;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private ProprietaireType ownerType;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean isMainScholarshipAccount = false;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean mandateSigned = false;

  @Column
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime mandateTimestamp;

  @Column(length = 45)
  private String registrationPlace;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  private MandatStatut mandateStatus;

  @Column
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime mandateValidatedAt;

  @Column(nullable = false)
  private boolean transferCompleted = false;

  @PrePersist
  public void prePersist() {
    if (this.trackingId == null) {
      this.trackingId = UUID.randomUUID();
    }
  }
}
