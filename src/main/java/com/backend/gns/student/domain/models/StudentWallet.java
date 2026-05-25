package com.backend.gns.student.domain.models;

import com.backend.gns.Shared.wallet.domain.models.Wallet;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("STUDENT")
@Getter
@Setter
public class StudentWallet extends Wallet {
    @OneToOne(mappedBy = "wallet")
    private Student student;
}
