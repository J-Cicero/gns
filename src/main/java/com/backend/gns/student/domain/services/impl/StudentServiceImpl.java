package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.domain.enums.KycStatus;
import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.student.application.dtos.requests.StudentRequest;
import com.backend.gns.student.application.dtos.responses.StudentResponse;
import com.backend.gns.student.application.mappers.StudentMapper;
import com.backend.gns.student.domain.models.Card;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.domain.services.StudentService;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final StudentRepository studentRepository;
  private final StudentMapper studentMapper;
  private final WalletRepository walletRepository;
  private final com.backend.gns.student.infrastructure.repositories.CardRepository cardRepository;
  private final ParametreGnsService parametreGnsService;

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
    log.info("Création d'un étudiant: {} {}", request.prenom(), request.nom());

    Student student = studentMapper.toEntity(request);

    // Initialisation du Wallet via Cascade
    Wallet wallet = new Wallet();
    wallet.setTrackingId(UUID.randomUUID());
    wallet.setTypeWallet(WalletType.STUDENT);
    // Type par défaut, sera mis à jour lors de l'inscription
    wallet.setStatutWallet(WalletStatus.INACTIF);
    wallet.setSolde(BigDecimal.ZERO);

    BigDecimal plafondDefaut =
        parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.MONTANT_DEFAUT_WALLET);
    wallet.setPlafond(plafondDefaut);
    wallet.setDateCreation(LocalDateTime.now());

    student.setWallet(wallet);

    Student savedStudent = studentRepository.save(student);
    log.info(
        "Étudiant créé avec succès avec son Wallet, trackingId: {}", savedStudent.getTrackingId());
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
    student.setNom(request.nom());
    student.setPrenom(request.prenom());
    student.setTelephone(request.telephone());
    student.setDateNaissance(request.dateNaissance());
    student.setStatutKYC(request.statutKYC());

    if (request.pinCode() != null && !request.pinCode().isBlank()) {
      student.setPinCode(request.pinCode());
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
        .findByStatutKYCOrderByCreatedAtAsc(statutKYC, normalize(pageable))
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
  public boolean verifyPin(UUID studentTrackingId, String pinCode) {
    log.debug("Vérification PIN étudiant trackingId: {}", studentTrackingId);

    Student student = findStudentOrThrow(studentTrackingId);

    if (student.getPinCode() == null || pinCode == null || pinCode.isBlank()) {
      return false;
    }

    return student.getPinCode().equals(pinCode);
  }

  @Override
  public long countAll() {
    return studentRepository.count();
  }

  @Override
  public long countByEstActif(boolean active) {
    return studentRepository.countByEstActif(active);
  }

  @Override
  public long countByStatutKYC(KycStatus kycStatus) {
    return studentRepository.countByStatutKYC(kycStatus);
  }

  @Override
  public Map<String, Object> getCard(UUID studentTrackingId) {
    Student student = findStudentOrThrow(studentTrackingId);
    Page<Card> page = cardRepository.findByStudent(student, PageRequest.of(0, 1));
    if (page.hasContent()) {
      Card c = page.getContent().get(0);
      Map<String, Object> map = new HashMap<>();
      map.put("trackingId", c.getTrackingId());
      map.put("qrCodeStatique", c.getQrCodeStatique());
      map.put("statut", c.getStatut());
      map.put("dateEmission", c.getDateEmission());
      return map;
    }
    throw new ResourceNotFoundException("Carte non trouvée pour cet étudiant");
  }
}
