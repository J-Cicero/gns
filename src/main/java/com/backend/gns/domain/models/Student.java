package com.backend.gns.domain.models;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.gns.Shared.user.domain.models.User;
import com.backend.gns.domain.enums.StudentNiveau;
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

    @Column(length = 50, nullable = false, unique = true)
    private String matriculeUL;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private StudentNiveau niveau;

    @Column(length = 100)
    private String mentionBac;

    @Column
    private Integer creditsValides;

    @Column(length = 50)
    private String RIB;

    @Column(length = 255)
    private String cheminCarteEtu;

    @Column(length = 255)
    private String cheminReleve;

    @Column
    private Boolean mandatSigne = false;

    @Column
    private LocalDate dateMandatSigne;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private KycStatus statutKYC;
}
