package com.backend.gns.wallet.application.mappers;

import com.backend.gns.wallet.application.dtos.requests.WalletRequest;
import com.backend.gns.wallet.application.dtos.responses.WalletResponse;
import com.backend.gns.wallet.domain.models.Wallet;
import java.time.LocalDateTime;
import java.util.UUID;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

  private final StudentRepository studentRepository;
  private final BoutiqueRepository boutiqueRepository;
  private final UniversiteRepository universiteRepository;

  public WalletMapper(StudentRepository studentRepository, BoutiqueRepository boutiqueRepository, UniversiteRepository universiteRepository) {
    this.studentRepository = studentRepository;
    this.boutiqueRepository = boutiqueRepository;
    this.universiteRepository = universiteRepository;
  }

  public Wallet toEntity(WalletRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("La requête WalletRequest ne peut pas être nulle");
    }

    Wallet wallet = new Wallet();
    wallet.setTrackingId(UUID.randomUUID());
    wallet.setTypeWallet(request.typeWallet());
    wallet.setStatutWallet(request.statutWallet());
    wallet.setSolde(request.solde());
    wallet.setPlafond(request.plafond());
    wallet.setDateCreation(
        request.dateCreation() != null ? request.dateCreation() : LocalDateTime.now());

    return wallet;
  }

  public WalletResponse toResponse(Wallet wallet) {
    if (wallet == null) {
      throw new IllegalArgumentException("L'entité Wallet ne peut pas être nulle");
    }

    String ownerName = "Inconnu";
    if (wallet.getTypeWallet() != null) {
      switch (wallet.getTypeWallet()) {
        case STUDENT -> ownerName = studentRepository.findByWalletTrackingId(wallet.getTrackingId())
            .map(s -> s.getNom() + " " + s.getPrenom())
            .orElse("Inconnu");
        case BOUTIQUE -> ownerName = boutiqueRepository.findByWalletTrackingId(wallet.getTrackingId())
            .map(b -> b.getNomBoutique())
            .orElse("Inconnu");
        case UNIVERSITY -> ownerName = universiteRepository.findByWalletTrackingId(wallet.getTrackingId())
            .map(u -> u.getNom())
            .orElse("Inconnu");
      }
    }

    return WalletResponse.builder()
        .trackingId(wallet.getTrackingId())
        .typeWallet(wallet.getTypeWallet())
        .statutWallet(wallet.getStatutWallet())
        .niveauSolde(wallet.getNiveauSolde())
        .solde(wallet.getSolde())
        .plafond(wallet.getPlafond())
        .dateCreation(wallet.getDateCreation())
        .ownerName(ownerName)
        .build();
  }

  public Wallet toEntityFromResponse(WalletResponse response) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse WalletResponse ne peut pas être nulle");
    }

    Wallet wallet = new Wallet();
    wallet.setTrackingId(response.trackingId());
    wallet.setTypeWallet(response.typeWallet());
    wallet.setStatutWallet(response.statutWallet());
    wallet.setNiveauSolde(response.niveauSolde());
    wallet.setSolde(response.solde());
    wallet.setPlafond(response.plafond());
    wallet.setDateCreation(response.dateCreation());

    return wallet;
  }
}
