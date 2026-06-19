package com.backend.gns.user.domain.services.Impl;

import com.backend.gns.core.parametrage.domain.models.Banque;
import com.backend.gns.core.parametrage.infrastructure.repositories.BanqueRepository;
import com.backend.gns.core.security.jwt.JwtService;
import com.backend.gns.user.application.dtos.requests.AdminBanqueRequest;
import com.backend.gns.user.application.dtos.requests.LoginRequest;
import com.backend.gns.user.application.dtos.requests.UserRequest;
import com.backend.gns.user.application.dtos.responses.LoginResponse;
import com.backend.gns.user.application.dtos.responses.UserResponse;
import com.backend.gns.user.application.mappers.UserMapper;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.user.domain.exception.ResourceNotFoundException;
import com.backend.gns.user.domain.models.AdminBanque;
import com.backend.gns.user.domain.models.User;
import com.backend.gns.user.domain.services.UserService;
import com.backend.gns.user.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final BanqueRepository banqueRepository;



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
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(role);
    user.setActive(false);

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
    admin.setPassword(passwordEncoder.encode(request.password()));
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
