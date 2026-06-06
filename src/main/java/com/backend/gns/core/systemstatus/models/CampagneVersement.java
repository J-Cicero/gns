package com.backend.gns.core.systemstatus.models;

import com.backend.gns.user.domain.models.User;
import com.backend.gns.core.systemstatus.enums.CampagneStatus;
import com.backend.gns.core.utils.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class CampagneVersement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 36, nullable = false, unique = true, updatable = false)
    private UUID trackingId;

    @Column(nullable = false, unique = true)
    private String nomVague; // Ex: "VAGUE_1"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampagneStatus statut;

    @Column(nullable = false)
    private LocalDateTime dateInitiation;

    @ManyToOne
    @JoinColumn(name = "initiateur_id", nullable = false)
    private User initiateur; 
}
