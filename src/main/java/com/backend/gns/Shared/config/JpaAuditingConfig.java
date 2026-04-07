package com.backend.gns.Shared.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.backend.gns.Shared.user.domain.models.User;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * Implémentation de AuditorAware pour récupérer l'utilisateur connecté
     */
    public static class AuditorAwareImpl implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }
            
            Object principal = authentication.getPrincipal();
            
            // Si c'est un User (notre entité)
            if (principal instanceof User user) {
                return Optional.of(user.getEmail());
            }
            
            // Si c'est une String (anonymousUser)
            if (principal instanceof String) {
                return Optional.of("SYSTEM");
            }
            
            return Optional.of("SYSTEM");
        }
    }
}
