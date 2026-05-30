package com.backend.gns.student.domain.models;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.enums.StatutDocument;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "DOCUMENT_ETUDIANT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DocumentEtudiant extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 36, nullable = false, unique = true, updatable = false)
  private UUID trackingId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "inscription_id")
  private InscriptionAnnuelle inscription;

  @Enumerated(EnumType.STRING)
  @Column(length = 30, nullable = false)
  private TypeDocument type;

  @Column(length = 500, nullable = false)
  private String urlFichier;

  @Column(length = 100)
  private String publicIdCloudinary;

  @Column private Double scoreFiabilite; // Score 0-100

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private StatutDocument statut;

  @Column(length = 255)
  private String commentaireRejet;

  @Column(nullable = false)
  private LocalDateTime dateDepot;

  @Column private LocalDateTime dateValidation;

  @Column(columnDefinition = "jsonb")
  private String donneesExtraites;
}
