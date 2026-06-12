package com.backend.gns.student.domain.models;

import com.backend.gns.core.domain.models.CompteBancaire;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inscription_id", nullable = true)
  private InscriptionAnnuelle inscription;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "compte_bancaire_id", nullable = true)
  private CompteBancaire compteBancaire;

  @Enumerated(EnumType.STRING)
  @Column(length = 30, nullable = false)
  private TypeDocument type;

  @Column(length = 500, nullable = false)
  private String urlFichier;

  @Column(length = 100)
  private String publicIdCloudinary;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private StatutDocument statut;

  @Column(nullable = false)
  private LocalDateTime dateDepot;
}
