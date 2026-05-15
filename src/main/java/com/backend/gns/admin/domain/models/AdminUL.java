package com.backend.gns.admin.domain.models;

import com.backend.gns.Shared.user.domain.models.User;
import com.backend.gns.Shared.wallet.domain.models.Wallet;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DiscriminatorValue("ADMIN_UL")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AdminUL extends User {

    @Column(unique = true, nullable = false)
    private String numeroCompte;

    @OneToOne
    @JoinColumn(name = "wallet_id", unique = true)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universite_id")
    private com.backend.gns.Shared.domain.models.Universite universite;
}
