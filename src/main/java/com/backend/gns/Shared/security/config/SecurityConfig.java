package com.backend.gns.Shared.security.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.backend.gns.Shared.security.constants.JavaConstant;
import com.backend.gns.Shared.security.jwt.filters.JwtAuthorizationToken;
import com.backend.gns.Shared.security.userDetailsConf.UserServiceSecure;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private JwtAuthorizationToken authenticationFilter;

    public SecurityConfig(JwtAuthorizationToken authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                //.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                //.authenticationProvider(authenticationProvider());
                .authorizeHttpRequests(auth -> auth
                        // URLs publiques (sans auth)
                        .requestMatchers(JavaConstant.PUBLIC_URLS).permitAll()
                        
                        // URLs ADMINISTRATEUR (accès le plus élevé)
                        .requestMatchers(JavaConstant.ADMINISTRATEUR_URLS).hasRole("ADMINISTRATEUR")
                        
                        // URLs GESTIONNAIRE
                        .requestMatchers(JavaConstant.GESTIONNAIRE_URLS).hasAnyRole("GESTIONNAIRE", "ADMINISTRATEUR")
                        
                        // URLs CONSULTANT
                        .requestMatchers(JavaConstant.CONSULTANT_URLS).hasAnyRole("CONSULTANT", "ADMINISTRATEUR")
                        
                        // URLs FREELANCE
                        .requestMatchers(JavaConstant.FREELANCE_URLS).hasAnyRole("FREELANCE", "ADMINISTRATEUR")
                        
                        // URLs UTILISATEUR (accessible à tous les rôles authentifiés)
                        .requestMatchers(JavaConstant.UTILISATEUR_URLS).hasAnyRole("UTILISATEUR", "GESTIONNAIRE", "CONSULTANT", "FREELANCE", "ADMINISTRATEUR")
                        
                        // Tout le reste nécessite une authentification
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserServiceSecure userServiceSecure) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userServiceSecure);
        authProvider.setPasswordEncoder(this.passwordEncoder());
        return authProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Configuration CORS spécifique pour production et développement
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "https://astonishing-medovik-c97d49.netlify.app",
                "https://*.netlify.app",
                "https://titan-backend-springboot-new-*.onrender.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token", "authorization", "content-type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight pour 1 heure

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
