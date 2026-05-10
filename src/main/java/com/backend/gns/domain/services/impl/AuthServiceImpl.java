package com.backend.gns.domain.services.impl;

import com.backend.gns.Shared.security.jwt.JwtService;
import com.backend.gns.Shared.user.infrastructure.repositories.UserRepository;
import com.backend.gns.application.dtos.requests.AuthRequest;
import com.backend.gns.application.dtos.responses.AuthResponse;
import com.backend.gns.domain.services.AuthService;
import com.backend.gns.Shared.user.domain.models.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service d'authentification.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String token = jwtService.generateJwtToken(authentication);

        return new AuthResponse(
                user.getTrackingId(),
                user.getEmail(),
                user.getNom(),
                user.getPrenom(),
                user.getRole(),
                token
        );
    }
}
