package com.backend.gns.commerce.domain.models;

import com.backend.gns.commerce.domain.enums.TransactionStatut;
import com.backend.gns.student.domain.models.Student;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID trackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boutique_id", nullable = false)
    private Boutique boutique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scolarite_year_id", nullable = false)
    private com.backend.gns.student.domain.models.ScolariteYear scolariteYear;

    @Column(nullable = false)
    private UUID commandeId;

    @Column(nullable = false)
    private BigDecimal montantDebite;

    @Column(nullable = false)
    private BigDecimal montantNetBoutique;

    @Column(nullable = false)
    private BigDecimal commissionTotale;

    @Column(nullable = false)
    private BigDecimal commissionGns;

    @Column(nullable = false)
    private BigDecimal commissionBanque;

    @Column(nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatut statut;

    @Column(nullable = false)
    private boolean estLiquide = false;
}
