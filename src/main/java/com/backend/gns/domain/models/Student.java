package com.backend.gns.domain.models;

import com.backend.gns.Shared.user.domain.models.User;
import com.backend.gns.domain.enums.Banque;
import com.backend.gns.domain.enums.KycStatus;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ETUDIANT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Student extends User {

    @Column(length = 50, nullable = false, unique = true)
    private String numEtudiantUL;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Banque banque;

    @Column(length = 100, nullable = false, unique = true)
    private String RIB;

    @Column(nullable = false)
    private boolean mandatSigne = false;

    @Column
    private LocalDateTime mandatTimestamp;

    @Column(length = 45)
    private String mandatIpAddress;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private KycStatus statutKYC;

    @OneToOne(mappedBy = "student")
    private Wallet wallet;

    @Column(length = 255)
    private String pinCode; 
}
