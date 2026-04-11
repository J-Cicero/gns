package com.backend.gns.domain.models;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.gns.Shared.utils.BaseEntity;
import com.backend.gns.domain.enums.VersementStatut;
import com.backend.gns.domain.enums.VersementType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "versement")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Versement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID trackingId;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private Double montantVerse;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private VersementType typeVersement;

    @Column
    private LocalDate datePrevue;

    @Column
    private LocalDate dateEffective;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private VersementStatut statut;
}
