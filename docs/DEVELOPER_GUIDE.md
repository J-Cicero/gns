# 👨‍💻 Developer Guide - Code Conventions & Best Practices

## 📋 Table of Contents
1. [Naming Conventions](#naming-conventions)
2. [Code Patterns](#code-patterns)
3. [Entity Guidelines](#entity-guidelines)
4. [Service Layer Guidelines](#service-layer-guidelines)
5. [Controller Guidelines](#controller-guidelines)
6. [DTO & Mapper Guidelines](#dto--mapper-guidelines)
7. [Testing Guidelines](#testing-guidelines)
8. [Git Workflow](#git-workflow)

---

## 📝 Naming Conventions

### Files & Classes

#### Entity Classes
```java
// Pattern: [FunctionalName].java
// Location: src/main/java/.../domain/models/
// Extends: BaseEntity or User

User.java               // Base entity
Student.java           // Extends User
Merchant.java          // Extends User
Wallet.java            // Standalone entity
Commande.java          // Business entity (French: Order)
```

#### Enum Classes
```java
// Pattern: [ConceptName].java
// Location: src/main/java/.../domain/enums/
// Convention: SCREAM_CASE for values

StudentNiveau.java     // L1, L2, L3, M1, M2
PaiementType.java      // CARTE, VIR_MOBILE, VIR_LVL, PAIEMENT_DIRECT
PaiementStatut.java    // INITIATED, COMPLETED, FAILED, CANCELLED
```

#### DTO Classes
```java
// Request: [Entity]Request.java (record)
StudentRequest.java

// Response: [Entity]Response.java (record)
StudentResponse.java

// Location: src/main/java/.../domain/dtos/ or .../application/dtos/
```

#### Service Classes
```java
// Interface: [Entity]Service.java
// Location: src/main/java/.../domain/services/

StudentService.java

// Implementation: [Entity]ServiceImpl.java
// Location: src/main/java/.../domain/services/impl/

StudentServiceImpl.java
```

#### Controller Classes
```java
// Pattern: [Entity]Controller.java
// Location: src/main/java/.../application/controllers/
// Annotation: @RestController
// Path: /api/{entity-plural}

StudentController.java    // @RequestMapping("/api/student")
MerchantController.java   // @RequestMapping("/api/merchant")
```

#### Repository Interfaces
```java
// Pattern: [Entity]Repository.java
// Extends: JpaRepository<Entity, ID>
// Location: src/main/java/.../infrastructure/repositories/

StudentRepository.java
WalletRepository.java
```

#### Mapper Classes
```java
// Pattern: [Entity]Mapper.java
// Annotation: @Component
// Location: src/main/java/.../application/mappers/

StudentMapper.java       // Maps Student ↔ StudentRequest/Response
PaiementMapper.java      // Maps Paiement ↔ PaiementRequest/Response
```

---

### Variables & Fields

```java
// Entity IDs
private Long id;                          // Internal identifier
private UUID trackingId;                  // External API identifier

// Boolean flags (prefix: is, est, has)
private boolean estActif;                 // French: is active
private boolean isSubscribed;             // English variant
private boolean hasKyc;                   // English: has KYC

// Collections
private List<Wallet> wallets;             // Plural for collections
private Set<String> roles;                // Set for unique values
private Map<String, Object> metadata;     // Use Map for key-value

// Foreign key relationships (after JPA migration)
private Student student;                  // @ManyToOne references
private List<Wallet> wallets;             // @OneToMany collections
```

### Method Names

```java
// Service/Repository methods
create(Request)                           // Create new entity
getAll()                                  // Get all entities
getByTrackingId(UUID)                     // Get by public ID
getById(Long)                             // Get by internal ID
update(UUID, Request)                     // Update entity
delete(UUID)                              // Delete entity

// Mapper methods
toEntity(Request)                         // Request → Entity
toEntity(Response)                        // Response → Entity
toResponse(Entity)                        // Entity → Response

// Custom query methods
findByEmail(String)                       // Repository method
findByTrackingId(UUID)                    // Repository method (custom)
findByStudent(Student)                    // Repository method
```

---

## 🔧 Code Patterns

### 1. Single-Table Inheritance Pattern

```java
// Parent Entity
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name = "user_type",
    discriminatorType = DiscriminatorType.STRING
)
public class User extends BaseEntity {
    private String email;
    private String password;
    private String nom;
    private String prenom;
    // ... common fields
}

// Child Entity
@Entity
@DiscriminatorValue("ETUDIANT")
public class Student extends User {
    private String matriculeUL;
    private StudentNiveau niveau;
    
    @OneToMany(mappedBy = "student")
    private List<Wallet> wallets;
}

// Usage Query
List<Student> students = studentRepository.findAll();  // Only Student records
List<User> allUsers = userRepository.findAll();        // All user types
```

**When to Use**: Multiple entities sharing common attributes (User → Student/Merchant/Admin)

---

### 2. Mapper with Repository Injection Pattern

```java
@Component
public class WalletMapper {
    private final StudentRepository studentRepository;
    
    public WalletMapper(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    // Request → Entity (resolve relationships)
    public Wallet toEntity(WalletRequest request) {
        Student student = studentRepository.findByTrackingId(request.studentTrackingId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Student with id " + request.studentTrackingId() + " not found"
            ));
        
        Wallet wallet = Wallet.builder()
            .typeWallet(WalletType.valueOf(request.typeWallet()))
            .student(student)  // Set relationship (not UUID)
            .balanceRelais(BigDecimal.ZERO)
            .balanceHorizon(BigDecimal.ZERO)
            .build();
        
        return wallet;
    }
    
    // Entity → Response (expose relationships)
    public WalletResponse toResponse(Wallet wallet) {
        return new WalletResponse(
            wallet.getTrackingId(),
            wallet.getTypeWallet().name(),  // Enum → String
            wallet.getStudent().getTrackingId(),  // Entity → UUID
            wallet.getBalanceRelais(),
            wallet.getBalanceHorizon(),
            wallet.getEstActif()
        );
    }
}
```

**Key Points**:
- Inject repositories needed for resolving foreign keys
- Use `.findByTrackingId()` to fetch related entities
- Set relationship objects, not UUID strings
- Use `.name()` for enum-to-string conversion
- Throw descriptive exceptions for missing entities

---

### 3. TrackingId (UUID) Pattern

```java
@Entity
public class Student extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Internal PK (never exposed)
    
    @Column(unique = true, nullable = false)
    private UUID trackingId;  // External API identifier
    
    @PrePersist
    public void generateTrackingId() {
        if (trackingId == null) {
            trackingId = UUID.randomUUID();
        }
    }
}

// In Repository
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByTrackingId(UUID trackingId);
    Optional<Student> findByEmail(String email);
}

// In Controller
@GetMapping("/{trackingId}")
public ResponseEntity<StudentResponse> getStudent(@PathVariable UUID trackingId) {
    StudentResponse response = studentService.getByTrackingId(trackingId);
    return ResponseEntity.ok(response);
}
```

**Benefits**:
- API uses UUIDs (user-friendly, non-sequential)
- Database uses Long IDs (efficient, indexed)
- Immutable tracking IDs (can't accidentally modify)

---

### 4. Service Layer CRUD Pattern

```java
@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    
    public StudentServiceImpl(StudentRepository repository, StudentMapper mapper) {
        this.studentRepository = repository;
        this.studentMapper = mapper;
    }
    
    @Override
    public StudentResponse create(StudentRequest request) {
        Student student = studentMapper.toEntity(request);
        Student saved = studentRepository.save(student);
        return studentMapper.toResponse(saved);
    }
    
    @Override
    public List<StudentResponse> getAll() {
        return studentRepository.findAll()
            .stream()
            .map(studentMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public StudentResponse getByTrackingId(UUID trackingId) {
        Student student = studentRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Student with id " + trackingId + " not found"
            ));
        return studentMapper.toResponse(student);
    }
    
    @Override
    @Transactional
    public StudentResponse update(UUID trackingId, StudentRequest request) {
        Student student = studentRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Student with id " + trackingId + " not found"
            ));
        
        // Update fields
        student.setNom(request.nom());
        student.setPrenom(request.prenom());
        // ... other fields
        
        Student updated = studentRepository.save(student);
        return studentMapper.toResponse(updated);
    }
    
    @Override
    public void delete(UUID trackingId) {
        Student student = studentRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Student with id " + trackingId + " not found"
            ));
        studentRepository.delete(student);
    }
}
```

---

### 5. REST Controller Pattern

```java
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    
    @PostMapping
    public ResponseEntity<StudentResponse> create(@RequestBody StudentRequest request) {
        StudentResponse response = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll() {
        List<StudentResponse> responses = studentService.getAll();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/{trackingId}")
    public ResponseEntity<StudentResponse> getOne(@PathVariable UUID trackingId) {
        StudentResponse response = studentService.getByTrackingId(trackingId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{trackingId}")
    public ResponseEntity<StudentResponse> update(
        @PathVariable UUID trackingId,
        @RequestBody StudentRequest request
    ) {
        StudentResponse response = studentService.update(trackingId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{trackingId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
        studentService.delete(trackingId);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 🏢 Entity Guidelines

### Required Annotations

```java
@Entity                                   // JPA entity
@Table(name = "students")                // Optional: custom table name
@builder                                  // Lombok builder
@Data                                     // Lombok: getters, setters, equals, hashCode, toString
@NoArgsConstructor                        // Lombok: no-arg constructor
@AllArgsConstructor                       // Lombok: all-arg constructor
public class Student extends User {
    // ...
}
```

### All Entities Must Extend BaseEntity

```java
@Entity
public class Wallet extends BaseEntity {
    // Inherits: id, trackingId, createdAt, updatedAt, createdBy, updatedBy
}
```

### Relationships

```java
// Many-to-One (foreign key side)
@ManyToOne
@JoinColumn(name = "student_id", nullable = false)
private Student student;

// One-to-Many (inverse side)
@OneToMany(mappedBy = "student")
private List<Wallet> wallets;

// NEVER use old UUID pattern
// ❌ WRONG: private UUID studentTrackingId;
// ✅ RIGHT: private Student student; with @ManyToOne
```

### Enums

```java
@Column(columnDefinition = "VARCHAR(30)")
@Enumerated(EnumType.STRING)              // Store as STRING, not ordinal
private StudentNiveau niveau;             // Use entity property

// Mapping in DTO
studentResponse = new StudentResponse(
    student.getNiveau().name()            // Enum → String in response
);
```

---

## 🔧 Service Layer Guidelines

### Always Use Interfaces

```java
public interface StudentService {
    StudentResponse create(StudentRequest request);
    List<StudentResponse> getAll();
    StudentResponse getByTrackingId(UUID trackingId);
    StudentResponse update(UUID trackingId, StudentRequest request);
    void delete(UUID trackingId);
}

@Service
public class StudentServiceImpl implements StudentService {
    // Implementation
}
```

### Constructor Injection (Never Field Injection)

```java
// ✅ CORRECT
@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    
    public StudentServiceImpl(StudentRepository repository, StudentMapper mapper) {
        this.studentRepository = repository;
        this.studentMapper = mapper;
    }
}

// ❌ WRONG - Field injection
@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;
}
```

### Exception Handling

```java
// Throw specific exceptions
public StudentResponse getByTrackingId(UUID trackingId) {
    return studentRepository.findByTrackingId(trackingId)
        .map(studentMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Student with id " + trackingId + " not found"
        ));
}
```

---

## 🌐 Controller Guidelines

### REST Conventions

```
POST   /api/resource              → 201 Created
GET    /api/resource              → 200 OK (list)
GET    /api/resource/{id}         → 200 OK (single)
PUT    /api/resource/{id}         → 200 OK
DELETE /api/resource/{id}         → 204 No Content
```

### Always Return Appropriate Status Codes

```java
@PostMapping
public ResponseEntity<StudentResponse> create(@RequestBody StudentRequest request) {
    StudentResponse response = studentService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);  // 201
}

@GetMapping("/{trackingId}")
public ResponseEntity<StudentResponse> getOne(@PathVariable UUID trackingId) {
    StudentResponse response = studentService.getByTrackingId(trackingId);
    return ResponseEntity.ok(response);  // 200
}

@DeleteMapping("/{trackingId}")
public ResponseEntity<Void> delete(@PathVariable UUID trackingId) {
    studentService.delete(trackingId);
    return ResponseEntity.noContent().build();  // 204
}
```

### Use @RequestBody and Validate

```java
@PostMapping
public ResponseEntity<StudentResponse> create(
    @Valid @RequestBody StudentRequest request  // @Valid for validation
) {
    StudentResponse response = studentService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

---

## 📦 DTO & Mapper Guidelines

### DTO Records (Immutable)

```java
// Request DTO
public record StudentRequest(
    @NotBlank(message = "Email is required")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,
    
    @NotBlank(message = "Nom is required")
    String nom,
    
    @NotBlank(message = "Prenom is required")
    String prenom,
    
    @NotBlank(message = "Matricule is required")
    String matriculeUL,
    
    @NotNull(message = "Niveau is required")
    String niveau  // Enum as String in DTO
) {}

// Response DTO
public record StudentResponse(
    UUID trackingId,
    String email,
    String nom,
    String prenom,
    String matriculeUL,
    String niveau,  // Enum converted to String
    Integer creditsValides,
    String statutKyc
) {}
```

### Mapper Responsibility

```java
@Component
public class StudentMapper {
    private final StudentRepository studentRepository;
    
    public StudentMapper(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    // 1. Convert JSON Request → Validated Entity
    public Student toEntity(StudentRequest request) {
        return Student.builder()
            .email(request.email())
            .password(new BCryptPasswordEncoder().encode(request.password()))
            .nom(request.nom())
            .prenom(request.prenom())
            .matriculeUL(request.matriculeUL())
            .niveau(StudentNiveau.valueOf(request.niveau()))  // String → Enum
            .build();
    }
    
    // 2. Convert Entity → JSON Response
    public StudentResponse toResponse(Student student) {
        return new StudentResponse(
            student.getTrackingId(),
            student.getEmail(),
            student.getNom(),
            student.getPrenom(),
            student.getMatriculeUL(),
            student.getNiveau().name(),  // Enum → String
            student.getCreditsValides(),
            student.getStatutKyc().name()
        );
    }
}
```

---

## ✅ Testing Guidelines

### Integration Test Pattern

```java
@SpringBootTest
@ActiveProfiles("test")  // Use H2 in-memory DB
@Transactional           // Rollback after each test
public class StudentServiceIntegrationTest {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private StudentService studentService;
    
    @Test
    public void testCreateStudent() {
        // Given
        StudentRequest request = new StudentRequest(
            "jean@test.com",
            "password123",
            "Traoré",
            "Jean",
            "STU2024001",
            "L1"
        );
        
        // When
        StudentResponse response = studentService.create(request);
        
        // Then
        assertNotNull(response.trackingId());
        assertEquals("jean@test.com", response.email());
        assertEquals("L1", response.niveau());
    }
    
    @Test
    public void testGetByTrackingId() {
        // Given - create student first
        Student student = studentRepository.save(
            Student.builder()
                .email("marie@test.com")
                .password("password123")
                .nom("Martin")
                .prenom("Marie")
                .matriculeUL("STU2024002")
                .niveau(StudentNiveau.L2)
                .build()
        );
        
        // When
        StudentResponse response = studentService.getByTrackingId(student.getTrackingId());
        
        // Then
        assertEquals(student.getTrackingId(), response.trackingId());
        assertEquals("marie@test.com", response.email());
    }
    
    @Test
    public void testDeleteStudent() {
        // Given
        Student student = studentRepository.save(
            Student.builder()
                .email("test@test.com")
                .build()
        );
        UUID trackingId = student.getTrackingId();
        
        // When
        studentService.delete(trackingId);
        
        // Then
        assertFalse(studentRepository.findByTrackingId(trackingId).isPresent());
    }
}
```

### Assertions

```java
import static org.junit.jupiter.api.Assertions.*;

assertEquals(expected, actual);
assertNotNull(value);
assertTrue(condition);
assertFalse(condition);
assertThrows(Exception.class, () -> { ... });
```

---

## 🌳 Git Workflow

### Branch Naming

```
feature/add-wallet-balance-calculation
bugfix/fix-jwt-token-expiration
hotfix/critical-security-issue
release/v0.1.0
```

### Commit Messages

```
feat: add wallet balance calculation
fix: correct JWT token expiration
docs: update API documentation
test: add integration tests for relations
refactor: simplify StudentService implementation
style: format code according to conventions
```

### Before Pushing

```bash
# 1. Pull latest changes
git pull origin feature/your-branch

# 2. Run tests
mvn clean test

# 3. Check compilation
mvn clean compile

# 4. Format code (if Spotless configured)
mvn spotless:apply

# 5. Commit
git add .
git commit -m "feat: your message"

# 6. Push
git push origin feature/your-branch

# 7. Create Pull Request
```

---

## ✨ Quick Reference Checklist

When creating a new entity:

- [ ] Create `[Entity].java` in `domain/models/`
- [ ] Extend `BaseEntity` (for id, trackingId, timestamps)
- [ ] Add `@Entity`, `@Data`, `@Builder`, `@NoArgsConstructor`
- [ ] Create `[Entity]Repository` extending `JpaRepository`
- [ ] Create `[Entity]Service` interface
- [ ] Create `[Entity]ServiceImpl` implementing service
- [ ] Create `[Entity]Mapper` with `@Component`
- [ ] Create `[Entity]Request` and `[Entity]Response` DTOs
- [ ] Create `[Entity]Controller` with all CRUD endpoints
- [ ] Add relationships via `@ManyToOne`/`@OneToMany` (NOT UUIDs)
- [ ] Add tests in `src/test/java/`
- [ ] Update API documentation in [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- [ ] Commit with message: `feat: add [Entity] entity with CRUD`

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [Project README](../../README.md)
- [API Documentation](API_DOCUMENTATION.md)

