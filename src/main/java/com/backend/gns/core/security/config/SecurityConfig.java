package com.backend.gns.core.security.config;

import com.backend.gns.core.security.jwt.filters.JwtAuthorizationToken;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.PUBLIC_URLS)
                    .permitAll()
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.ADMIN_URLS)
                    .hasRole("ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.ETUDIANT_URLS)
                    .hasAnyRole("ETUDIANT", "ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.COMMERCANT_URLS)
                    .hasAnyRole("COMMERCANT", "ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.BANQUE_URLS)
                    .hasAnyRole("ADMIN_BANQUE", "ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.UNIVERSITY_URLS)
                    .hasAnyRole("UNIVERSITY_ADMIN", "ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.DBS_URLS)
                    .hasAnyRole("ADMIN_DBS", "ADMIN_GNS")
                    // Rôles pour les URLs partagées
                    .requestMatchers("/wallets/freeze-all")
                    .hasRole("ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.WALLETS_URLS)
                    .hasAnyRole("ETUDIANT", "ADMIN_BANQUE", "ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.VERSEMENTS_URLS)
                    .hasAnyRole("ADMIN_BANQUE", "ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.PAIEMENTS_URLS)
                    .hasAnyRole("ETUDIANT", "UNIVERSITY_ADMIN", "ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.COMMANDES_URLS)
                    .hasAnyRole("ETUDIANT", "COMMERCANT", "ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.STATS_URLS)
                    .hasAnyRole("UNIVERSITY_ADMIN", "ADMIN_DBS", "ADMIN_GNS")
                    .requestMatchers(
                        com.backend.gns.core.security.constants.JavaConstant.UNIVERSITES_URLS)
                    .hasAnyRole("ADMIN_GNS", "ADMIN_DBS", "UNIVERSITY_ADMIN", "ETUDIANT")
                    .anyRequest()
                    .authenticated());
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // TEMPORAIRE : Autoriser toutes les origines pour les tests
    configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Collections.singletonList("*"));
    configuration.setExposedHeaders(Arrays.asList("x-auth-token", "authorization", "content-type"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
