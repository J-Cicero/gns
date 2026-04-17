package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.StudentRequest;
import com.backend.gns.application.dtos.responses.StudentResponse;
import com.backend.gns.domain.models.Student;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StudentMapper {

  private final WalletRepository walletRepository;

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
    student.setCreditsValides(request.creditsValides());
    student.setRIB(request.RIB());
    student.setCNI(request.CNI());
    student.setCheminReleve(request.cheminReleve());
    student.setStatutKYC(request.statutKYC());

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
        .creditsValides(student.getCreditsValides())
        .RIB(student.getRIB())
        .verifiedCNI(true)
        .verifiedReleve(true)
        .statutKYC(student.getStatutKYC())
        .walletTrackingId(student.getWallet() != null ? student.getWallet().getTrackingId() : null)
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
    student.setCreditsValides(response.creditsValides());
    student.setRIB(response.RIB());
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

    return student;
  }
}
