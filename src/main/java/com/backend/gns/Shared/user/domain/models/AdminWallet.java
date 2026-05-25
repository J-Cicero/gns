package com.backend.gns.Shared.user.domain.models;

import com.backend.gns.Shared.wallet.domain.models.Wallet;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
public class AdminWallet extends Wallet {
    @OneToOne(mappedBy = "wallet")
    private Admin admin;
}
