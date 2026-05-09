package com.backend.gns.domain.models;

import com.backend.gns.Shared.user.domain.models.User;
import com.backend.gns.domain.enums.KycStatus;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DiscriminatorValue("STUDENT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Student extends User {

    @Column(length = 50, nullable = false, unique = true)
    private String numEtudiantUL;

    @Column(name = "pin_code", length = 60, nullable = false)
    private String pinCode;


    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private KycStatus statutKYC;

    @OneToOne(mappedBy = "student", optional = true)
    private BanqueEtudiant banqueEtudiant;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

}
