# 📚 MiabéShop Documentation Index

Welcome to the MiabéShop (GNS - Global Needs Solution) Backend Documentation!

---

## 🎯 Quick Navigation

### For New Developers
Start with these in order:
1. **[README.md](../README.md)** - Project overview, quick start, tech stack
2. **[DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)** - Code conventions, patterns, best practices
3. **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)** - REST endpoints, data models

### For API Integration
Go to **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)**
- REST Endpoints reference
- Authentication & JWT tokens
- Request/Response examples
- Error codes

### For Security & Authentication
Go to **[SECURITY_JWT_GUIDE.md](SECURITY_JWT_GUIDE.md)**
- JWT token flow
- Configuration files
- Security best practices
- Troubleshooting token issues

### For Troubleshooting
Go to **[FAQ_TROUBLESHOOTING.md](FAQ_TROUBLESHOOTING.md)**
- Common issues & solutions
- Build errors
- Runtime errors
- Database problems
- Performance optimization

### For Architecture & Database
Go to **[ARCHITECTURE.md](ARCHITECTURE.md)** (if exists) or **[API_DOCUMENTATION.md](API_DOCUMENTATION.md#-database-schema--relations)**
- Entity relationships
- Database schema
- ER diagram

---

## 📖 Complete Documentation Files

### 🏠 [README.md](../README.md)
**Main entry point for the project**
- Quick start guide
- Technology stack overview
- Architecture overview
- Initial setup instructions
- REST API guidelines
- Deployment instructions

**Read this if**: You're starting the project for the first time

---

### 👨‍💻 [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)
**Code standards and development patterns**
- Naming conventions (files, classes, methods, variables)
- Code patterns (inheritance, mappers, DTOs)
- Entity guidelines
- Service layer patterns
- Controller guidelines
- Testing guidelines
- Git workflow

**Read this if**: You're writing code or reviewing PRs

---

### 🔌 [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
**REST API reference and database design**
- Project overview & features
- Architecture details
- Database schema & relationships
- All REST endpoints (CRUD for 9 entities)
- Authentication & security config
- Testing procedures
- Development setup

**Read this if**: You're integrating with the API or understanding data models

---

### 🔐 [SECURITY_JWT_GUIDE.md](SECURITY_JWT_GUIDE.md)
**Security implementation & JWT configuration**
- JWT authentication flow
- Token generation & validation
- Configuration files (dev/prod/test)
- JwtService implementation
- JWT filter implementation
- SecurityConfig overview
- Login example requests
- Error responses
- Security best practices
- Environment variables setup
- Postman testing guide
- Token renewal strategies
- Troubleshooting guide

**Read this if**: You're debugging authentication issues or configuring security

---

### 🆘 [FAQ_TROUBLESHOOTING.md](FAQ_TROUBLESHOOTING.md)
**Common questions and solutions**
- 10 FAQs (start development, run tests, create entities, etc.)
- Build and compilation errors
- Runtime errors
- Database connection issues
- JWT/authentication problems
- Relationship debugging
- Database errors
- Test failures
- Performance issues
- Debug information checklist
- Useful commands

**Read this if**: You encounter an error or have a common question

---

## 📊 Architecture Diagram Files

### [diagrams/miabeshop-er-diagram.drawio](diagrams/miabeshop-er-diagram.drawio)
**Entity-Relationship Diagram**
- Visual representation of all 9 entities
- Relationship cardinality (1:N, 1:1, N:M)
- Attribute listing per entity
- Single-table inheritance visualization

**View this if**: You want to understand the database structure visually

---

## 🗂️ Documentation Structure

```
docs/
├── README.md                          ← You are here (INDEX)
├── API_DOCUMENTATION.md               ← REST API reference
├── DEVELOPER_GUIDE.md                 ← Code conventions
├── SECURITY_JWT_GUIDE.md              ← Authentication guide
├── FAQ_TROUBLESHOOTING.md             ← Common issues
└── diagrams/
    └── miabeshop-er-diagram.drawio    ← Database visual
```

---

## 🧭 Navigation by Task

### "I need to..."

#### Setup & Running
- **...get started?** → [README.md](../README.md#-quick-start)
- **...run the server locally?** → [README.md](../README.md#1️⃣-clone--setup)
- **...run tests?** → [API_DOCUMENTATION.md](API_DOCUMENTATION.md#-testing) or [FAQ](FAQ_TROUBLESHOOTING.md#3-how-do-i-run-tests)

#### Understanding the System
- **...understand the architecture?** → [README.md](../README.md#-architecture-overview)
- **...see all the entities?** → [API_DOCUMENTATION.md](API_DOCUMENTATION.md#-database-schema--relations)
- **...understand the relationships?** → [API_DOCUMENTATION.md](API_DOCUMENTATION.md#entity-relationships-diagram)

#### Development
- **...add a new entity?** → [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md#-entity-guidelines) & [FAQ](FAQ_TROUBLESHOOTING.md#5-how-do-i-create-a-new-entity)
- **...follow coding standards?** → [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) (entire file)
- **...implement a service?** → [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md#-service-layer-guidelines)
- **...create REST endpoints?** → [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md#-controller-guidelines)
- **...write tests?** → [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md#-testing-guidelines)

#### API Usage
- **...see all endpoints?** → [API_DOCUMENTATION.md](API_DOCUMENTATION.md#-rest-api-endpoints)
- **...authenticate/login?** → [SECURITY_JWT_GUIDE.md](SECURITY_JWT_GUIDE.md#user-login-example)
- **...get a JWT token?** → [FAQ](FAQ_TROUBLESHOOTING.md#7-how-do-i-get-a-valid-jwt-token) or [SECURITY_JWT_GUIDE.md](SECURITY_JWT_GUIDE.md)
- **...make API calls?** → [API_DOCUMENTATION.md](API_DOCUMENTATION.md#rest-api-endpoints) with examples

#### Troubleshooting
- **...fix compilation errors?** → [FAQ](FAQ_TROUBLESHOOTING.md#🐛-troubleshooting) → Build Errors
- **...fix runtime errors?** → [FAQ](FAQ_TROUBLESHOOTING.md#🐛-troubleshooting) → Runtime Errors
- **...debug relationships?** → [FAQ](FAQ_TROUBLESHOOTING.md#9-how-do-i-debug-relationship-issues)
- **...fix database issues?** → [FAQ](FAQ_TROUBLESHOOTING.md#🐛-troubleshooting) → Database Errors
- **...debug authentication?** → [FAQ](FAQ_TROUBLESHOOTING.md) or [SECURITY_JWT_GUIDE.md](SECURITY_JWT_GUIDE.md#troubleshooting)
- **...fix a specific error?** → Search [FAQ_TROUBLESHOOTING.md](FAQ_TROUBLESHOOTING.md)

#### Deployment & Production
- **...deploy the application?** → [README.md](../README.md#-deployment)
- **...configure production settings?** → [SECURITY_JWT_GUIDE.md](SECURITY_JWT_GUIDE.md#applicationprodproperties-production)
- **...set up environment variables?** → [SECURITY_JWT_GUIDE.md](SECURITY_JWT_GUIDE.md#environment-variables-setup)

---

## 🔍 Quick Reference

### Entity Classes (9 Total)
```
User (base with single-table inheritance)
├── Student
├── Merchant
└── Admin

Financial Entities:
├── Wallet (Student 1:N)
├── Paiement (Commande 1:N, Wallet 1:N)
└── Versement (Wallet 1:N)

Commerce Entities:
├── Product (Merchant 1:N)
├── Commande (Student 1:N, Merchant 1:N)
└── BudgetVirtuel (Merchant 1:N)
```

### REST Endpoints Pattern
```
POST   /api/{entity}                    → Create (201)
GET    /api/{entity}                    → Get all (200)
GET    /api/{entity}/{trackingId}       → Get one (200)
PUT    /api/{entity}/{trackingId}       → Update (200)
DELETE /api/{entity}/{trackingId}       → Delete (204)

Special:
POST   /api/user/login                  → Get JWT token
POST   /api/user/register               → Create user
```

### Key Files in Project
```
src/main/java/com/backend/gns/
├── domain/models/                      → 9 @Entity classes
├── domain/services/                    → 18 Service classes
├── domain/enums/                       → 10 Enum classes
├── application/controllers/            → 9 REST controllers
├── application/mappers/                → 9 DTO mappers
├── infrastructure/repositories/        → 9 JPA repositories
└── Shared/security/                    → JWT & security config

src/main/resources/
├── application.properties               → Default config
├── application-dev.properties          → Development
├── application-prod.properties         → Production
└── (static files, templates)

src/test/
├── JpaRelationshipsIntegrationTest.java

docs/
├── API_DOCUMENTATION.md
├── DEVELOPER_GUIDE.md
├── SECURITY_JWT_GUIDE.md
├── FAQ_TROUBLESHOOTING.md
└── diagrams/miabeshop-er-diagram.drawio
```

---

## 📚 Learning Paths

### Path 1: First Time Using This Project (2 hours)
1. Read [README.md](../README.md) (20 min)
2. Follow Quick Start section (30 min)
3. Access Swagger UI at http://localhost:8080/api/swagger-ui.html (10 min)
4. Read [API_DOCUMENTATION.md](API_DOCUMENTATION.md) sections 1-3 (30 min)
5. Try making some API calls via Swagger (20 min)
6. Share questions in Discord/team chat

### Path 2: Contributing Code (4 hours)
1. Complete Path 1 (2 hours)
2. Read [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) (60 min)
3. Follow "Create a new entity" tutorial in [FAQ](FAQ_TROUBLESHOOTING.md#5-how-do-i-create-a-new-entity) (60 min)
4. Run tests to verify setup (20 min)
5. Ready to create your first feature!

### Path 3: Understanding Security (2 hours)
1. Read [SECURITY_JWT_GUIDE.md](SECURITY_JWT_GUIDE.md) sections 1-3 (45 min)
2. Test login flow via Swagger (15 min)
3. Read "Token Validation" section (20 min)
4. Test making authenticated requests (20 min)
5. Try the Postman setup in [FAQ](FAQ_TROUBLESHOOTING.md) (20 min)

### Path 4: Debugging & Troubleshooting (1 hour)
1. Keep [FAQ_TROUBLESHOOTING.md](FAQ_TROUBLESHOOTING.md) bookmarked
2. Read sections for your specific error
3. Try suggested solutions
4. Enable DEBUG logging per recommendations
5. Use Hibernate SQL query logging to debug relationships

---

## 🚀 Quick Commands Reference

```bash
# Development
mvn clean compile              # Rebuild
mvn spring-boot:run           # Start server
mvn test                       # Run tests

# Debugging
mvn clean compile -X          # Verbose output
mvn test -Dtest=TestClass     # Run specific test
mvn dependency:tree           # View dependencies

# Cleaning
mvn clean                      # Remove build artifacts
git clean -fd                  # Remove untracked files

# Git
git status                     # Check changes
git log --oneline -10          # View recent commits
git diff <file>               # See what changed
git add . && git commit -m "feat: description"  # Commit
```

---

## 💡 Pro Tips

1. **Always read the error message completely** - It usually tells you exactly what's wrong
2. **Use Swagger UI for testing** - http://localhost:8080/api/swagger-ui.html
3. **Enable DEBUG logging** - Add to application-dev.properties: `logging.level.com.backend.gns=DEBUG`
4. **Use `mvn clean` when stuck** - Removes old build artifacts that might be causing issues
5. **Check existing entities** - Copy patterns from Student.java or Wallet.java when creating new ones
6. **Read stack traces from bottom up** - Your code is usually at the bottom
7. **Use integration tests** - Run `mvn test` before pushing to validate everything works
8. **Commit frequently** - Small commits are easier to debug and review
9. **Check git status before pull** - Always commit or stash changes before pulling
10. **Use Postman for complex testing** - Better than Swagger for saved requests/environment variables

---

## 📞 Support & Contact

### Getting Help
1. **Search [FAQ_TROUBLESHOOTING.md](FAQ_TROUBLESHOOTING.md)** - 90% of issues are answered
2. **Check [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)** - Verify code patterns are correct
3. **Enable logging** - Add `logging.level.com.backend.gns=DEBUG` to see what's happening
4. **Run `mvn clean compile`** - Fresh rebuild often fixes mysterious errors
5. **Ask in team chat** - Include error message and steps to reproduce

### Reporting Issues
Include:
- Error message (full stack trace if possible)
- What you were trying to do
- Steps to reproduce
- Environment (`java -version`, `mvn -v`)
- Recent git commits (`git log --oneline -5`)

---

## 📋 Documentation Checklist

- [x] Main README with quick start
- [x] API Documentation with all endpoints
- [x] Developer Guide with code patterns
- [x] Security Guide with JWT details
- [x] FAQ & Troubleshooting guide
- [x] Architecture documentation (in README & API_DOCUMENTATION)
- [x] ER diagram for database schema
- [x] Build/Test/Deploy instructions
- [x] Security best practices
- [x] Entity relationship explanations

---

## 🎉 You're All Set!

Pick a documentation file above based on what you need to do, and start reading. Most issues are covered in the FAQ section.

**Questions?** Check [FAQ_TROUBLESHOOTING.md](FAQ_TROUBLESHOOTING.md) or ask on team chat! 

Happy coding! 🚀

---

**Last Updated**: April 2024  
**Documentation Version**: 1.0.0  
**Project Version**: 0.0.1
