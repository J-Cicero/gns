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
@DiscriminatorValue("MERCHANT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Merchant extends User {

    @Column(length = 100, nullable = false)
    private String nomBoutique;

    @Column(length = 100)
    private String categorieShop;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private KycStatus statutKYC;

}
