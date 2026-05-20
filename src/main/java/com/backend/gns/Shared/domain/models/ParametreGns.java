package com.backend.gns.Shared.domain.models;

import com.backend.gns.Shared.domain.enums.TypeParametreGns;
import com.backend.gns.Shared.utils.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "PARAMETRE_GNS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParametreGns extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 36, nullable = false, unique = true, updatable = false)
    private UUID trackingId = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "nom_parametre", nullable = false, unique = true, length = 50)
    private TypeParametreGns nomParametre;

    @Column(name = "valeur_parametre", nullable = false)
    private String valeurParametre;

    @Column(nullable = false)
    private boolean estActif = true;

    @Column
    private String description;
}
