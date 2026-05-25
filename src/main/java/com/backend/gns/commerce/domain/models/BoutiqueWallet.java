package com.backend.gns.commerce.domain.models;

import com.backend.gns.Shared.wallet.domain.models.Wallet;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("BOUTIQUE")
@Getter
@Setter
public class BoutiqueWallet extends Wallet {
    @OneToOne(mappedBy = "wallet")
    private Boutique boutique;
}
