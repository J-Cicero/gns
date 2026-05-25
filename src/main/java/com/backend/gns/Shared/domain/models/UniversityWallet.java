package com.backend.gns.Shared.domain.models;

import com.backend.gns.Shared.wallet.domain.models.Wallet;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("UNIVERSITY")
@Getter
@Setter
public class UniversityWallet extends Wallet {
    @OneToOne(mappedBy = "wallet")
    private Universite universite;
}
