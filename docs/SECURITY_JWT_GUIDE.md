# 🔑 JWT & Security Configuration Reference

## JWT Authentication Flow

### Token Generation
```
User Login Endpoint
        ↓
UserDetailsServiceImpl validates credentials
        ↓ (BCrypt comparison)
Credentials valid ✓
        ↓
JwtService.generateToken() creates JWT
        ↓
Response: { "token": "..." }
```

### Token Validation on Each Request
```
HTTP Request with: Authorization: Bearer {token}
        ↓
JwtAuthorizationToken filter intercepts
        ↓
Extract token from header
        ↓
JwtService.validateToken()
        ↓
Parse & verify HS256 signature
        ↓
Check expiration
        ↓
✓ Valid → Load UserDetails
✓ Create authentication → Continue
✗ Invalid/Expired → 401 Unauthorized
```

---

## Configuration Files

### application.properties (Development)
```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/miabeshop
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Logging
logging.level.root=INFO
logging.level.com.backend.gns=DEBUG
logging.level.org.springframework.security=DEBUG

# JWT Configuration
jwt.secret=dev-secret-key-for-local-testing
jwt.expiration.ms=86400000

# OpenAPI/Swagger
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=method
springdoc.api-docs.path=/v3/api-docs
```

### application-prod.properties (Production)
```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database (use environment variables)
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://db:5432/miabeshop}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:miabeshop}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Logging
logging.level.root=WARN
logging.level.com.backend.gns=INFO

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration.ms=${JWT_EXPIRATION_MS:86400000}

# OpenAPI/Swagger (disabled in production)
springdoc.swagger-ui.enabled=false
```

### application-test.properties (Testing)
```properties
# Server
server.port=8080

# Database (H2 in-memory)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.show-sql=false

# Logging
logging.level.root=WARN
logging.level.com.backend.gns=DEBUG

# JWT Configuration (test secret)
jwt.secret=test-secret-key-for-testing
jwt.expiration.ms=3600000
```

---

## JWT Service Implementation

### JwtService.java Source Code
```java
@Service
@Slf4j
public class JwtService {
    
    @Value("${jwt.secret:default-secret-key}")
    private String SECRET_KEY;
    
    @Value("${jwt.expiration.ms:86400000}")
    private long expirationMs;
    
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;
    
    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported token: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
    
    /**
     * Extract expiration from token
     */
    public Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(SECRET_KEY)
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
    }
    
    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }
}
```

---

## JWT Filter Implementation

### JwtAuthorizationToken.java
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationToken extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            String jwt = extractJwtFromRequest(request);
            
            if (jwt != null && jwtService.validateToken(jwt)) {
                String username = jwtService.getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Authenticated user: {}", username);
            }
        } catch (Exception ex) {
            log.error("Authentication error: {}", ex.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract JWT token from Authorization header
     * Expected format: Authorization: Bearer {token}
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
}
```

---

## Security Configuration

### SecurityConfig.java Overview
```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthorizationToken jwtAuthorizationToken;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Stateless session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // CSRF disabled for stateless REST API
            .csrf(csrf -> csrf.disable())
            
            // Exception handling
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
            )
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                .requestMatchers("/api/swagger-ui/**", "/api/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            
            // Add JWT filter
            .addFilterBefore(
                jwtAuthorizationToken,
                UsernamePasswordAuthenticationFilter.class
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://*.netlify.app",
            "https://*.render.com",
            "https://miabeshop.cm",
            "https://www.miabeshop.cm"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

## User Login Example

### Login Request & Response
```bash
# Request
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jean@example.com",
    "password": "securePassword123"
  }'

# Response (Success)
HTTP/1.1 200 OK
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6WyJFVFVESUFOVCJdLCJzdWIiOiJqZWFuQGV4YW1wbGUuY29tIiwiaWF0IjoxNjMxNjEzODcwLCJleHAiOjE2MzE3MDAyNzB9.3Jd7Z5K2gL9pQ8wY1xN4vM6oR2aT5bC8dF1eH3iJ5kL"
}

# Response (Failure)
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "message": "Invalid email or password"
}
```

### Using Token in Requests
```bash
# Request with Bearer token
curl -X GET http://localhost:8080/api/student \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6WyJFVFVESUFOVCJdLCJzdWIiOiJqZWFuQGV4YW1wbGUuY29tIiwiaWF0IjoxNjMxNjEzODcwLCJleHAiOjE2MzE3MDAyNzB9.3Jd7Z5K2gL9pQ8wY1xN4vM6oR2aT5bC8dF1eH3iJ5kL"

# Response
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "trackingId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "jean@example.com",
    "nom": "Traoré",
    "prenom": "Jean",
    ...
  }
]
```

---

## Error Responses

### 401 Unauthorized (Missing/Invalid Token)
```json
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "status": 401,
  "message": "Unauthorized - Missing or invalid token",
  "timestamp": "2024-04-11T10:30:00Z"
}
```

### 403 Forbidden (Insufficient Permissions)
```json
HTTP/1.1 403 Forbidden
Content-Type: application/json

{
  "status": 403,
  "message": "Forbidden - Insufficient permissions",
  "timestamp": "2024-04-11T10:30:00Z"
}
```

---

## Security Best Practices

### ✅ DO
- ✅ Always use HTTPS in production
- ✅ Store JWT secret in environment variables (never in code)
- ✅ Set appropriate JWT expiration (24 hours recommended)
- ✅ Use strong password hashing (BCrypt)
- ✅ Validate all input from users
- ✅ Use CORS to restrict requests to known domains
- ✅ Log authentication attempts
- ✅ Disable Swagger UI in production

### ❌ DON'T
- ❌ Expose JWT secret in code or version control
- ❌ Use weak passwords or default secrets
- ❌ Set extremely long JWT expiration times
- ❌ Store sensitive data in JWT payload (it's base64, not encrypted)
- ❌ Disable CSRF protection without understanding implications
- ❌ Accept credentials from unverified sources
- ❌ Log passwords or tokens
- ❌ Allow unlimited CORS origins

---

## Environment Variables Setup

### Development (.env file)
```bash
# .env (add to .gitignore)
JWT_SECRET=dev-secret-key-for-local-testing
JWT_EXPIRATION_MS=86400000
DATABASE_URL=jdbc:postgresql://localhost:5432/miabeshop
DATABASE_USER=postgres
DATABASE_PASSWORD=password
```

### Production (Kubernetes/Docker)
```bash
kubectl set env deployment/gns-api \
  JWT_SECRET="<production-secret-key>" \
  JWT_EXPIRATION_MS="86400000" \
  SPRING_DATASOURCE_URL="jdbc:postgresql://prod-db:5432/miabeshop" \
  SPRING_DATASOURCE_USERNAME="miabeshop" \
  SPRING_DATASOURCE_PASSWORD="<db-password>"
```

---

## Testing with Postman

### 1. Create Login Collection
```
POST http://localhost:8080/api/user/login
Headers: Content-Type: application/json
Body:
{
  "email": "jean@example.com",
  "password": "securePassword123"
}
```

### 2. Extract Token
```
Tests tab:
var jsonData = pm.response.json();
pm.environment.set("jwt_token", jsonData.token);
```

### 3. Use Token in Requests
```
GET http://localhost:8080/api/student
Headers:
Authorization: Bearer {{jwt_token}}
```

---

## Token Renewal Strategy

### Option 1: Force Re-login After Expiration
- Simple implementation
- User logs out when token expires
- Best for short-lived tokens (< 1 hour)

### Option 2: Refresh Token Pattern
```
1. Login returns: access_token (1 hour) + refresh_token (7 days)
2. After access_token expires, use refresh_token to get new access_token
3. Refresh_token stored in database (can be revoked)
```

### Current Implementation
MiabéShop uses **Option 1** (force re-login after 24 hours)
- Simplifies token management
- Reasonable for non-critical applications
- Can be upgraded to refresh tokens later

---

## Troubleshooting

### Issue: "Invalid token" error
```
Causes:
1. Token expired (check expiration time)
2. Secret key mismatch (dev/prod have different secrets)
3. Token format incorrect (missing "Bearer " prefix)

Solution:
- Get new token from login endpoint
- Verify jwt.secret matches environment
- Check Authorization header format
```

### Issue: "Token not found" error
```
Cause:
- Authorization header missing
- Incorrect header name (must be "Authorization")

Solution:
- Add header: Authorization: Bearer {token}
```

### Issue: User can access protected resources without token
```
Cause:
- Security configuration not applied
- Filter order incorrect

Solution:
- Verify @EnableWebSecurity annotation
- Check filter is added before UsernamePasswordAuthenticationFilter
- Run mvn clean compile and restart application
```

---

## References

- [JWT.io Documentation](https://jwt.io)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [Spring Security Guide](https://spring.io/guides/topicals/spring-security-architecture/)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

