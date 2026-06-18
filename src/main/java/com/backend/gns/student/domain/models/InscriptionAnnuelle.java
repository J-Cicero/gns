package com.backend.gns.student.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.enums.TypeBourse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "INSCRIPTION_ANNUELLE",
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

  @Column(length = 36, nullable = false, unique = true, updatable = false)
  private UUID trackingId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scolarite_year_id", nullable = false)
  private ScolariteYear scolariteYear;

  @Column(nullable = false)
  private boolean isFullyEnrolled = false;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private StudentNiveau studyLevel;

  @Column(nullable = false)
  private boolean isEligibleForScholarship = false;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private TypeBourse scholarshipType;

  @Column private LocalDateTime apiValidationDate;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal allocatedBudget;
}
