package com.backend.gns.user.application.mappers;

import com.backend.gns.user.application.dtos.requests.UserRequest;
import com.backend.gns.user.application.dtos.responses.UserResponse;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.user.domain.models.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapper {

  public User toEntity(UserRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("UserRequest cannot be null");
    }
    User user = new User();
    user.setTrackingId(UUID.randomUUID());
    user.setLastName(request.lastName());
    user.setFirstName(request.firstName());
    user.setEmail(request.email());
    user.setPhoneNumber(request.phoneNumber());
    if (request.role() != null && !request.role().trim().isEmpty()) {
      try {
        user.setRole(UserRole.valueOf(request.role()));
      } catch (IllegalArgumentException e) {
        user.setRole(UserRole.ETUDIANT);
      }
    } else {
      user.setRole(UserRole.ETUDIANT);
    }
    user.setActive(true);
    return user;
  }

  public UserResponse toResponse(User user) {
    if (user == null) {
      return null;
    }

    return new UserResponse(
        user.getTrackingId(),
        user.getLastName(),
        user.getFirstName(),
        user.getPhoneNumber(),
        user.getEmail(),
        user.getRole() != null ? user.getRole().name() : null,
        user.getRegistrationDate(),
        user.getKycStatus(),
        user.isActive());
  }
}
