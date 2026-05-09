package com.backend.gns.domain.models;

import com.backend.gns.Shared.user.domain.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
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
}
