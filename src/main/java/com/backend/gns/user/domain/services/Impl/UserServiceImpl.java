package com.backend.gns.user.domain.services.Impl;

import com.backend.gns.core.infrastructure.repositories.BanqueRepository;
import com.backend.gns.core.security.jwt.JwtService;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.user.application.dtos.requests.LoginRequest;
import com.backend.gns.user.application.dtos.requests.UserRequest;
import com.backend.gns.user.application.dtos.responses.LoginResponse;
import com.backend.gns.user.application.dtos.responses.UserResponse;
import com.backend.gns.user.application.mappers.UserMapper;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.user.domain.exception.ResourceNotFoundException;
import com.backend.gns.user.domain.models.User;
import com.backend.gns.user.domain.models.AdminBanque;
import com.backend.gns.user.application.dtos.requests.AdminBanqueRequest;
import com.backend.gns.core.domain.models.Banque;
import com.backend.gns.user.domain.services.UserService;
import com.backend.gns.user.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final UniversiteRepository universiteRepository;
  private final BanqueRepository banqueRepository;
  private final com.backend.gns.core.infrastructure.repositories.CompteBancaireRepository compteBancaireRepository;
  private final com.backend.gns.student.infrastructure.repositories.StudentRepository studentRepository;
  private final com.backend.gns.student.infrastructure.repositories.DocumentEtudiantRepository documentEtudiantRepository;
  private final com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository boutiqueRepository;
  private final com.backend.gns.core.storage.CloudinaryStorageService storageService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserResponse registerStudent(com.backend.gns.student.application.dtos.requests.StudentRequest request, org.springframework.web.multipart.MultipartFile rib, org.springframework.web.multipart.MultipartFile mandat) {
      log.info("Inscription étudiant complète pour: {}", request.email());
      
      if (userRepository.findByEmail(request.email()).isPresent()) {
          throw new IllegalArgumentException("Cet email est déjà utilisé par un autre compte.");
      }
      
      com.backend.gns.student.domain.models.Student student = new com.backend.gns.student.domain.models.Student();
      student.setTrackingId(UUID.randomUUID());
      student.setLastName(request.lastName());
      student.setFirstName(request.firstName());
      student.setEmail(request.email());
      student.setPhoneNumber(request.phoneNumber());
      student.setRole(UserRole.ETUDIANT);
      student.setActive(true);
      student.setPasswordHash(passwordEncoder.encode(request.password()));
      student.setStudentIdNumber(request.studentIdNumber());
      student.setKycStatus(com.backend.gns.core.domain.enums.KycStatus.EN_ATTENTE);
      student.setBirthDate(request.birthDate()); // Ajouté
      student.setBirthPlace(request.birthPlace()); // Ajouté

      com.backend.gns.wallet.domain.models.Wallet wallet = new com.backend.gns.wallet.domain.models.Wallet();
      wallet.setTrackingId(UUID.randomUUID());
      wallet.setWalletType(com.backend.gns.wallet.domain.enums.WalletType.STUDENT);
      wallet.setStatus(com.backend.gns.wallet.domain.enums.WalletStatus.INACTIF);
      wallet.setBalance(java.math.BigDecimal.ZERO);
      wallet.setLimitAmount(java.math.BigDecimal.ZERO);
      wallet.setCreatedAt(java.time.LocalDateTime.now());
      student.setWallet(wallet);
      
      if (request.universiteTrackingId() != null) {
          student.setUniversite(universiteRepository.findByTrackingId(request.universiteTrackingId()).orElse(null));
      }
      
      com.backend.gns.student.domain.models.Student savedStudent = studentRepository.save(student);
      
      try {
          if (rib != null) {
              var ribUpload = storageService.upload(rib, "rib_" + savedStudent.getTrackingId());
              var docRib = com.backend.gns.student.domain.models.DocumentEtudiant.builder()
                  .trackingId(UUID.randomUUID())
                  .ownerTrackingId(savedStudent.getTrackingId())
                  .documentType(com.backend.gns.core.domain.enums.TypeDocument.RIB)
                  .fileUrl(ribUpload.get("url"))
                  .status(com.backend.gns.student.domain.enums.StatutDocument.EN_ATTENTE)
                  .uploadedAt(java.time.LocalDateTime.now())
                  .build();
              var savedDocRib = documentEtudiantRepository.save(docRib);
              
              com.backend.gns.core.domain.models.CompteBancaire cb = new com.backend.gns.core.domain.models.CompteBancaire();
              cb.setTrackingId(UUID.randomUUID());
              cb.setOwnerTrackingId(savedStudent.getTrackingId());
              cb.setOwnerType(com.backend.gns.core.domain.enums.ProprietaireType.STUDENT);
              cb.setAccountNumber(request.accountNumber());
              cb.setMainScholarshipAccount(true);
              
              if (request.bankTrackingId() != null) {
                  cb.setBank(banqueRepository.findByTrackingId(request.bankTrackingId()).orElse(null));
              }
              compteBancaireRepository.save(cb);
          }
          
          if (mandat != null) {
              var mandatUpload = storageService.upload(mandat, "mandat_" + savedStudent.getTrackingId());
              var docMandat = com.backend.gns.student.domain.models.DocumentEtudiant.builder()
                  .trackingId(UUID.randomUUID())
                  .ownerTrackingId(savedStudent.getTrackingId())
                  .documentType(com.backend.gns.core.domain.enums.TypeDocument.MANDAT)
                  .fileUrl(mandatUpload.get("url"))
                  .status(com.backend.gns.student.domain.enums.StatutDocument.EN_ATTENTE)
                  .uploadedAt(java.time.LocalDateTime.now())
                  .build();
              documentEtudiantRepository.save(docMandat);
          }
      } catch (Exception e) {
          throw new RuntimeException("Erreur lors de l'upload des documents bancaires", e);
      }
      
      return userMapper.toResponse(savedStudent);
  }

  @Override
  @Transactional
  public UserResponse registerMerchant(com.backend.gns.commerce.application.dtos.requests.MerchantRequest request, org.springframework.web.multipart.MultipartFile rib) {
      log.info("Inscription commerçant complète pour: {}", request.email());

      if (userRepository.findByEmail(request.email()).isPresent()) {
          throw new IllegalArgumentException("Cet email est déjà utilisé par un autre compte.");
      }
      
      com.backend.gns.commerce.domain.models.Merchant merchant = new com.backend.gns.commerce.domain.models.Merchant();
      merchant.setTrackingId(UUID.randomUUID());
      merchant.setLastName(request.lastName());
      merchant.setFirstName(request.firstName());
      merchant.setEmail(request.email());
      merchant.setPhoneNumber(request.phoneNumber());
      merchant.setRole(UserRole.COMMERCANT);
      merchant.setActive(true);
      merchant.setPasswordHash(passwordEncoder.encode(request.password()));
      merchant.setBusinessName(request.businessName());
      merchant.setRegistrationNumber(request.registrationNumber());
      
      com.backend.gns.commerce.domain.models.Merchant savedMerchantUser = userRepository.save(merchant);
      
      com.backend.gns.commerce.domain.models.Boutique boutique = new com.backend.gns.commerce.domain.models.Boutique();
      boutique.setTrackingId(UUID.randomUUID());
      boutique.setName(request.businessName());
      boutique.setDescription(request.description() != null ? request.description() : "N/A");
      boutique.setMerchant(savedMerchantUser);
      boutique.setKycStatus(com.backend.gns.core.domain.enums.KycStatus.EN_ATTENTE);

      com.backend.gns.wallet.domain.models.Wallet wallet = new com.backend.gns.wallet.domain.models.Wallet();
      wallet.setTrackingId(UUID.randomUUID());
      wallet.setWalletType(com.backend.gns.wallet.domain.enums.WalletType.BOUTIQUE);
      wallet.setStatus(com.backend.gns.wallet.domain.enums.WalletStatus.ACTIF);
      wallet.setBalance(java.math.BigDecimal.ZERO);
      wallet.setLimitAmount(java.math.BigDecimal.ZERO); 
      wallet.setCreatedAt(java.time.LocalDateTime.now());
      boutique.setWallet(wallet);

      boutiqueRepository.save(boutique);
      
      if (rib != null) {
          try {
              var ribUpload = storageService.upload(rib, "rib_merchant_" + savedMerchantUser.getTrackingId());
              
              com.backend.gns.core.domain.models.CompteBancaire cb = new com.backend.gns.core.domain.models.CompteBancaire();
              cb.setTrackingId(UUID.randomUUID());
              cb.setOwnerTrackingId(savedMerchantUser.getTrackingId());
              cb.setOwnerType(com.backend.gns.core.domain.enums.ProprietaireType.MERCHANT);
              cb.setAccountNumber(request.accountNumber());
              
              if (request.bankTrackingId() != null) {
                  cb.setBank(banqueRepository.findByTrackingId(request.bankTrackingId()).orElse(null));
              }
              compteBancaireRepository.save(cb);
          } catch (Exception e) {
              throw new RuntimeException("Erreur lors de l'upload du RIB commerçant", e);
          }
      }
      
      return userMapper.toResponse(savedMerchantUser);
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtService.generateJwtToken(authentication);

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

    return new LoginResponse(
        user.getTrackingId(),
        jwt,
        "Bearer",
        user.getFirstName(),
        user.getLastName(),
        user.getPhoneNumber(),
        user.getEmail(),
        user.getRole().name(),
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()),
        "Cameroon",
        user.isActive());
  }

  @Override
  @Transactional
  public UserResponse createUser(UserRequest request) {
    log.info("Tentative de création d'utilisateur: {} avec le rôle: {}", request.email(), request.role());
    
    UserRole role = UserRole.valueOf(request.role());

    User user = new User();
    
    user.setTrackingId(UUID.randomUUID());
    user.setLastName(request.lastName());
    user.setFirstName(request.firstName());
    user.setEmail(request.email());
    user.setPhoneNumber(request.phoneNumber());
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setRole(role);
    user.setActive(true);

    User savedUser = userRepository.save(user);
    log.info("Utilisateur créé avec succès: ID {}", savedUser.getTrackingId());
    
    return userMapper.toResponse(savedUser);
  }

  @Override
  @Transactional
  public UserResponse createAdminBanque(AdminBanqueRequest request) {
    log.info("Création d'un administrateur de banque: {}", request.email());

    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("Cet email est déjà utilisé.");
    }

    Banque banque =
        banqueRepository
            .findByTrackingId(request.bankTrackingId())
            .orElseThrow(() -> new EntityNotFoundException("Banque non trouvée"));

    AdminBanque admin = new AdminBanque();
    admin.setTrackingId(UUID.randomUUID());
    admin.setLastName(request.lastName());
    admin.setFirstName(request.firstName());
    admin.setEmail(request.email());
    admin.setPhoneNumber(request.phoneNumber());
    admin.setRole(UserRole.ADMIN_BANQUE);
    admin.setActive(true);
    admin.setPasswordHash(passwordEncoder.encode(request.password()));
    admin.setBanque(banque);

    AdminBanque savedAdmin = userRepository.save(admin);
    return userMapper.toResponse(savedAdmin);
  }

  @Override
  public UserResponse getUserByTrackingId(UUID trackingId) {
    User user =
        userRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    return userMapper.toResponse(user);
  }

  @Override
  public Page<UserResponse> getAllUsers(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);
    Page<User> usersPage = userRepository.findAll(pageRequest);
    return usersPage.map(userMapper::toResponse);
  }

  public Page<UserResponse> getUsersByRole(String role, int page, int size) {
    return Page.empty();
  }

  @Transactional
  public UserResponse updateUser(UUID trackingId, UserRequest request) {
    User user =
        userRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

    if (!user.getEmail().equals(request.email())
        && userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("Cet email est déjà utilisé.");
    }

    user.setLastName(request.lastName());
    user.setFirstName(request.firstName());
    user.setEmail(request.email());
    user.setPhoneNumber(request.phoneNumber());

    if (request.role() != null && !request.role().trim().isEmpty()) {
      try {
        user.setRole(UserRole.valueOf(request.role()));
      } catch (IllegalArgumentException e) {
        log.warn("Rôle ignoré lors de la mise à jour car invalide: {}", request.role());
      }
    }

    User updatedUser = userRepository.save(user);
    return userMapper.toResponse(updatedUser);
  }

  @Override
  @Transactional
  public UserResponse updateUserEtat(UUID trackingId, boolean etat) {
    User user =
        userRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    user.setActive(etat);
    return userMapper.toResponse(userRepository.save(user));
  }

  @Override
  public java.util.List<UserResponse> searchUsers(String query) {
    return java.util.Collections.emptyList();
  }

  @Override
  @Transactional
  public void deleteUser(UUID trackingId) {
    User user =
        userRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    userRepository.delete(user);
  }
}
