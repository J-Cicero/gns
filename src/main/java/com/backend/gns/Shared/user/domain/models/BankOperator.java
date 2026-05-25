package com.backend.gns.Shared.user.domain.models;

import com.backend.gns.Shared.domain.enums.Banque;
import com.backend.gns.Shared.wallet.domain.models.Wallet;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DiscriminatorValue("BANK_OPERATOR")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BankOperator extends User {

    @Column(nullable = true, length = 100)
    private String nomBanque;

    @Column(nullable = true)
    private Banque banquePartenaire;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", unique = true)
    private Wallet wallet;
}
