package com.backend.gns.student.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.student.domain.enums.TypeParametreDbs;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "PARAMETRE_DBS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParametreDbs extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 36, nullable = false, unique = true, updatable = false)
    private UUID trackingId = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "nom_parametre", nullable = false, unique = true, length = 50)
    private TypeParametreDbs nomParametre;

    @Column(name = "valeur_parametre", nullable = false)
    private String valeurParametre;

    @Column(nullable = false)
    private boolean estActif = true;

    @Column
    private String description;
}
