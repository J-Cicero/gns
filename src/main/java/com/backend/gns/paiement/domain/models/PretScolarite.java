package com.backend.gns.paiement.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.academique.domain.models.Universite;
import com.backend.gns.student.domain.models.ScolariteYear;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "PRET_SCOLARITE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PretScolarite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 36, nullable = false, unique = true, updatable = false)
    private UUID trackingId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universite_id", nullable = false)
    private Universite universite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scolarite_year_id", nullable = false)
    private ScolariteYear scolariteYear;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false)
    private boolean estRembourse = false;

    @Column(length = 255)
    private String description;
}
