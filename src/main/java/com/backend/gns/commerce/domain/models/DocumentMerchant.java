package com.backend.gns.commerce.domain.models;

import com.backend.gns.core.parametrage.domain.models.Document;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DOCUMENT_MERCHANT")
@Getter
@Setter
@NoArgsConstructor
public class DocumentMerchant extends Document { // Extends generic Document

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "merchant_id", nullable = false)
  private Merchant merchant;
}
