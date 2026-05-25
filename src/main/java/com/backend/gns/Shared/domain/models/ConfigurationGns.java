package com.backend.gns.Shared.domain.models;

import com.backend.gns.Shared.utils.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "CONFIGURATION_GNS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationGns extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 36, nullable = false, unique = true, updatable = false)
    private UUID trackingId = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    private String cle; // e.g., "COMMISSION_BANCAIRE_FIXE"

    @Column(nullable = false)
    private String valeur;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean estModifiable = false;
}
