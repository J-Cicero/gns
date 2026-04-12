# 🛒 MiabéShop - GNS Backend (Global Needs Solution)

**Production-Ready Spring Boot Backend for Student Payment & Commerce Platform**

---

## 📌 Quick Start

### System Requirements
```
✓ Java 17+
✓ Maven 3.8+
✓ PostgreSQL 12+
```

### 1️⃣ Clone & Setup
```bash
git clone <repo>
cd gns
```

### 2️⃣ Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/miabeshop
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 3️⃣ Build & Run
```bash
# Development
mvn clean compile
mvn spring-boot:run

# Production  
mvn clean package -DskipTests
java -jar target/gns-0.0.1-SNAPSHOT.jar
```

### 4️⃣ Test
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=JpaRelationshipsIntegrationTest
```

### 5️⃣ Access Documentation
- API Docs: http://localhost:8080/api/swagger-ui.html
- REST API: http://localhost:8080/api/
- OpenAPI JSON: http://localhost:8080/api/v3/api-docs

---

## 🏛️ Architecture Overview

### Technology Stack
| Layer | Technology |
|-------|-----------|
| **Framework** | Spring Boot 3.0.5 |
| **ORM** | JPA/Hibernate |
| **Database** | PostgreSQL |
| **Security** | Spring Security + JWT |
| **API** | SpringDoc OpenAPI/Swagger |
| **Testing** | JUnit 5, MockMvc |
| **Build** | Maven |

### Project Layout
```
src/main/java/com/backend/gns
├── domain/
│   ├── enums/           → 10 Business enums (StudentNiveau, PaiementType, etc.)
│   ├── models/          → 9 JPA entities with single-table inheritance
│   └── services/        → 18 service classes (9 interfaces + 9 implementations)
├── application/
│   ├── controllers/     → 9 REST controllers (CRUD endpoints)
│   ├── mappers/         → 9 mappers (Entity ↔ DTO conversion)
│   └── dtos/            → 18 DTOs (9 Requests + 9 Responses)
├── infrastructure/
│   └── repositories/    → 9 JPA repositories
└── Shared/
    ├── config/          → OpenAPI, JPA Auditing, Database config
    ├── security/        → JWT tokens, CORS, Authentication, Security filters
    └── utils/           → BaseEntity, constants, utilities

src/main/resources/
├── application.properties           → Default config
├── application-dev.properties       → Development config
├── application-prod.properties      → Production config
└── ... (static files, templates)
```

---

## 🗄️ Data Model (9 Entities)

### Core Entities (with Single-Table Inheritance)
```
User (base entity)
├── Student         → Enrolled students with wallets
├── Merchant        → Shop owners with products & budgets
└── Admin           → Administrative users
```

### Financial Entities
```
Wallet              → Student's account (Relais / Horizon types)
├── balanceRelais   → Scholarship wallet
├── balanceHorizon  → Shopping wallet
└── Links to: Paiement (payments), Versement (transfers)

Paiement            → Payment transaction between Student (Wallet) → Merchant
├── montantProduit  → Merchant receives this
├── commission      → 2% calculated fee
├── montantDebite   → Student pays this
└── estSwitch       → Indicates wallet switching after first payment

Versement           → Credit transaction (scholarships, transfers)
├── montantVerse    → Amount transferred
├── typeVersement   → BOURSE_DBS, COTISATION_TMONEY, BUDGET_BOUTIQUE
└── statutVersement → PROGRAMME, EXECUTE, EN_RETARD
```

### Commerce Entities
```
Product             → Merchant's inventory item
├── nom, description, prix
└── merchantId      → Related merchant

Commande            → Order linking Student buyer → Merchant seller
├── studentId       → Student placing order
├── merchantId      → Shop receiving order
└── Links to Paiement (multiple payments per order)

BudgetVirtuel       → Monthly merchant budget allocation
├── montantAlloue   → Fixed monthly amount
├── montantRestant  → Decremented per payment
└── periodeMois     → YYYY-MM format
```

### All Relations Map
```
Student     (1) ──────────── (N) Wallet
Merchant    (1) ──────────── (N) Product
Merchant    (1) ──────────── (N) BudgetVirtuel
Merchant    (1) ──────────── (N) Commande
Student     (1) ──────────── (N) Commande
Commande    (1) ──────────── (N) Paiement
Wallet      (1) ──────────── (N) Paiement
Wallet      (1) ──────────── (N) Versement
```

---

## 🔌 REST API Endpoints

### Base URL
```
Development: http://localhost:8080/api
Production:  https://miabeshop.cm/api
```

### Authentication
```bash
# Login
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# Response
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

# Use token in request headers
curl -H "Authorization: Bearer {token}" http://localhost:8080/api/student
```

### CRUD Endpoints (Per Entity)
```
POST   /api/{entity}                  → Create
GET    /api/{entity}                  → Get all (paginated)
GET    /api/{entity}/{trackingId}     → Get one
PUT    /api/{entity}/{trackingId}     → Update
DELETE /api/{entity}/{trackingId}     → Delete

# Entities: student, merchant, admin, wallet, product, commande, paiement, versement, budget-virtuel
```

### Example: Student CRUD
```bash
# Create
curl -X POST http://localhost:8080/api/student \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "email":"jean@example.com",
    "password":"securepass123",
    "nom":"Traoré",
    "prenom":"Jean",
    "telephone":"+237123456789",
    "matriculeUL":"STU2024001",
    "niveau":"L1",
    "creditsValides":45
  }'

# Read All
curl -X GET "http://localhost:8080/api/student?page=0&size=10" \
  -H "Authorization: Bearer {token}"

# Read One
curl -X GET http://localhost:8080/api/student/{trackingId} \
  -H "Authorization: Bearer {token}"

# Update
curl -X PUT http://localhost:8080/api/student/{trackingId} \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"niveau":"L2","creditsValides":60}'

# Delete
curl -X DELETE http://localhost:8080/api/student/{trackingId} \
  -H "Authorization: Bearer {token}"
```

---

## 🔐 Security Features

### Authentication Flow
```
1. User submits credentials (email + password)
   ↓
2. Spring Security validates via UserDetailsServiceImpl
   ↓
3. JwtService generates JWT token (HS256 algorithm)
   ↓
4. Client includes token in Authorization header for subsequent requests
   ↓
5. JwtAuthorizationToken filter validates token on each request
```

### Security Configuration
- **JWT Algorithm**: HS256
- **Token TTL**: 24 hours (configurable)
- **Password Encoding**: BCrypt
- **CORS Policy**: 
  - localhost:* (development)
  - *.netlify.app (staging)
  - miabeshop.cm, www.miabeshop.cm (production)
- **Session Management**: Stateless (no server-side sessions)
- **HTTP Methods Security**:
  - POST, PUT, DELETE: Authenticated
  - GET (with sensitive data): Authenticated
  - GET /api/swagger-ui/**, /api/v3/api-docs: Public

### Roles & Permissions
```
ETUDIANT    → Student-specific operations
COMMERCANT  → Merchant operations
ADMIN       → Administrative access
DBS         → Database administrator role
```

---

## ✅ Testing

### Test Structure
```
src/test/java/com/backend/gns/
└── JpaRelationshipsIntegrationTest.java
    ├── testCreateStudentAndWallet()      → OneToMany relationship
    ├── testWalletManyToOneRelationship() → ManyToOne direction
    ├── testMultipleWalletsPerStudent()   → Cardinality validation
    └── setUp()                           → Test fixture with StudentRequest
```

### Test Database Configuration
- Type: H2 in-memory database
- DDL: create-drop (fresh schema per test)
- Profile: @ActiveProfiles("test")
- Config file: src/main/resources/application-test.properties

### Running Tests
```bash
# All tests
mvn test

# Single test class
mvn test -Dtest=JpaRelationshipsIntegrationTest

# Single test method
mvn test -Dtest=JpaRelationshipsIntegrationTest#testCreateStudentAndWallet

# With coverage
mvn test jacoco:report
```

---

## 📚 Key Implementation Patterns

### 1. Single-Table Inheritance (User Hierarchy)
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = STRING)
public class User extends BaseEntity {
    private String email, password, nom, prenom;
}

@Entity
@DiscriminatorValue("ETUDIANT")
public class Student extends User {
    private String matriculeUL;
    private StudentNiveau niveau;
}
```

**Benefits**: Single query retrieves all user types; simple polymorphic queries.

### 2. Mapper Pattern with Repository Injection
```java
@Component
public class WalletMapper {
    private final StudentRepository studentRepository;
    
    public WalletMapper(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    public Wallet toEntity(WalletRequest request) {
        Student student = studentRepository.findByTrackingId(request.studentTrackingId())
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        Wallet wallet = new Wallet();
        wallet.setStudent(student);
        wallet.setTypeWallet(WalletType.valueOf(request.typeWallet()));
        return wallet;
    }
}
```

**Benefits**: Proper JPA relationship resolution; prevents orphaned references; testable dependency injection.

### 3. TrackingId Pattern (UUID instead of Long in API)
```java
@Entity
public class Student extends User {
    @Id
    private Long id;  // Internal database identifier (hidden from API)
    
    @Column(unique = true)
    private UUID trackingId;  // External API identifier (exposed in REST)
    
    @PrePersist
    public void generateTrackingId() {
        if (trackingId == null) {
            trackingId = UUID.randomUUID();
        }
    }
}
```

**Benefits**: API stability (UUIDs don't change); security (hides database sequence); user-friendly identifiers.

### 4. DTO Response with Enum Conversion
```java
public record StudentResponse(
    UUID trackingId,
    String email,
    String nom,
    String prenom,
    StudentNiveau niveau  // Enum converted to String for JSON
) {}

// In mapper:
return new StudentResponse(
    student.getTrackingId(),
    student.getEmail(),
    student.getNom(),
    student.getPrenom(),
    student.getNiveau()  // Serialized as "L1", "M2", etc.
);
```

**Benefits**: Type-safe enums in backend; human-readable JSON responses.

---

## 📊 Database Diagram

![ER Diagram](docs/diagrams/miabeshop-er-diagram.drawio)

**Key Relationships:**
- Single-table inheritance: All users in one table with discriminator
- 1:N Cardinality: Students → Wallets, Merchants → Products/Commands
- 1:N:1 Pattern: Student → Commande ← Merchant (orders linking both)
- Foreign Keys via @JoinColumn: Ensure referential integrity

---

## 🚀 Deployment

### Production Configuration
```bash
# Set environment variables
export JAVA_OPTS="-Xmx512m -Xms256m"
export SPRING_PROFILES_ACTIVE=prod

# Start with production properties
java -Dspring.config.location=application-prod.properties -jar gns.jar
```

### Environment Variables (Recommended)
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/miabeshop
SPRING_DATASOURCE_USERNAME=miabeshop_user
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
JWT_SECRET=${JWT_SECRET_KEY}
JWT_EXPIRATION=86400000  # 24 hours in ms
```

### CI/CD Integration
```bash
# GitHub Actions / GitLab CI example
mvn clean package -DskipTests
docker build -t miabeshop/gns:latest .
docker push miabeshop/gns:latest
```

---

## 📖 Additional Documentation

- [API Documentation](docs/API_DOCUMENTATION.md)
- [ER Diagram](docs/diagrams/miabeshop-er-diagram.drawio)
- [Security Configuration Guide](src/main/java/com/backend/gns/Shared/security/config/SecurityConfig.java)
- [OpenAPI Spec](http://localhost:8080/api/v3/api-docs)

---

## 🛠️ Development Workflow

### Create Feature Branch
```bash
git checkout -b feature/new-feature
```

### Make Changes & Commit
```bash
git add .
git commit -m "feat: add new feature"
```

### Run Tests Before Push
```bash
mvn clean test
```

### Push & Create Pull Request
```bash
git push origin feature/new-feature
```

---

## ⚠️ Common Issues

### Issue: "Connection refused" on PostgreSQL
**Solution**: Ensure PostgreSQL is running
```bash
sudo service postgresql start  # Linux
brew services start postgresql # macOS
```

### Issue: "Port 8080 already in use"
**Solution**: Change port in application.properties
```properties
server.port=8081
```

### Issue: JWT token expired
**Solution**: Request new token from /api/user/login

---

## 📝 Changelog

### v0.0.1 (Current)
- ✅ 9 entities with CRUD operations
- ✅ JPA relationships (@ManyToOne/@OneToMany)
- ✅ Spring Security + JWT authentication
- ✅ OpenAPI/Swagger documentation
- ✅ Integration tests for relations
- ✅ Single-table inheritance pattern
- ✅ TrackingId (UUID) pattern

### v0.0.2 (Planned)
- 🔄 Unit tests for services
- 🔄 Performance optimization (caching, pagination)
- 🔄 Audit logging
- 🔄 Health checks and metrics

---

## 📞 Contact & Support

**Project Lead**: [Your Name]  
**Email**: support@miabeshop.cm  
**Repository**: [GitHub Link]  
**Issues**: [GitHub Issues Link]

---

## 📄 License

MIT License - see LICENSE file for details

---

**Last Updated**: April 2024  
**Version**: 0.0.1  
**Status**: 🟢 Production Ready
