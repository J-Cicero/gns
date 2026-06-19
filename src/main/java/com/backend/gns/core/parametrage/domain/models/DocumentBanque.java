package com.backend.gns.core.parametrage.domain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("BANQUE")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DocumentBanque extends Document {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_bancaire_id")
    private CompteBancaire compteBancaire;
}