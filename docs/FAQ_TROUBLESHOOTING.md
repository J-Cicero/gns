# 🆘 FAQ & Troubleshooting Guide

## 📚 Frequently Asked Questions

### 1. How do I start the development server?

**Q**: What's the command to run the application?  
**A**:
```bash
mvn clean compile
mvn spring-boot:run
```

Or with a specific profile:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

Access the application at: http://localhost:8080/api/swagger-ui.html

---

### 2. How do I connect to the database?

**Q**: I get "Connection refused" error on PostgreSQL startup  
**A**:
```bash
# macOS
brew services start postgresql

# Linux (Ubuntu/Debian)
sudo service postgresql start

# Check if running
psql --version
psql -U postgres -c "SELECT 1"

# Windows
# Start via Services app or: pg_ctl -D "C:\Program Files\PostgreSQL\15\data" start
```

Create the development database:
```bash
createdb miabeshop
```

---

### 3. How do I run tests?

**Q**: What's the command to execute the integration tests?  
**A**:
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=JpaRelationshipsIntegrationTest

# Run specific test method
mvn test -Dtest=JpaRelationshipsIntegrationTest#testCreateStudentAndWallet

# Run with coverage report
mvn clean test jacoco:report
# Report: target/site/jacoco/index.html
```

---

### 4. What's the difference between the three property files?

**Q**: When should I use application.properties vs application-dev.properties?  
**A**:

| File | Usage | Database | When to Use |
|------|-------|----------|------------|
| `application.properties` | Default profile | PostgreSQL | Never directly (other profiles override) |
| `application-dev.properties` | Development | PostgreSQL (localhost) | `mvn spring-boot:run` |
| `application-prod.properties` | Production | PostgreSQL (remote) | Docker/Kubernetes deployment |
| `application-test.properties` | Testing | H2 in-memory | `mvn test` |

**Activate a profile**:
```bash
# Development (default)
mvn spring-boot:run

# Production
mvn spring-boot:run -Dspring.profiles.active=prod

# Testing
mvn test  # Automatically uses test profile
```

---

### 5. How do I create a new entity?

**Q**: What's the step-by-step process?  
**A**:

1. **Create the Entity** (`domain/models/YourEntity.java`)
```java
@Entity
@Table(name = "your_entities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YourEntity extends BaseEntity {
    private String name;
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "related_entity_id")
    private RelatedEntity relatedEntity;
}
```

2. **Create the Repository** (`infrastructure/repositories/YourEntityRepository.java`)
```java
@Repository
public interface YourEntityRepository extends JpaRepository<YourEntity, Long> {
    Optional<YourEntity> findByTrackingId(UUID trackingId);
}
```

3. **Create the Service** (`domain/services/YourEntityService.java`)
```java
public interface YourEntityService {
    YourEntityResponse create(YourEntityRequest request);
    List<YourEntityResponse> getAll();
    YourEntityResponse getByTrackingId(UUID trackingId);
    YourEntityResponse update(UUID trackingId, YourEntityRequest request);
    void delete(UUID trackingId);
}
```

4. **Implement the Service** (`domain/services/impl/YourEntityServiceImpl.java`)

5. **Create the Mapper** (`application/mappers/YourEntityMapper.java`)
```java
@Component
@RequiredArgsConstructor
public class YourEntityMapper {
    private final RelatedEntityRepository relatedEntityRepository;
    
    public YourEntity toEntity(YourEntityRequest request) {
        RelatedEntity related = relatedEntityRepository.findByTrackingId(...)
            .orElseThrow(...);
        return YourEntity.builder()
            .name(request.name())
            .relatedEntity(related)
            .build();
    }
}
```

6. **Create DTOs** (`domain/dtos/YourEntityRequest.java` & `YourEntityResponse.java`)

7. **Create the Controller** (`application/controllers/YourEntityController.java`)

8. **Write Tests** (`src/test/java/YourEntityServiceTest.java`)

9. **Commit**
```bash
git add .
git commit -m "feat: add YourEntity with CRUD operations"
```

✅ See [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) for detailed patterns and examples.

---

### 6. How do I fix "Cannot find symbol" compilation errors?

**Q**: I get compilation errors after adding new code  
**A**:

**Common Causes:**

1. **Missing import statement**
```java
// Missing: import java.util.UUID;
UUID trackingId;  // ❌ "Cannot find symbol: UUID"
```
**Solution**: Add `import java.util.UUID;`

2. **Typo in variable/method name**
```java
wallet.getStudent().getId();  // ✅ Correct
wallet.getStudentId();        // ❌ Method doesn't exist (not @ManyToOne anymore)
```
**Solution**: Use the relationship object: `wallet.getStudent().getId()`

3. **Missing repository injection**
```java
// ❌ WRONG: No repository injected
public WalletMapper() {}

public Wallet toEntity(WalletRequest request) {
    studentRepository.findByTrackingId(...);  // ❌ Cannot find field
}

// ✅ CORRECT: Inject repository
public WalletMapper(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
}
```

4. **Missing @Component or @Service annotation**
```java
// ❌ WRONG: No annotation
public class WalletMapper { }

// ✅ CORRECT: Add annotation
@Component
public class WalletMapper { }
```

**Solution**: Run full rebuild
```bash
mvn clean compile
```

---

### 7. How do I get a valid JWT token?

**Q**: How do I authenticate and get a token for testing?  
**A**:

1. **Use Swagger UI** (easiest for testing)
   - Go to http://localhost:8080/api/swagger-ui.html
   - Find `POST /api/user/login`
   - Click "Try it out"
   - Enter credentials:
     ```json
     {
       "email": "jean@example.com",
       "password": "securePassword123"
     }
     ```
   - Copy the token from response

2. **Use cURL**
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"jean@example.com",
    "password":"securePassword123"
  }'

# Response:
# { "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }
```

3. **Use in Future Requests**
```bash
curl -X GET http://localhost:8080/api/student \
  -H "Authorization: Bearer {token}"
```

4. **First-Time Setup** (create initial user)
```bash
# 1. Register a new user
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "email":"newuser@example.com",
    "password":"securePassword123",
    "nom":"Doe",
    "prenom":"John"
  }'

# 2. Login with that user
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"newuser@example.com",
    "password":"securePassword123"
  }'
```

---

### 8. What database fields does each entity have?

**Q**: What columns are in each table?  
**A**:

All entities inherit from `BaseEntity`:
- `id` (Long, PK)
- `tracking_id` (UUID, unique)
- `created_at` (Timestamp)
- `updated_at` (Timestamp)
- `created_by` (String)
- `updated_by` (String)

**Entity-Specific Fields:**

| Entity | Key Fields | Foreign Keys |
|--------|-----------|--------------|
| **User** | email, password, nom, prenom, role, est_actif, date_inscription | - |
| **Student** | matricule_ul, niveau (enum), credits_valides, note_de_passage, rib, statut_kyc | - |
| **Merchant** | nom_boutique, description, adresse, grade | - |
| **Admin** | grade | - |
| **Wallet** | type_wallet (enum), balance_relais, balance_horizon, est_actif | student_id (FK) |
| **Product** | nom, description, prix, quantite_disponible | merchant_id (FK) |
| **Commande** | statut_commande (enum), montant_total, date_commande | student_id, merchant_id (FK) |
| **Paiement** | montant_produit, commission, montant_debite, est_switch, type_paiement (enum), statut (enum) | commande_id, wallet_id (FK) |
| **Versement** | montant_verse, type_versement (enum), statut_versement (enum), date_versement | wallet_id (FK) |
| **BudgetVirtuel** | montant_alloue, montant_restant, periode_mois, est_epuise | merchant_id (FK) |

---

### 9. How do I debug relationship issues?

**Q**: My @ManyToOne relationship is null or not loading  
**A**:

**Symptoms**:
- `NullPointerException` when accessing related entity
- Related entity not populated from database
- Lazy loading issues

**Debugging Steps**:

1. **Check the mapping**
```java
// In Mapper.toEntity():
Student student = studentRepository.findByTrackingId(request.studentTrackingId())
    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

wallet.setStudent(student);  // Ensure you set the object
```

2. **Enable Hibernate logging** (in application-dev.properties)
```properties
logging.level.org.hibernate=DEBUG
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true
```

3. **Verify @JoinColumn**
```java
// ✅ CORRECT
@ManyToOne
@JoinColumn(name = "student_id", nullable = false)
private Student student;

// ❌ WRONG (missing @JoinColumn)
@ManyToOne
private Student student;  // Hibernate might use default column name
```

4. **Check repository returns results**
```java
Optional<Student> student = studentRepository.findByTrackingId(trackingId);
System.out.println("Student found: " + student.isPresent());  // Should be True
```

5. **Run integration test**
```bash
mvn test -Dtest=JpaRelationshipsIntegrationTest#testCreateStudentAndWallet
```

---

### 10. How do I deploy to production?

**Q**: What are the deployment steps?  
**A**:

1. **Build the JAR**
```bash
mvn clean package -DskipTests
# Creates: target/gns-0.0.1-SNAPSHOT.jar
```

2. **Configure Production Properties**
Set environment variables:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db.example.com:5432/miabeshop
export SPRING_DATASOURCE_USERNAME=miabeshop_user
export SPRING_DATASOURCE_PASSWORD=<secure-password>
export JWT_SECRET=<production-secret-key>
export SPRING_PROFILES_ACTIVE=prod
```

3. **Run the Application**
```bash
java -jar target/gns-0.0.1-SNAPSHOT.jar
```

4. **With Docker** (if Dockerfile exists)
```bash
docker build -t miabeshop/gns:latest .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/miabeshop \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  -e JWT_SECRET=<secret> \
  miabeshop/gns:latest
```

5. **With Docker Compose**
```bash
docker-compose up -d
# See docker-compose.yml for configuration
```

---

## 🐛 Troubleshooting

### Build Errors

#### Error: "cannot find symbol: class UUID"
```
Cause: Missing import
Solution: Add: import java.util.UUID;
```

#### Error: "cannot find symbol: method findByTrackingId"
```
Cause: Custom repository method not defined
Solution: Add to repository: Optional<Entity> findByTrackingId(UUID trackingId);
```

#### Error: "compilation failure" with 36 errors after JPA changes
```
Cause: Mappers still using old UUID foreign key pattern
Solution:
  1. Update mappers to use repository injection
  2. Replace UUID fields with @ManyToOne relationships
  3. Use: wallet.setStudent(student) not wallet.setStudentTrackingId(uuid)
  4. Run: mvn clean compile
```

---

### Runtime Errors

#### Error: "Connection refused" on PostgreSQL
```
Cause: PostgreSQL not running
Solution:
  brew services start postgresql  # macOS
  sudo service postgresql start   # Linux
  Check: psql -U postgres
```

#### Error: "Port 8080 already in use"
```
Cause: Another application using port 8080
Solution:
  # Find process using port 8080
  sudo lsof -i :8080
  # Kill process or use different port
  mvn spring-boot:run -Dserver.port=8081
```

#### Error: "GET /api/student returns 401 Unauthorized"
```
Cause: Missing JWT token in request header
Solution:
  1. Get token: POST /api/user/login
  2. Use token: Authorization: Bearer {token}
  3. Swagger/Swagger UI can auto-insert token
```

#### Error: "401 Unauthorized: Invalid token"
```
Cause: Token expired or invalid signature
Solution:
  1. Get new token from login
  2. Verify JWT_SECRET matches in dev/prod
  3. Check token format (should be: Bearer prefix + JWT)
```

#### Error: "NullPointerException accessing wallet.getStudent()"
```
Cause: Relationship not loaded from database
Solution:
  1. Check @JoinColumn name matches database
  2. Verify Hibernate SQL queries in logs (enable DEBUG logging)
  3. Use integration tests to validate mapping
  4. Check if entity has @ManyToOne with @JoinColumn
```

---

### Database Errors

#### Error: "relation \"students\" does not exist"
```
Cause: Table not created by Hibernate
Solution:
  1. Verify spring.jpa.hibernate.ddl-auto=create or update
  2. Check Entity class has @Entity annotation
  3. Restart application with clean database
```

#### Error: "Unique constraint violation" on secondary keys
```
Cause: Duplicate trackingId or unique field
Solution:
  1. Check if @Column(unique=true) is set
  2. Verify data doesn't have duplicates
  3. Clear database and restart
```

#### Error: "Foreign key constraint violation"
```
Cause: Referenced entity doesn't exist
Solution:
  1. Verify parent entity is created before child
  2. Check @JoinColumn references correct column
  3. Ensure studentRepository.findByTrackingId() returns valid entity
```

---

### Test Errors

#### Error: Test class not found with "mvn test -Dtest=ClassName"
```
Cause: File doesn't exist or wrong name
Solution:
  Full path: src/test/java/com/backend/gns/JpaRelationshipsIntegrationTest.java
  Command: mvn test -Dtest=JpaRelationshipsIntegrationTest
```

#### Error: "@SpringBootTest fails - Configuration not found"
```
Cause: Test properties file missing or inactive profile
Solution:
  1. Add @ActiveProfiles("test") annotation
  2. Create: src/main/resources/application-test.properties
  3. Use H2 database in test config
```

#### Error: "Test transaction not rolled back"
```
Cause: Missing @Transactional annotation
Solution:
  @SpringBootTest
  @Transactional  // Add this
  public class MyTest { }
```

---

### Performance Issues

#### Symptom: Application startup takes > 30 seconds
```
Cause: DDL scripts running or database connection slow
Solution:
  1. Set spring.jpa.hibernate.ddl-auto=validate in prod
  2. Pre-create database schema
  3. Use connection pooling (HikariCP configured)
  4. Check network latency to database
```

#### Symptom: Queries are slow (> 1 second for simple GET)
```
Cause: N+1 query problem or missing indexes
Solution:
  1. Use @EntityGraph to load relationships eagerly
  2. Check Hibernate SQL in logs
  3. Add database indexes on foreign keys
  4. Use projection DTOs instead of full entities
```

#### Symptom: Memory usage keeps increasing
```
Cause: Memory leak or large list accumulation
Solution:
  1. Implement pagination in getAll() methods
  2. Use streams instead of loading full lists
  3. Enable Spring Data pagination
  4. Monitor with: jps and jmap -heap
```

---

## ✨ Best Practices Checklist

Before committing code:

- [ ] Code compiles without errors: `mvn clean compile`
- [ ] All tests pass: `mvn test`
- [ ] No @Field injection warnings in logs
- [ ] Following naming conventions (Entity extends BaseEntity)
- [ ] Mappers use repository injection for JPA relations
- [ ] DTOs use Record syntax
- [ ] Services have interface + implementation
- [ ] Controllers return proper HTTP status codes
- [ ] Unit tests covering main paths
- [ ] Git commit message follows convention
- [ ] Sensitive data not committed (passwords, tokens)
- [ ] No TODO comments left in code

---

## 📞 Getting Help

### Debug Information to Provide

When reporting issues, include:
1. **Error message and stack trace**
2. **Reproduction steps**
3. **Environment**:
   ```bash
   java -version
   mvn -v
   git log --oneline -5
   ```
4. **Logs**:
   ```bash
   # From IDE console or:
   tail -f logs/app.log
   ```
5. **Database state**:
   ```bash
   # Test connection (when error is DB-related)
   psql -U postgres -c "SELECT 1"
   ```

### Useful Commands

```bash
# Clean rebuild (when in doubt)
mvn clean compile

# View dependency tree
mvn dependency:tree

# Check for security vulnerabilities
mvn dependency-check:check

# Format code
mvn spotless:apply

# Generate test coverage report
mvn clean test jacoco:report
# View: open target/site/jacoco/index.html

# View compilation warnings
mvn compile 2>&1 | grep warning
```

### Escalation Path (For Unresolved Issues)

1. Check this FAQ & troubleshooting guide
2. Review [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)
3. Check [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
4. Search project GitHub issues
5. Contact team lead with debug information above

---

**Documentation Last Updated**: April 2024  
**Questions? See**: [README.md](../../README.md) | [API_DOCUMENTATION.md](API_DOCUMENTATION.md) | [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)
