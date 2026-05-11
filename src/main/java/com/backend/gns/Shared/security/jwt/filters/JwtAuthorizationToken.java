package com.saas.plateform.Shared.security.jwt.filters;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.saas.plateform.Shared.security.constants.JavaConstant;
import com.saas.plateform.Shared.security.jwt.JwtService;
import com.saas.plateform.Shared.security.userDetailsConf.UserServiceSecure;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationToken extends OncePerRequestFilter {

    public static final String TOKEN_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserServiceSecure userServiceSecure;

    public JwtAuthorizationToken( JwtService jwtService, UserServiceSecure userServiceSecure) {
        this.jwtService = jwtService;
        this.userServiceSecure = userServiceSecure;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", JavaConstant. FRONTEND_URL);
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", String.valueOf(true));
        response.setHeader("Access-Control-Max-Age", String.valueOf(180));
        if (request.getMethod().equalsIgnoreCase(JavaConstant.OPTIONS_HTTP_METHOD)) {
            response.setStatus(OK.value());
            System.out.println("filter 1");
        } else {
            String authorizeHeader = request.getHeader(AUTHORIZATION);
            if (authorizeHeader == null || !authorizeHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authorizeHeader.substring(TOKEN_PREFIX.length());
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = this.userServiceSecure.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails) && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("/**********************************************/");
                System.out.println("The token is valid");
                System.out.println("/**********************************************/");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                System.out.println(authToken.isAuthenticated());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        }
    }
}
