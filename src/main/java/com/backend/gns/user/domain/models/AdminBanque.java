package com.backend.gns.user.domain.models;

import com.backend.gns.core.parametrage.domain.models.Banque;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@DiscriminatorValue("ADMIN_BANQUE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AdminBanque extends User {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "banque_id")
  private Banque banque;
}
