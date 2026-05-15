package com.backend.gns.student.domain.models;

import com.backend.gns.Shared.utils.BaseEntity;
import com.backend.gns.student.domain.enums.StudentNiveau;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "REGLE_BOURSE_DBS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegleBourseDbs extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 36, nullable = false, unique = true, updatable = false)
    private UUID trackingId = UUID.randomUUID();

    @Column(nullable = false)
    private String libelle; // ex: "L1 Passable", "L2 30-59 crédits", "Age Max Licence"

    @Column(nullable = false, unique = true)
    private String codeUnique; // ex: "L1_PASSABLE", "AGE_MAX_LICENCE"

    @Column(precision = 10, scale = 2)
    private BigDecimal valeurNumerique; // Pour les montants ou les âges

    @Column
    private String valeurTextuelle; // Pour des conditions spécifiques si besoin

    @Column(nullable = false)
    private boolean estActif = true;

    @Column
    private String description;
}
