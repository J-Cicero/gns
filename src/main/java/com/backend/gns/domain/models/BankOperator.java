package com.backend.gns.domain.models;

import com.backend.gns.Shared.user.domain.models.User;
import com.backend.gns.domain.enums.Banque;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "BANK_OPERATOR")
@DiscriminatorValue("BANK_OPERATOR")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BankOperator extends User {

    @Column(nullable = false, length = 100)
    private String nomBanque;

    @Column(nullable = false)
    private Banque banquePartenaire;

    @OneToOne
    @JoinColumn(name = "wallet_id", unique = true)
    private Wallet wallet;
}
