package com.backend.gns.user.domain.services;

import com.backend.gns.user.application.dtos.requests.UserRequest;
import com.backend.gns.user.application.dtos.responses.UserResponse;
import com.backend.gns.user.application.mappers.UserMapper;
import com.backend.gns.user.domain.exception.ResourceNotFoundException;
import com.backend.gns.user.domain.models.User;
import com.backend.gns.user.infrastructure.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import com.backend.gns.user.application.dtos.requests.LoginRequest;
import com.backend.gns.user.application.dtos.responses.LoginResponse;
import com.backend.gns.core.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Override
  public LoginResponse login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtService.generateJwtToken(authentication);

    User user = userRepository.findByEmail(request.email())
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
        "Cameroon", // Default country since there's no country field
        user.isEstActif()
    );
  }



  @Override
  @Transactional
  public UserResponse createUser(UserRequest request) {
    User user = userMapper.toEntity(request);
    user.setMotDePasse(request.motDePasse());
    userRepository.save(user);
    return userMapper.toResponse(user);
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
    Pageable pageable = PageRequest.of(page, size);
    return userRepository.findAll(pageable).map(userMapper::toResponse);
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
