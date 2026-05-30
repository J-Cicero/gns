package com.backend.gns.user.application.mappers;

import com.backend.gns.user.application.dtos.requests.UserRequest;
import com.backend.gns.user.application.dtos.responses.UserResponse;
import com.backend.gns.user.domain.enums.UserRole;
import com.backend.gns.user.domain.models.User;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toEntity(UserRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("UserRequest ne peut pas être null");
    }
    User user = new User();
    user.setTrackingId(UUID.randomUUID());
    user.setNom(request.nom());
    user.setPrenom(request.prenom());
    user.setEmail(request.email());
    user.setPassword(request.motDePasse());
    user.setTelephone(request.telephone());
    user.setRole(UserRole.ETUDIANT);
    user.setEstActif(true);
    return user;
  }

  public UserResponse toResponse(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User ne peut pas être null");
    }

    return new UserResponse(
        user.getTrackingId(),
        user.getNom(),
        user.getPrenom(),
        null,
        user.getEmail(),
        user.getRole().name(),
        null,
        user.isEstActif());
  }
}
