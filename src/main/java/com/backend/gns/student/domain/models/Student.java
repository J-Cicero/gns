package com.backend.gns.student.domain.models;

import com.backend.gns.user.domain.models.User;
import com.backend.gns.core.domain.enums.KycStatus;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.student.domain.models.Universite ;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@DiscriminatorValue("STUDENT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Student extends User {

    @Column(length = 50, nullable = true, unique = true)
    private String numEtudiantUniv;

    @Column(name = "pin_code", length = 60, nullable = true)
    private String pinCode;


    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = true)
    private KycStatus statutKYC;

    @OneToOne(mappedBy = "student", optional = true)
    private BanqueEtudiant banqueEtudiant;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universite_id")
    private Universite universite;

}
