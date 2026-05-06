package com.backend.gns.domain.models;

import com.backend.gns.Shared.user.domain.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DiscriminatorValue("BANQUE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Bank extends User {

  @Column(length = 100, nullable = false, unique = true)
  private String bankName;

  @Column(length = 20, nullable = false, unique = true)
  private String bankCode;

  @Column(length = 100)
  private String bankAddress;

  @Column(length = 20)
  private String bankPhone;

  @Column(length = 100, unique = true)
  private String bankEmail;
}
