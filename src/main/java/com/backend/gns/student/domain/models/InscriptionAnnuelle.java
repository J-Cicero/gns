package com.backend.gns.student.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.enums.TypeBourse;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "INSCRIPTION_ANNUELLE",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "scolarite_year_id"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class InscriptionAnnuelle extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Initialisation automatique du UUID si non fourni
  @Column(length = 36, nullable = false, unique = true, updatable = false)
  private UUID trackingId = UUID.randomUUID();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scolarite_year_id", nullable = false)
  private ScolariteYear scolariteYear;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private com.backend.gns.student.domain.enums.StatutInscription status = com.backend.gns.student.domain.enums.StatutInscription.EN_ATTENTE;

  @Column(length = 255)
  private String rejectionReason;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private StudentNiveau studyLevel;

  @Column(nullable = false)
  private boolean isEligibleForScholarship = false;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private TypeBourse scholarshipType;

  @Column
  private LocalDateTime apiValidationDate;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal allocatedBudget;
}