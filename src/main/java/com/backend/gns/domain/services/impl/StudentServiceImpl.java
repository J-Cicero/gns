package com.backend.gns.domain.services.impl;

import com.backend.gns.application.dtos.requests.StudentRequest;
import com.backend.gns.application.dtos.requests.WalletRequest;
import com.backend.gns.application.dtos.responses.StudentResponse;
import com.backend.gns.application.mappers.StudentMapper;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.domain.enums.WalletStatus;
import com.backend.gns.domain.enums.WalletType;
import com.backend.gns.domain.models.Student;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.domain.services.StudentService;
import com.backend.gns.domain.services.WalletService;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import com.backend.gns.infrastructure.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;
  private final WalletRepository walletRepository;
  private final WalletService walletService;

  public StudentServiceImpl(
      StudentRepository studentRepository,
      StudentMapper studentMapper,
      WalletRepository walletRepository,
      WalletService walletService) {
    this.studentRepository = studentRepository;
    this.studentMapper = studentMapper;
    this.walletRepository = walletRepository;
    this.walletService = walletService;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public StudentResponse create(StudentRequest request) {
    Student student = studentMapper.toEntity(request);
    Student savedStudent = studentRepository.save(student);

    // Créer un Wallet pour l'étudiant avec type BOURSE_DBS_36k
    WalletRequest walletRequest =
        WalletRequest.builder()
            .typeWallet(WalletType.BOURSE_DBS_36k)
            .statutWallet(WalletStatus.ACTIF)
            .solde(BigDecimal.ZERO)
            .plafond(BigDecimal.ZERO)
            .estVerrouille(false)
            .dateCreation(LocalDateTime.now())
            .build();
    walletService.create(walletRequest);

    // Récupérer le dernier wallet créé et l'associer à l'étudiant
    var wallets = walletRepository.findByTypeWallet(WalletType.BOURSE_DBS_36k, Pageable.unpaged());
    if (wallets.hasContent()) {
      Wallet wallet = wallets.getContent().get(wallets.getContent().size() - 1);
      savedStudent.setWallet(wallet);
      studentRepository.save(savedStudent);
    }

    return studentMapper.toResponse(savedStudent);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<StudentResponse> findByTrackingId(UUID trackingId) {
    return studentRepository.findByTrackingId(trackingId).map(studentMapper::toResponse);
  }

  @Override
  @Transactional
  public StudentResponse update(UUID trackingId, StudentRequest request) {
    Student student =
        studentRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Étudiant non trouvé avec l'ID: " + trackingId));

    student.setEmail(request.email());
    student.setNom(request.nom());
    student.setPrenom(request.prenom());
    student.setTelephone(request.telephone());
    student.setDateNaissance(request.dateNaissance());
    student.setRIB(request.RIB());
    student.setStatutKYC(request.statutKYC());

    // TODO: PIN code encoding - requires Spring Security
    if (request.pinCode() != null && !request.pinCode().isEmpty()) {
      student.setPinCode(request.pinCode());
    }

    if (request.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
      student.setWallet(wallet);
    }

    Student updatedStudent = studentRepository.save(student);
    return studentMapper.toResponse(updatedStudent);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Student student =
        studentRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Étudiant non trouvé avec l'ID: " + trackingId));
    studentRepository.delete(student);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StudentResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable) {
    return studentRepository
        .findByStatutKYCOrderByCreatedAtAsc(statutKYC, normalize(pageable))
        .map(studentMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StudentResponse> findAll(Pageable pageable) {
    return studentRepository.findAll(normalize(pageable)).map(studentMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean verifyPin(UUID studentTrackingId, String pinCode) {
    // TODO: PIN verification - requires Spring Security PasswordEncoder
    Student student =
        studentRepository
            .findByTrackingId(studentTrackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException("Étudiant non trouvé avec l'ID: " + studentTrackingId));

    if (student.getPinCode() == null || pinCode == null) {
      return false;
    }

    // Temporary: direct string comparison (not secure, for now)
    return student.getPinCode().equals(pinCode);
  }
}
