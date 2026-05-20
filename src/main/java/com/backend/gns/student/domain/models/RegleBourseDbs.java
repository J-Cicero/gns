package com.backend.gns.student.domain.models;

import com.backend.gns.Shared.utils.BaseEntity;
import com.backend.gns.student.domain.enums.TypeRegleBourse;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type_regle", nullable = false, unique = true, length = 50)
    private TypeRegleBourse typeRegle;

    @Column(name = "valeur_critere", precision = 10, scale = 2, nullable = false)
    private BigDecimal valeurCritere;

    @Column(nullable = false)
    private boolean estActif = true;

    @Column
    private String description;
}
