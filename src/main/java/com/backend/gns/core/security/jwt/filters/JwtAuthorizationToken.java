package com.backend.gns.core.security.jwt.filters;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import com.backend.gns.core.security.jwt.JwtService;
import com.backend.gns.core.security.userDetailsConf.UserServiceSecure;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthorizationToken extends OncePerRequestFilter {

  public static final String TOKEN_PREFIX = "Bearer ";

  private final JwtService jwtService;
  private final UserServiceSecure userServiceSecure;

  public JwtAuthorizationToken(JwtService jwtService, UserServiceSecure userServiceSecure) {
    this.jwtService = jwtService;
    this.userServiceSecure = userServiceSecure;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authorizeHeader = request.getHeader(AUTHORIZATION);
    if (authorizeHeader == null || !authorizeHeader.startsWith(TOKEN_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }
    String token = authorizeHeader.substring(TOKEN_PREFIX.length());
    String username = jwtService.extractUsername(token);
    UserDetails userDetails = this.userServiceSecure.loadUserByUsername(username);
    if (jwtService.isTokenValid(token, userDetails)
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    } else {
      SecurityContextHolder.clearContext();
    }
    filterChain.doFilter(request, response);
  }
}
