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
      
      // 1. Création de l'étudiant
      com.backend.gns.student.domain.models.Student student = new com.backend.gns.student.domain.models.Student();
      student.setTrackingId(UUID.randomUUID());
      student.setNom(request.nom());
      student.setPrenom(request.prenom());
      student.setEmail(request.email());
      student.setTelephone(request.telephone());
      student.setRole(UserRole.ETUDIANT);
      student.setEstActif(true);
      student.setMotDePasse(passwordEncoder.encode(request.password()));
      student.setMatricule(request.matricule());
      student.setStatutKYC(com.backend.gns.core.domain.enums.KycStatus.EN_ATTENTE);
      
      if (request.universiteTrackingId() != null) {
          student.setUniversite(universiteRepository.findByTrackingId(request.universiteTrackingId()).orElse(null));
      }
      
      com.backend.gns.student.domain.models.Student savedStudent = studentRepository.save(student);
      
      // 2. Traitement des fichiers (RIB et Mandat)
      try {
          if (rib != null) {
              var ribUpload = storageService.upload(rib, "rib_" + savedStudent.getTrackingId());
              var docRib = com.backend.gns.student.domain.models.DocumentEtudiant.builder()
                  .trackingId(UUID.randomUUID())
                  .type(com.backend.gns.core.domain.enums.TypeDocument.RIB)
                  .urlFichier(ribUpload.get("url"))
                  .statut(com.backend.gns.student.domain.enums.StatutDocument.EN_ATTENTE)
                  .dateDepot(java.time.LocalDateTime.now())
                  .build();
              var savedDocRib = documentEtudiantRepository.save(docRib);
              
              // Création du compte bancaire associé
              com.backend.gns.core.domain.models.CompteBancaire cb = new com.backend.gns.core.domain.models.CompteBancaire();
              cb.setTrackingId(UUID.randomUUID());
              cb.setProprietaireTrackingId(savedStudent.getTrackingId());
              cb.setTypeProprietaire(com.backend.gns.core.domain.enums.ProprietaireType.STUDENT);
              cb.setNumeroCompte(request.numeroCompte());
              cb.setRibDocument(savedDocRib);
              cb.setComptePrincipalBourse(true);
              
              if (request.banqueTrackingId() != null) {
                  cb.setBanque(banqueRepository.findByTrackingId(request.banqueTrackingId()).orElse(null));
              }
              compteBancaireRepository.save(cb);
          }
          
          if (mandat != null) {
              var mandatUpload = storageService.upload(mandat, "mandat_" + savedStudent.getTrackingId());
              var docMandat = com.backend.gns.student.domain.models.DocumentEtudiant.builder()
                  .trackingId(UUID.randomUUID())
                  .type(com.backend.gns.core.domain.enums.TypeDocument.MANDAT)
                  .urlFichier(mandatUpload.get("url"))
                  .statut(com.backend.gns.student.domain.enums.StatutDocument.EN_ATTENTE)
                  .dateDepot(java.time.LocalDateTime.now())
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
      
      // 1. Création de l'utilisateur Merchant
      com.backend.gns.commerce.domain.models.Merchant merchant = new com.backend.gns.commerce.domain.models.Merchant();
      merchant.setTrackingId(UUID.randomUUID());
      merchant.setNom(request.nom());
      merchant.setPrenom(request.prenom());
      merchant.setEmail(request.email());
      merchant.setTelephone(request.telephone());
      merchant.setRole(UserRole.COMMERCANT);
      merchant.setEstActif(true);
      merchant.setMotDePasse(passwordEncoder.encode(request.password()));
      
      com.backend.gns.commerce.domain.models.Merchant savedMerchantUser = userRepository.save(merchant);
      
      // 2. Création de la boutique associée
      com.backend.gns.commerce.domain.models.Boutique boutique = new com.backend.gns.commerce.domain.models.Boutique();
      boutique.setTrackingId(UUID.randomUUID());
      boutique.setNomBoutique(request.nomBoutique());
      boutique.setMerchant(savedMerchantUser);
      boutique.setStatutKYC(com.backend.gns.core.domain.enums.KycStatus.EN_ATTENTE);
      boutique.setCheminCarteEDJ("N/A"); // À uploader plus tard si besoin
      boutique.setCategorieShop("N/A");
      boutiqueRepository.save(boutique);
      
      // 3. Création du compte bancaire pour les liquidations
      if (rib != null) {
          try {
              var ribUpload = storageService.upload(rib, "rib_merchant_" + boutique.getTrackingId());
              // Note: On pourrait réutiliser DocumentEtudiant ou créer DocumentMerchant si besoin
              // Ici on stocke juste l'info bancaire
              com.backend.gns.core.domain.models.CompteBancaire cb = new com.backend.gns.core.domain.models.CompteBancaire();
              cb.setTrackingId(UUID.randomUUID());
              cb.setProprietaireTrackingId(boutique.getTrackingId());
              cb.setTypeProprietaire(com.backend.gns.core.domain.enums.ProprietaireType.MERCHANT);
              cb.setNumeroCompte(request.numeroCompte());
              
              if (request.banqueTrackingId() != null) {
                  cb.setBanque(banqueRepository.findByTrackingId(request.banqueTrackingId()).orElse(null));
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
        user.getPrenom(),
        user.getNom(),
        user.getTelephone(),
        user.getEmail(),
        user.getRole().name(),
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()),
        "Cameroon",
        user.isEstActif());
  }

  @Override
  @Transactional
  public UserResponse createUser(UserRequest request) {
    log.info("Tentative de création d'utilisateur: {} avec le rôle: {}", request.email(), request.role());
    
    UserRole role;
    try {
      role = UserRole.valueOf(request.role());
    } catch (Exception e) {
      role = UserRole.ETUDIANT;
    }

    User user = new User();
    
    user.setTrackingId(UUID.randomUUID());
    user.setNom(request.nom());
    user.setPrenom(request.prenom());
    user.setEmail(request.email());
    user.setTelephone(request.telephone());
    user.setPays(request.pays());
    user.setRole(role);
    user.setEstActif(true);
    user.setMotDePasse(passwordEncoder.encode(request.motDePasse()));

    User savedUser = userRepository.save(user);
    log.info("Utilisateur créé avec succès: {}", savedUser.getEmail());
    return userMapper.toResponse(savedUser);
  }

  @Override
  @Transactional
  public UserResponse createAdminBanque(AdminBanqueRequest request) {
    log.info("Tentative de création d'admin banque: {}", request.email());

    Banque banque = banqueRepository.findByTrackingId(request.banqueTrackingId())
        .orElseThrow(() -> new ResourceNotFoundException("Banque non trouvée"));

    AdminBanque adminBanque = new AdminBanque();
    adminBanque.setTrackingId(UUID.randomUUID());
    adminBanque.setNom(request.nom());
    adminBanque.setPrenom(request.prenom());
    adminBanque.setEmail(request.email());
    adminBanque.setTelephone(request.telephone());
    adminBanque.setPays(request.pays());
    adminBanque.setRole(UserRole.ADMIN_BANQUE);
    adminBanque.setEstActif(true);
    adminBanque.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
    adminBanque.setBanque(banque);

    AdminBanque savedUser = userRepository.save(adminBanque);
    log.info("Admin Banque créé avec succès: {}", savedUser.getEmail());
    return userMapper.toResponse(savedUser);
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
  @Transactional
  public UserResponse updateUserEtat(UUID trackingId, boolean etat) {
    User user =
        userRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    user.setEstActif(etat);
    return userMapper.toResponse(userRepository.save(user));
  }

  @Override
  public Page<UserResponse> getAllUsers(int page, int size) {
    PageRequest pageable = PageRequest.of(page, size);
    return userRepository.findAll(pageable).map(userMapper::toResponse);
  }

  @Override
  public java.util.List<UserResponse> searchUsers(String query) {
    return userRepository.searchUsers(query).stream().map(userMapper::toResponse).toList();
  }

  @Override
  @Transactional
  public void deleteUser(UUID trackingId) {
    User user =
        userRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    user.setEstActif(false); // soft delete
    userRepository.save(user);
  }
}
