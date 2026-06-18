package com.backend.gns.commerce.domain.models;

import com.backend.gns.core.parametrage.domain.models.Document;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("MERCHANT")
@Getter
@Setter
@NoArgsConstructor
public class DocumentMerchant extends Document {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "merchant_id")
  private Merchant merchant;
}