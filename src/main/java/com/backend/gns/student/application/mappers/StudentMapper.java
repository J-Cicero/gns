package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.models.Universite;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StudentMapper {

  private final WalletRepository walletRepository;
  private final UniversiteRepository universiteRepository;

  public Student toEntity(StudentRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("StudentRequest cannot be null");
    }

    Student student = new Student();
    student.setTrackingId(UUID.randomUUID());
    student.setEmail(request.email());
    student.setPassword(request.password());
    student.setLastName(request.lastName());
    student.setFirstName(request.firstName());
    student.setRole(UserRole.ETUDIANT);
    student.setActive(request.isActive() != null ? request.isActive() : false);
    student.setPhoneNumber(request.phoneNumber());
    student.setBirthDate(request.birthDate());
    student.setBirthPlace(request.birthPlace()); 
    student.setKycStatus(request.kycStatus());
    student.setStudentIdNumber(request.studentIdNumber());

    if (request.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Wallet not found with trackingId: " + request.walletTrackingId()));
      student.setWallet(wallet);
    }

    if (request.universiteTrackingId() != null) {
      Universite universite =
          universiteRepository
              .findByTrackingId(request.universiteTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "University not found with trackingId: " + request.universiteTrackingId()));
      student.setUniversite(universite);
    }

    return student;
  }

  public StudentResponse toResponse(Student student) {
    if (student == null) {
      return null;
    }

    return StudentResponse.builder()
        .trackingId(student.getTrackingId())
        .email(student.getEmail())
        .lastName(student.getLastName())
        .firstName(student.getFirstName())
        .isActive(student.isActive())
        .phoneNumber(student.getPhoneNumber())
        .birthDate(student.getBirthDate())
        .birthPlace(student.getBirthPlace()) // Ajouté
        .kycStatus(student.getKycStatus())
        .studentIdNumber(student.getStudentIdNumber())
        .walletTrackingId(student.getWallet() != null ? student.getWallet().getTrackingId() : null)
        .balance(student.getWallet() != null ? student.getWallet().getBalance() : BigDecimal.ZERO)
        .universiteTrackingId(
            student.getUniversite() != null ? student.getUniversite().getTrackingId() : null)
        .universiteFullName(student.getUniversite() != null ? student.getUniversite().getFullName() : null)
        .build();
  }
}
