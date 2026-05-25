package com.backend.gns.student.domain.models;

import com.backend.gns.Shared.utils.BaseEntity;
import com.backend.gns.student.domain.enums.SourceVerification;
import com.backend.gns.student.domain.enums.StatutInscription;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.enums.TypeBourse;
import java.math.BigDecimal; 

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "INSCRIPTION_ANNUELLE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "anneeAcademique"})
})
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
    private boolean estInscritDefinitif = true; // Par défaut à true pour la V1

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private StudentNiveau niveau;

    @Column(nullable = false)
    private int creditsTotalValides;

    @Column(precision = 4, scale = 2)
    private BigDecimal moyenneBac;

    @Column(nullable = false)
    private boolean estBoursier = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeBourse typeBourse;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private StatutInscription statut;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SourceVerification source;

    @Column
    private LocalDateTime dateActivation;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal plafondAccorde;
}
