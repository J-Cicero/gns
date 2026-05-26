package com.backend.gns.Shared.domain.models;

import com.backend.gns.Shared.utils.BaseEntity;
import com.backend.gns.Shared.wallet.domain.models.Wallet;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "UNIVERSITE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Universite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 36, nullable = false, unique = true, updatable = false)
    private UUID trackingId = UUID.randomUUID();

    @Column(length = 10, nullable = false, unique = true)
    private String code; 

    @Column(length = 100, nullable = false)
    private String nom;

    @Column(length = 50)
    private String ville;

    @Column(nullable = false)
    private boolean estActive = true;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
}
