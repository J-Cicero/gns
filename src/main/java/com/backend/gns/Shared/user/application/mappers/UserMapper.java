package com.backend.gns.Shared.user.application.mappers;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.backend.gns.Shared.user.application.dtos.requests.UserRequest;
import com.backend.gns.Shared.user.application.dtos.responses.UserResponse;
import com.backend.gns.Shared.user.domain.enums.TypeRole;
import com.backend.gns.Shared.user.domain.models.User;


@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(UserRequest request){
        if (request == null) {
            throw new IllegalArgumentException("UserRequest ne peut pas être null");
        }
        User user = new User();
        user.setTrackingId(UUID.randomUUID());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        
        user.setRole(TypeRole.ADMINISTRATEUR);
        user.setActive(true);
        return user;
    }

    public UserResponse toResponse(User user){
        if (user == null) {
            throw new IllegalArgumentException("User ne peut pas être null");
        }
        
        return new UserResponse(
                user.getTrackingId(),
                user.getFirstName(),
                user.getLastName(),
                null,
                user.getEmail(),
                user.getRole().name(),
                null,
                user.isActive()
        );
    }
}
