package com.backend.gns.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.gns.Shared.utils.BaseEntity;
import com.backend.gns.domain.enums.PaiementStatut;
import com.backend.gns.domain.enums.PaiementType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "paiement")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Paiement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID trackingId;

    @Column(nullable = false)
    private UUID commandeTrackingId;

    @Column(nullable = false)
    private UUID walletTrackingId;

    @Column(nullable = false)
    private Double montantProduit;

    @Column(nullable = false)
    private Double commission;

    @Column(nullable = false)
    private Double montantDebite;

    @Column
    private LocalDateTime dateTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaiementType typePaiement;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaiementStatut statutPaiement;

    @Column
    private Boolean estSwitch = false;

    @Column(length = 36)
    private String commandeRef;
}
