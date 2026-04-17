package com.backend.gns.Shared.user.domain.services;

import com.backend.gns.Shared.user.application.dtos.requests.UserRequest;
import com.backend.gns.Shared.user.application.dtos.responses.UserResponse;
import com.backend.gns.Shared.user.application.mappers.UserMapper;
import com.backend.gns.Shared.user.domain.models.User;
import com.backend.gns.Shared.user.infrastructure.repositories.UserRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;

  public UserResponse createUser(UserRequest request) {
    User user = this.userMapper.toEntity(request);
    this.userRepository.save(user);
    return this.userMapper.toResponse(user);
  }

  @Override
  public UserResponse getUserByTrackingId(UUID trackingId) {
    User user =
        userRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
    return userMapper.toResponse(user);
  }

  @Override
  public UserResponse updateUserEtat(UUID trackingId, boolean etat) {
    User user =
        userRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

    user.setEstActif(etat);
    user = userRepository.save(user);
    return userMapper.toResponse(user);
  }

  @Override
  public Page<UserResponse> getAllUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return userRepository.findAll(pageable).map(userMapper::toResponse);
  }

  @Override
  public void deleteUser(UUID trackingId) {
    User user =
        userRepository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

    userRepository.delete(user);
  }
}
