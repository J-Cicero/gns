package com.backend.gns.Shared.user.domain.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.gns.Shared.security.jwt.JwtService;
import com.backend.gns.Shared.user.application.dtos.requests.LoginRequest;
import com.backend.gns.Shared.user.application.dtos.requests.UserRequest;
import com.backend.gns.Shared.user.application.dtos.responses.LoginResponse;
import com.backend.gns.Shared.user.application.dtos.responses.UserResponse;
import com.backend.gns.Shared.user.application.mappers.UserMapper;
import com.backend.gns.Shared.user.domain.models.User;
import com.backend.gns.Shared.user.infrastructure.repositories.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserResponse createUser(UserRequest request){
        User user = this.userMapper.toEntity(request);
        this.userRepository.save(user);
       return this.userMapper.toResponse(user);
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtService.generateJwtToken(authentication);

            Object principal = authentication.getPrincipal();
            
            if (principal instanceof User user) {
                List<String> rolesList = authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

                return new LoginResponse(
                        user.getTrackingId(),
                        token,
                        "Bearer",
                        user.getNom(),
                        user.getPrenom(),
                        null,
                        user.getEmail(),
                        user.getRole().name(),
                        rolesList,
                        null,
                        user.isEstActif()
                );
            } else {
                throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
            }

        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Les paramètres de connexion sont incorrects");
        }
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
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
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
