package com.backend.gns.Shared.domain.models;

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

    @Column(nullable = false, unique = true)
    private String cle; // ex: "TAUX_COMMISSION_PAIEMENT", "FRAIS_REVIENT_CARTE"

    @Column(nullable = false)
    private String valeur;

    @Column(nullable = false)
    private boolean estActif = true;

    @Column
    private String description;
}
