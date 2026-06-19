package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.core.parametrage.domain.models.CompteBancaire;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.services.impl.CloudinaryStorageService;
import com.backend.gns.core.parametrage.infrastructure.repositories.BanqueRepository;
import com.backend.gns.core.parametrage.infrastructure.repositories.CompteBancaireRepository;
import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import org.springframework.web.multipart.MultipartFile;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.core.parametrage.domain.models.Wallet;
import com.backend.gns.student.application.mappers.StudentMapper;
import com.backend.gns.student.domain.models.Card;
import com.backend.gns.student.infrastructure.repositories.CardRepository;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.StudentService;
import com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.user.infrastructure.repositories.UserRepository;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
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
  private final WalletRepository walletRepository;
  private final CardRepository cardRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UniversiteRepository universiteRepository;
  private final BanqueRepository banqueRepository;
  private final CompteBancaireRepository compteBancaireRepository;
  private final DocumentEtudiantRepository documentEtudiantRepository;
  private final CloudinaryStorageService storageService;

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
  public StudentResponse create(StudentRequest request, MultipartFile rib, MultipartFile mandat) {
    log.info("Inscription étudiant complète pour: {}", request.email());

    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("Cet email est déjà utilisé par un autre compte.");
    }

    Student student = studentMapper.toEntity(request);
    student.setTrackingId(UUID.randomUUID());
    student.setRole(UserRole.ETUDIANT);
    student.setActive(true);
    student.setKycStatus(KycStatus.EN_ATTENTE);
    
    if (request.password() != null && !request.password().isEmpty()) {
      student.setPasswordHash(passwordEncoder.encode(request.password()));
    }

    Wallet wallet = new Wallet();
    wallet.setTrackingId(UUID.randomUUID());
    wallet.setWalletType(WalletType.STUDENT);
    wallet.setStatus(WalletStatus.INACTIF);
    wallet.setBalance(BigDecimal.ZERO);
    wallet.setLimitAmount(BigDecimal.valueOf(30000.0));   
    wallet.setCreatedAt(LocalDateTime.now());
    student.setWallet(wallet);

    if (request.universiteTrackingId() != null) {
      student.setUniversite(universiteRepository.findByTrackingId(request.universiteTrackingId()).orElse(null));
    }

    Student savedStudent = studentRepository.save(student);

    try {
      if (rib != null) {
        var ribUpload = storageService.upload(rib, "rib_" + savedStudent.getTrackingId());
        var docRib = new DocumentEtudiant();
        docRib.setTrackingId(UUID.randomUUID());
        docRib.setDocumentType(TypeDocument.RIB);
        docRib.setFileUrl((String) ribUpload.get("url"));
        docRib.setProviderPublicId((String) ribUpload.get("publicId"));
        docRib.setStatus(StatutDocument.EN_ATTENTE);
        docRib.setUploadedAt(LocalDateTime.now());
        documentEtudiantRepository.save(docRib);

        CompteBancaire cb = new CompteBancaire();
        cb.setTrackingId(UUID.randomUUID());
        cb.setOwnerTrackingId(savedStudent.getTrackingId());
        cb.setOwnerType(ProprietaireType.STUDENT);
        cb.setAccountNumber(request.accountNumber());
        cb.setMainScholarshipAccount(true);

        if (request.bankTrackingId() != null) {
          cb.setBank(banqueRepository.findByTrackingId(request.bankTrackingId()).orElse(null));
        }
        compteBancaireRepository.save(cb);
      }

      if (mandat != null) {
        var mandatUpload = storageService.upload(mandat, "mandat_" + savedStudent.getTrackingId());
        var docMandat = new DocumentEtudiant();
        docMandat.setTrackingId(UUID.randomUUID());
        docMandat.setDocumentType(TypeDocument.MANDAT);
        docMandat.setFileUrl((String) mandatUpload.get("url"));
        docMandat.setProviderPublicId((String) mandatUpload.get("publicId"));
        docMandat.setStatus(StatutDocument.EN_ATTENTE);
        docMandat.setUploadedAt(LocalDateTime.now());
        documentEtudiantRepository.save(docMandat);
      }
    } catch (Exception e) {
      throw new RuntimeException("Erreur lors de l'upload des documents bancaires", e);
    }

    log.info("Étudiant créé avec succès, trackingId: {}", savedStudent.getTrackingId());
    return studentMapper.toResponse(savedStudent);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<StudentResponse> findByTrackingId(UUID trackingId) {
    log.debug("Recherche étudiant par trackingId: {}", trackingId);
    return studentRepository.findByTrackingId(trackingId).map(studentMapper::toResponse);
  }

  @Override
  @Transactional
  public StudentResponse update(UUID trackingId, StudentRequest request) {
    log.info("Mise à jour étudiant trackingId: {}", trackingId);

    Student student = findStudentOrThrow(trackingId);

    student.setEmail(request.email());
    student.setLastName(request.lastName());
    student.setFirstName(request.firstName());
    student.setPhoneNumber(request.phoneNumber());
    student.setBirthDate(request.birthDate());
    student.setKycStatus(request.kycStatus());

    if (request.password() != null && !request.password().isBlank()) {
      student.setPasswordHash(passwordEncoder.encode(request.password()));
    }

    if (request.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
      student.setWallet(wallet);
    }

    Student updatedStudent = studentRepository.save(student);
    log.info("Étudiant mis à jour avec succès, trackingId: {}", trackingId);
    return studentMapper.toResponse(updatedStudent);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    log.info("Suppression étudiant trackingId: {}", trackingId);
    Student student = findStudentOrThrow(trackingId);
    studentRepository.delete(student);
    log.info("Étudiant supprimé avec succès, trackingId: {}", trackingId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StudentResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable) {
    log.debug("Recherche étudiants par KYC status: {}", statutKYC);
    return studentRepository
        .findByKycStatusOrderByCreatedAtAsc(statutKYC, normalize(pageable))
        .map(studentMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StudentResponse> findAll(Pageable pageable) {
    log.debug("Récupération de tous les étudiants, page: {}", pageable.getPageNumber());
    return studentRepository.findAll(normalize(pageable)).map(studentMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<StudentResponse> findByUniversiteTrackingId(
      UUID universiteTrackingId, Pageable pageable) {
    log.debug("Recherche étudiants par université trackingId: {}", universiteTrackingId);
    return studentRepository
        .findByUniversiteTrackingId(universiteTrackingId, normalize(pageable))
        .map(studentMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean verifyPassword(UUID studentTrackingId, String password) {
    log.debug("Vérification mot de passe étudiant trackingId: {}", studentTrackingId);

    Student student = findStudentOrThrow(studentTrackingId);

    if (student.getPasswordHash() == null || password == null || password.isBlank()) {
      return false;
    }

    return passwordEncoder.matches(password, student.getPasswordHash());
  }

  @Override
  @Transactional
  public StudentResponse assignerMatricule(UUID trackingId, String matricule) {
    log.info("Assignation du matricule {} à l'étudiant {}", matricule, trackingId);
    Student student = findStudentOrThrow(trackingId);
    student.setStudentIdNumber(matricule);
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
