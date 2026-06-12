package com.backend.gns.core.parametrage.domain.models;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TypeParametreGns nomParametre;

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
