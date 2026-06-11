package com.backend.gns.user.domain.models;

import com.backend.gns.core.utils.BaseEntity;
import com.backend.gns.user.domain.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "USERS")
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

  @Column(length = 100, nullable = false)
  private String nom;

  @Column(length = 100, nullable = false)
  private String prenom;

  @Column(length = 100, nullable = false, unique = true)
  private String email;

  @JsonIgnore
  @Column(length = 255, nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private UserRole role;

  @Column(nullable = false)
  private boolean estActif = false;

  @Column(length = 20)
  private String telephone;

  @Column(length = 50)
  private String pays;

  @Column(nullable = false, updatable = false)
  @CreatedDate
  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime dateInscription;

  @Column(nullable = true)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime dateNaissance;

  public void setMotDePasse(String motDePasse) {
    this.password = motDePasse;
  }
}
