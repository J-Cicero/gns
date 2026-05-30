package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.student.domain.models.Universite;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.student.infrastructure.repositories.BanqueEtudiantRepository;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import java.util.UUID;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class StudentMapper {

  private final WalletRepository walletRepository;
  private final BanqueEtudiantRepository banqueEtudiantRepository;
  private final UniversiteRepository universiteRepository;

  public Student toEntity(StudentRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("La requête StudentRequest ne peut pas être nulle");
    }

    Student student = new Student();
    student.setTrackingId(UUID.randomUUID());
    student.setEmail(request.email());
    student.setPassword(request.password());
    student.setNom(request.nom());
    student.setPrenom(request.prenom());
    student.setRole(UserRole.ETUDIANT);
    student.setEstActif(request.estActif());
    student.setTelephone(request.telephone());
    student.setDateNaissance(request.dateNaissance());
    student.setStatutKYC(request.statutKYC());
    student.setNumEtudiantUniv(request.numEtudiantUniv());

    if (request.pinCode() != null && !request.pinCode().isEmpty()) {
      student.setPinCode(request.pinCode());
    }

    if (request.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
      student.setWallet(wallet);
    }

     if (request.banqueEtudiantTrackingId() != null) {
      BanqueEtudiant banqueEtudiant =
          banqueEtudiantRepository
              .findByTrackingId(request.banqueEtudiantTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Portefeuille non trouvé avec l'ID: " + request.banqueEtudiantTrackingId()));
      student.setBanqueEtudiant(banqueEtudiant);
    }

    if (request.universiteTrackingId() != null) {
        Universite universite = universiteRepository.findByTrackingId(request.universiteTrackingId())
            .orElseThrow(() -> new IllegalArgumentException("Université non trouvée avec l'ID: " + request.universiteTrackingId()));
        student.setUniversite(universite);
    }

    return student;
  }

  public StudentResponse toResponse(Student student) {
    if (student == null) {
      throw new IllegalArgumentException("L'entité Student ne peut pas être nulle");
    }

    return StudentResponse.builder()
        .trackingId(student.getTrackingId())
        .email(student.getEmail())
        .nom(student.getNom())
        .prenom(student.getPrenom())
        .estActif(student.isEstActif())
        .telephone(student.getTelephone())
        .dateNaissance(student.getDateNaissance())
        .statutKYC(student.getStatutKYC())
        .numEtudiantUniv(student.getNumEtudiantUniv())
        .walletTrackingId(student.getWallet() != null ? student.getWallet().getTrackingId() : null)
        .solde(student.getWallet() != null ? student.getWallet().getSolde() : BigDecimal.ZERO)
        .banqueEtudiantTrackingId(student.getBanqueEtudiant() != null ? student.getBanqueEtudiant().getTrackingId() : null)
        .universiteTrackingId(student.getUniversite() != null ? student.getUniversite().getTrackingId() : null)
        .universiteNom(student.getUniversite() != null ? student.getUniversite().getNom() : null)
        .pinCode(student.getPinCode())
        .build();
  }

  public Student toEntityFromResponse(StudentResponse response) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse StudentResponse ne peut pas être nulle");
    }

    Student student = new Student();
    student.setTrackingId(response.trackingId());
    student.setEmail(response.email());
    student.setNom(response.nom());
    student.setPrenom(response.prenom());
    student.setRole(UserRole.ETUDIANT);
    student.setEstActif(response.estActif());
    student.setTelephone(response.telephone());
    student.setDateNaissance(response.dateNaissance());
    student.setStatutKYC(response.statutKYC());
    student.setNumEtudiantUniv(response.numEtudiantUniv());

    if (response.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(response.walletTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Portefeuille non trouvé avec l'ID: " + response.walletTrackingId()));
      student.setWallet(wallet);
    }

      if (response.banqueEtudiantTrackingId() != null) {
        BanqueEtudiant banqueEtudiant =
            banqueEtudiantRepository
                .findByTrackingId(response.banqueEtudiantTrackingId())
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "Portefeuille non trouvé avec l'ID: " + response.banqueEtudiantTrackingId()));
        student.setBanqueEtudiant(banqueEtudiant);
      }
      
      if (response.universiteTrackingId() != null) {
          Universite universite = universiteRepository.findByTrackingId(response.universiteTrackingId())
              .orElseThrow(() -> new IllegalArgumentException("Université non trouvée avec l'ID: " + response.universiteTrackingId()));
          student.setUniversite(universite);
      }

    return student;
  }
}
