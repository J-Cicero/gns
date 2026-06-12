package com.backend.gns.core.domain.models;

import com.backend.gns.core.domain.enums.ProprietaireType;
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
  @JoinColumn(name = "document_rib_id", nullable = true)
  private com.backend.gns.student.domain.models.DocumentEtudiant ribDocument;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "banque_id", nullable = false)
  private Banque banque;

  @Column(length = 36, nullable = false)
  private UUID proprietaireTrackingId;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private ProprietaireType typeProprietaire;

  // --- Champs transférés de BanqueEtudiant pour les Étudiants ---
  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean isComptePrincipalBourse = false;

  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean mandatSigne = false;

  @Column
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime mandatTimestamp;

  @Column(length = 45)
  private String lieuEnregistrement;

  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  private MandatStatut mandatStatut;

  @Column
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime mandatValideLeDate;

  @Column(nullable = false)
  private boolean virementEffectue = false;

  @PrePersist
  public void prePersist() {
    if (this.trackingId == null) {
      this.trackingId = UUID.randomUUID();
    }
  }
}
