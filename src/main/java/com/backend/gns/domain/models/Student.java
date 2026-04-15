package com.backend.gns.domain.models;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.gns.Shared.user.domain.models.User;
import com.backend.gns.domain.enums.KycStatus;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ETUDIANT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Student extends User {

    @Column
    private int creditsValides;

    @Column(length = 50, nullable = false, unique = true)
    private String RIB;

    @Column(length = 255, nullable = false, unique = true)
    private String CNI;

    @Column(length = 255 , nullable = false)
    private String cheminReleve;

    @Enumerated(EnumType.STRING)
    @Column(length = 20 , nullable = false)
    private KycStatus statutKYC;
}
