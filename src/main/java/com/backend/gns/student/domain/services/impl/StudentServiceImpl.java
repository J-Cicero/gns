package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.models.ParametreGns;
import com.backend.gns.core.parametrage.infrastructure.repositories.ParametreGnsRepository;
import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.student.application.mappers.StudentMapper;
import com.backend.gns.student.domain.models.Card;
import com.backend.gns.student.infrastructure.repositories.CardRepository;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.StudentService;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.user.infrastructure.repositories.UserRepository;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;
  private final CardRepository cardRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UniversiteRepository universiteRepository;
  private final ParametreGnsRepository parametreGnsRepository;

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  private Student findStudentOrThrow(UUID trackingId) {
    return studentRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                    () -> {
                      log.warn("Étudiant introuvable avec trackingId: {}", trackingId);
                      return new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + trackingId);
                    });
  }

  @Override
  @Transactional
  public StudentResponse create(StudentRequest request) {
    log.info("Étape 1 : Création de l'identité étudiante pour: {}", request.email());

    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("Cet email est déjà utilisé par un autre compte.");
    }
    Student student = studentMapper.toEntity(request);
    if (request.password() != null && !request.password().isEmpty()) {
      student.setPassword(passwordEncoder.encode(request.password()));
    }

    ParametreGns parametreGns = parametreGnsRepository.findByNomParametre(TypeParametreGns.MONTANT_BOURSE_MAJORATION)
            .orElseThrow( () -> new ResourceNotFoundException("Paramètre de majoration de bourse non trouvé"));
    Wallet wallet = new Wallet();
    wallet.setTrackingId(UUID.randomUUID());
    wallet.setWalletType(WalletType.STUDENT);
    wallet.setStatus(WalletStatus.INACTIF);
    wallet.setBalance(BigDecimal.ZERO);
    wallet.setLimitAmount(parametreGns.getValeurAsBigDecimal());
    wallet.setCreatedAt(LocalDateTime.now());

    student.setWallet(wallet);

    Student savedStudent = studentRepository.save(student);

    log.info("Identité étudiante créée avec succès, trackingId: {}", savedStudent.getTrackingId());
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
    Student student = findStudentOrThrow(trackingId);

    student.setEmail(request.email());
    student.setLastName(request.lastName());
    student.setFirstName(request.firstName());
    student.setPhoneNumber(request.phoneNumber());
    student.setBirthDate(request.birthDate());
    student.setBirthPlace(request.birthPlace());

    if (request.password() != null && !request.password().isBlank()) {
      student.setPassword(passwordEncoder.encode(request.password()));
    }

    // Si on veut mettre à jour l'université
    if (request.universiteTrackingId() != null) {
      student.setUniversite(universiteRepository.findByTrackingId(request.universiteTrackingId()).orElse(null));
    }

    Student updatedStudent = studentRepository.save(student);
    return studentMapper.toResponse(updatedStudent);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Student student = findStudentOrThrow(trackingId);
    studentRepository.delete(student);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StudentResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable) {
    return studentRepository
            .findByKycStatusOrderByCreatedAtAsc(statutKYC, normalize(pageable))
            .map(studentMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StudentResponse> findAll(Pageable pageable) {
    return studentRepository.findAll(normalize(pageable)).map(studentMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StudentResponse> findByUniversiteTrackingId(UUID universiteTrackingId, Pageable pageable) {
    return studentRepository
            .findByUniversiteTrackingId(universiteTrackingId, normalize(pageable))
            .map(studentMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean verifyPassword(UUID studentTrackingId, String password) {
    Student student = findStudentOrThrow(studentTrackingId);

    if (student.getPassword() == null || password == null || password.isBlank()) {
      return false;
    }
    return passwordEncoder.matches(password, student.getPassword());
  }

  @Override
  @Transactional
  public StudentResponse assignerMatricule(UUID trackingId, String studentNumber) {
    Student student = findStudentOrThrow(trackingId);
    student.setStudenNumber(studentNumber);
    return studentMapper.toResponse(studentRepository.save(student));
  }

  @Override
  public long countAll() {
    return studentRepository.count();
  }

  @Override
  public long countByEstActif(boolean active) {
    return studentRepository.countByIsActive(active);
  }

  @Override
  public long countByStatutKYC(KycStatus kycStatus) {
    return studentRepository.countByKycStatus(kycStatus);
  }

  @Override
  public Map<String, Object> getCard(UUID studentTrackingId) {
    Student student = findStudentOrThrow(studentTrackingId);
    Page<Card> page = cardRepository.findByWallet(student.getWallet(), PageRequest.of(0, 1));
    if (page.hasContent()) {
      Card c = page.getContent().get(0);
      Map<String, Object> map = new HashMap<>();
      map.put("trackingId", c.getTrackingId());
      map.put("qrCodeData", c.getQrCodeData());
      map.put("status", c.getStatus());
      map.put("emissionDate", c.getEmissionDate());
      return map;
    }
    throw new ResourceNotFoundException("Carte non trouvée pour cet étudiant");
  }
}