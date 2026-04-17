package com.backend.gns.domain.models;


import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.gns.Shared.user.domain.models.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("MERCHANT")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Merchant extends User {
    
}
