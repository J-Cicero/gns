package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import com.backend.gns.student.domain.models.BanqueEtudiant;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.Shared.wallet.domain.models.Wallet;
import com.backend.gns.student.infrastructure.repositories.BanqueEtudiantRepository;
import com.backend.gns.Shared.wallet.infrastructure.repositories.WalletRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class StudentMapper {

  private final WalletRepository walletRepository;
  private final BanqueEtudiantRepository banqueEtudiantRepository;

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
    student.setRole(request.role());
    student.setEstActif(request.estActif());
    student.setTelephone(request.telephone());
    student.setDateNaissance(request.dateNaissance());
    student.setStatutKYC(request.statutKYC());

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
        .role(student.getRole())
        .estActif(student.isEstActif())
        .telephone(student.getTelephone())
        .dateNaissance(student.getDateNaissance())
        .statutKYC(student.getStatutKYC())
        .walletTrackingId(student.getWallet() != null ? student.getWallet().getTrackingId() : null)
        .banqueEtudiantTrackingId(student.getBanqueEtudiant() != null ? student.getBanqueEtudiant().getTrackingId() : null)
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
    student.setRole(response.role());
    student.setEstActif(response.estActif());
    student.setTelephone(response.telephone());
    student.setDateNaissance(response.dateNaissance());
    student.setStatutKYC(response.statutKYC());

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

    return student;
  }
}
