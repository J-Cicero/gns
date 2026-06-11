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
import com.backend.gns.user.domain.models.BankOperator;
import com.backend.gns.user.domain.models.User;
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
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

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

    User user;
    if (role == UserRole.ADMIN_BANQUE) {
      BankOperator bankOp = new BankOperator();
      if (request.banquePartenaireTrackingId() != null) {
        bankOp.setBanquePartenaire(banqueRepository.findByTrackingId(request.banquePartenaireTrackingId())
            .orElseThrow(() -> new ResourceNotFoundException("Banque non trouvée")));
      }
      user = bankOp;
    } else {
      user = new User();
    }

    user.setTrackingId(UUID.randomUUID());
    user.setNom(request.nom());
    user.setPrenom(request.prenom());
    user.setEmail(request.email());
    user.setTelephone(request.telephone());
    user.setRole(role);
    user.setEstActif(true);
    user.setMotDePasse(passwordEncoder.encode(request.motDePasse()));

    User savedUser = userRepository.save(user);
    log.info("Utilisateur créé avec succès: {}", savedUser.getEmail());
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
