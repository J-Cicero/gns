# MiabéShop - GNS Backend API Documentation

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Database Schema & Relations](#database-schema--relations)
4. [API Endpoints](#api-endpoints)
5. [Authentication & Security](#authentication--security)
6. [Testing](#testing)
7. [Development Setup](#development-setup)

---

## 🎯 Project Overview

**MiabéShop** is a comprehensive Student Payment & Commerce Platform built with Spring Boot, JPA/Hibernate, and modern microservices patterns.

### Key Features
- ✅ **Student Management**: Complete lifecycle from registration to KYC validation
- ✅ **Merchant/Shop Management**: Multi-shop support with budget allocation
- ✅ **Payment System**: Multi-wallet architecture (Relais + Horizon)
- ✅ **Order Management**: Full order pipeline with payment tracking
- ✅ **Virtual Budget**: Monthly merchant budget management

---

## 🏗️ Architecture

### Technology Stack
```
Backend Framework:    Spring Boot 3.0+ (Java 17)
ORM:                  JPA/Hibernate
Database:             PostgreSQL
Security:             Spring Security + JWT
API Documentation:    SpringDoc OpenAPI/Swagger
Testing:              JUnit 5, MockMvc
```

### Project Structure (MVC Monolithic)
```
com.backend.gns/
├── domain/
│   ├── enums/              (10 Enums: StudentNiveau, PaiementType, etc.)
│   ├── models/             (9 @Entity classes with JPA relationships)
│   ├── services/           (9 interfaces + 9 ServiceImpl classes)
│   └── dtos/
│       ├── requests/       (9 Request records)
│       └── responses/      (9 Response records)
├── application/
│   ├── controllers/        (9 REST Controllers)
│   ├── mappers/            (9 @Component mappers)
│   └── dtos/               (User-related DTOs)
├── infrastructure/
│   └── repositories/       (9 JpaRepository interfaces)
└── Shared/
    ├── config/             (OpenAPI, JPA Auditing, etc.)
    └── security/           (JWT, CORS, Authentication, etc.)
```

---

## 🗄️ Database Schema & Relations

### Entity Relationships Diagram
![ER Diagram](docs/diagrams/miabeshop-er-diagram.drawio)

### Detailed Relations

#### **1. User Hierarchy (Single Table Inheritance)**
```java
User (base) @DiscriminatorColumn("user_type")
├── Student (@DiscriminatorValue("ETUDIANT"))
├── Merchant (@DiscriminatorValue("COMMERCANT"))
└── Admin (@DiscriminatorValue("ADMIN"))
```

**User Entity Fields:**
- `id` (Long, PK)
- `trackingId` (UUID, natural key exposed to client)
- `email` (unique)
- `password` (BCrypt encrypted)
- `nom`, `prenom`, `telephone`
- `dateInscription`, `dateNaissance`
- `role` (Enum: ETUDIANT, COMMERCANT, ADMIN, DBS)
- `estActif` (Boolean)

---

#### **2. Student-Wallet Relationship (1:N)**
```
Student (1) ───────── (N) Wallet
↑                           │
└── @OneToMany            └── @ManyToOne
    (mappedBy="student")       (student_id FK)
```

**Cascade Behavior:** 
- When Student is deleted → Wallets are deleted (ON_DELETE CASCADE)
- When Wallet is deleted → No impact on Student

**Use Case:**
- A student can have multiple wallets (Relais + Horizon types)
- Each wallet tracks balance independently

---

#### **3. Merchant-Product Relationship (1:N)**
```
Merchant (1) ──────── (N) Product
  │                       │
  └── @OneToMany        └── @ManyToOne
      (mappedBy="merchant")  (merchant_id FK)
```

**Use Case:**
- A merchant manages multiple products
- Each product belongs to exactly one merchant

---

#### **4. Merchant-BudgetVirtuel Relationship (1:N)**
```
Merchant (1) ───────── (N) BudgetVirtuel
  │                         │
  └── @OneToMany          └── @ManyToOne
      (mappedBy="merchant")  (merchant_id FK)
```

**Use Case:**
- Monthly budget allocation per merchant
- Track remaining vs allocated budget

---

#### **5. Commande Central Junction (N:N becomes 1:N:1)**
```
Student (1) ────┐     ┌──── (1) Wallet
               │     │
            Commande (1) ───── (N) Paiement
               │     │        ├─→ Wallet FK
           Merchant (1)      └─→ Commande FK
```

**Relations:**
- Student (N) ──── (1:N) ──── Commande (1:1 Student)
- Merchant (N) ──── (1:N) ──── Commande (1:1 Merchant)
- Commande (1) ──── (1:N) ──── Paiement (ManyToOne Commande)
- Paiement (N) ──── (ManyToOne) ──── Wallet

**Flow:**
1. Student creates Commande with Merchant
2. Commande references both Student and Merchant
3. Multiple Paiements can be created for single Commande (e.g., split payment between wallets)

---

#### **6. Versement-Wallet Relationship (N:1)**
```
Versement (N) ──── (1) Wallet
  │                    │
  └── @ManyToOne     └── @OneToMany
      (wallet_id FK)     (mappedBy="wallet")
```

**Use Case:**
- Track fund deposits/transfers to a wallet
- Versement statut: PROGRAMME, EXECUTE, EN_RETARD

---

### Summary of All Relations

| Source | Target | Type | Cascade | Use Case |
|--------|--------|------|---------|----------|
| Student | Wallet | 1:N | DELETE | Student has multiple wallets |
| Merchant | Product | 1:N | DELETE | Merchant lists products |
| Merchant | BudgetVirtuel | 1:N | DELETE | Monthly budget tracking |
| Merchant | Commande | 1:N | PERSIST | Merchant receives orders |
| Student | Commande | 1:N | PERSIST | Student places orders |
| Commande | Paiement | 1:N | DELETE | Order has payment records |
| Wallet | Paiement | 1:N | PERSIST | Wallet processes payment |
| Wallet | Versement | 1:N | PERSIST | Wallet receives transfers |

---

## 🔌 API Endpoints

### Authentication
```http
POST   /api/user/login                  → Get JWT Token
POST   /api/user/register               → Create new user
```

### Student Management
```http
POST   /api/student                     → Create student
GET    /api/student                     → Get all students
GET    /api/student/{trackingId}        → Get student details
PUT    /api/student/{trackingId}        → Update student
DELETE /api/student/{trackingId}        → Delete student
```

### Merchant Management
```http
POST   /api/merchant                    → Register merchant
GET    /api/merchant                    → List merchants
GET    /api/merchant/{trackingId}       → Get merchant
PUT    /api/merchant/{trackingId}       → Update merchant
DELETE /api/merchant/{trackingId}       → Delete merchant
```

### Wallet Management
```http
POST   /api/wallet                      → Create wallet (requires Student)
GET    /api/wallet                      → Get all wallets
GET    /api/wallet/{trackingId}         → Get wallet details
PUT    /api/wallet/{trackingId}         → Update wallet
DELETE /api/wallet/{trackingId}         → Delete wallet
```

### Product Management
```http
POST   /api/product                     → Add product (requires Merchant)
GET    /api/product                     → List all products
GET    /api/product/{trackingId}        → Get product details
PUT    /api/product/{trackingId}        → Update product
DELETE /api/product/{trackingId}        → Delete product
```

### Order Management
```http
POST   /api/commande                    → Create order
GET    /api/commande                    → Get all orders
GET    /api/commande/{trackingId}       → Get order details
PUT    /api/commande/{trackingId}       → Update order
DELETE /api/commande/{trackingId}       → Delete order
```

### Payment Management
```http
POST   /api/paiement                    → Record payment
GET    /api/paiement                    → Get all payments
GET    /api/paiement/{trackingId}       → Get payment details
```

### Budget Management
```http
POST   /api/budget-virtuel              → Allocate budget
GET    /api/budget-virtuel              → List budgets
GET    /api/budget-virtuel/{trackingId} → Get budget details
```

### Transfer Management (Versement)
```http
POST   /api/versement                   → Record transfer
GET    /api/versement                   → Get all transfers
GET    /api/versement/{trackingId}      → Get transfer details
```

### Documentation
```http
GET    /api/swagger-ui.html             → Swagger UI
GET    /api/v3/api-docs                 → OpenAPI JSON
```

---

## 🔐 Authentication & Security

### JWT Token Flow
```
1. User posts login credentials → POST /api/user/login
2. Server validates & returns JWT token
3. Client includes in Authorization header: Bearer {token}
4. Server validates token on each request via JwtAuthorizationToken filter
```

### Security Configuration
- **CORS**: Enabled for localhost:*, netlify.app, render.com
- **Session**: Stateless (STATELESS_CREATION_POLICY)
- **Password**: BCrypt hashing
- **Roles**: ETUDIANT, COMMERCANT, ADMIN, DBS

### Public URLs
```
POST   /api/user/login
POST   /api/user/register
GET    /api/swagger-ui.html
GET    /api/v3/api-docs
```

### Protected URLs (require roles)
- `/api/admin/**` → requires ADMIN role
- `/api/merchant/**` → requires COMMERCANT or ADMIN
- `/api/student/**` → requires ETUDIANT or ADMIN

---

## ✅ Testing

### Integration Tests
Run relation validation tests:
```bash
mvn test -Dtest=JpaRelationshipsIntegrationTest
```

### Test Classes Created
- `JpaRelationshipsIntegrationTest`: Validates Student-Wallet relationships

### Test Scenarios
1. ✅ Create Student and attach Wallet
2. ✅ Verify ManyToOne relationship
3. ✅ Verify OneToMany population
4. ✅ Multiple Wallets per Student

---

## 🚀 Development Setup

### Prerequisites
```bash
Java 17+
Maven 3.8+
PostgreSQL 12+
```

### Setup Steps

1. **Clone and Navigate**
```bash
git clone <repo>
cd gns
```

2. **Configure Database**
```bash
# src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/miabeshop
spring.datasource.username=postgres
spring.datasource.password=your_password
```

3. **Build Project**
```bash
mvn clean compile
```

4. **Run Tests**
```bash
mvn test
```

5. **Start Application**
```bash
mvn spring-boot:run
```

6. **Access Documentation**
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api/v3/api-docs

---

## 📊 Git Workflow

### Current Branch
```
security (feature branch with JWT implementation)
```

### Recent Commits
```
✅ feat: add complete JPA relationships to all entities
✅ feat: add CRUD operations for all entities  
✅ feat: configure OpenAPI/Swagger documentation
✅ feat: add integration tests for relations
```

---

## 📝 Next Steps

1. ✅ **JPA Relations** - Complete
2. ✅ **Swagger/OpenAPI** - Configured
3. ✅ **Integration Tests** - Created
4. ⏳ **Unit Tests** - To be added
5. ⏳ **API Validation** - To be added
6. ⏳ **Performance Tuning** - To be assessed

---

## 📞 Support

For questions or issues, contact the MiabéShop team at support@miabeshop.cm
