package com.backend.gns.core.parametrage.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "PARAMETRE_GNS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametreGns extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID trackingId;

    private String nomParametre;
    private String valeurParametre;
    private BigDecimal valeurAsBigDecimal;
    private Integer valeurAsInteger;
    private String description;

    @jakarta.persistence.PrePersist
    public void prePersist() {
        if (this.trackingId == null) {
            this.trackingId = UUID.randomUUID();
        }
    }
}
