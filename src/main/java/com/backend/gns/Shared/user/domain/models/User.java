package com.backend.gns.Shared.user.domain.models;

import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.gns.Shared.user.domain.enums.TypeRole;
import com.backend.gns.Shared.utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entity User - Pure domain model (NO Spring Security coupling)
 * UserDetails adapter is handled separately in UserPrincipal
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("User")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID trackingId;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 100, nullable = false)
    private String firstName;

    @Column(length = 100, nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeRole role;

    @Column(nullable = false)
    private boolean active = false;
}
