package com.backend.gns.domain.models;

import java.math.BigDecimal;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PAIEMENT")
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

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private BigDecimal montantProduit;

    @Column(nullable = false)
    private BigDecimal commission;

    @Column(nullable = false)
    private BigDecimal montantDebite;

    @Column(nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaiementType typePaiement;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaiementStatut statutPaiement;
}
