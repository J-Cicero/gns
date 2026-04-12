# ✅ Production Readiness Checklist

Use this checklist to verify the project is ready for deployment to production.

---

## 📋 Code Quality

- [ ] **All compilation warnings fixed**
  ```bash
  mvn clean compile 2>&1 | grep -i warning
  # Should have no warnings related to your code
  ```

- [ ] **All unit/integration tests passing**
  ```bash
  mvn clean test
  # BUILD SUCCESS with all tests passing
  ```

- [ ] **Code follows project conventions**
  - [ ] Entity classes extend `BaseEntity`
  - [ ] Services have interfaces + implementations
  - [ ] Controllers return proper HTTP status codes
  - [ ] Mappers use repository injection for relationships
  - [ ] DTOs use Record syntax
  - [ ] No field injection (use constructor injection)

- [ ] **No debug/test code in production files**
  - [ ] No `System.out.println()` or `e.printStackTrace()`
  - [ ] No `@Disabled` tests committed
  - [ ] No TODO comments suggesting incomplete work
  - [ ] No hardcoded credentials or secrets

- [ ] **Error handling is appropriate**
  - [ ] All endpoints handle exceptions
  - [ ] Custom exceptions thrown (ResourceNotFoundException, etc.)
  - [ ] No stack traces exposed to clients
  - [ ] All error responses have proper status codes

---

## 🔐 Security

- [ ] **JWT configuration for production**
  ```bash
  # application-prod.properties must have:
  jwt.secret=${JWT_SECRET}  # NOT hardcoded
  jwt.expiration.ms=${JWT_EXPIRATION_MS}  # Default 24 hours
  ```

- [ ] **Database credentials are environment variables**
  ```bash
  # application-prod.properties must have:
  spring.datasource.url=${SPRING_DATASOURCE_URL}
  spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
  spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
  ```

- [ ] **No sensitive data in logs**
  - [ ] Password fields not logged
  - [ ] JWT tokens not logged
  - [ ] API keys not logged
  - [ ] Logging level set to WARN or ERROR in prod

- [ ] **CORS configured for production domain**
  ```java
  // SecurityConfig.java should have:
  configuration.setAllowedOriginPatterns(Arrays.asList(
      "https://miabeshop.cm",
      "https://www.miabeshop.cm"
  ));
  ```

- [ ] **HTTPS enforced**
  - [ ] All endpoints use HTTPS in production
  - [ ] SSL certificate installed on server
  - [ ] HTTP redirects to HTTPS

- [ ] **Spring Security is configured**
  - [ ] All endpoints have proper authorization
  - [ ] Role-based access control working
  - [ ] CSRF protection enabled
  - [ ] No test/demo users in production database

- [ ] **Swagger/OpenAPI disabled in production**
  ```properties
  # application-prod.properties
  springdoc.swagger-ui.enabled=false
  ```

---

## 🗄️ Database

- [ ] **Database schema is migration-ready**
  ```bash
  # Verify with PostgreSQL
  psql -U postgres -d miabeshop -c "\dt"
  # Should show all 9 entities as tables
  ```

- [ ] **All foreign key relationships are valid**
  ```sql
  -- Verify no orphaned references
  SELECT * FROM wallet WHERE student_id NOT IN (SELECT id FROM "user");
  -- Should return no results
  ```

- [ ] **Database indexes are created**
  ```sql
  -- Verify indexes on foreign keys
  SELECT * FROM pg_indexes WHERE tablename = 'wallet';
  -- Should show index on student_id
  ```

- [ ] **Unique constraints are in place**
  ```sql
  -- Verify unique constraints
  SELECT * FROM pg_constraint WHERE tablename = 'user' AND contype = 'u';
  -- Should show email, tracking_id as unique
  ```

- [ ] **Backups are configured**
  - [ ] Daily automated backups scheduled
  - [ ] Backup restoration tested
  - [ ] Backup stored in secure offsite location

- [ ] **Database DDL is set to 'validate'**
  ```properties
  # application-prod.properties
  spring.jpa.hibernate.ddl-auto=validate
  # NOT create-drop or update
  ```

---

## 📊 Performance

- [ ] **Query performance is acceptable**
  ```bash
  # Enable query logging and check for N+1 queries
  logging.level.org.hibernate.SQL=DEBUG
  # Each GET should only execute 1-2 queries
  ```

- [ ] **Connection pooling is configured**
  ```properties
  spring.datasource.hikari.maximum-pool-size=20
  spring.datasource.hikari.minimum-idle=5
  ```

- [ ] **Memory usage is reasonable**
  - [ ] Heap size set appropriately (-Xmx512m)
  - [ ] Monitor for memory leaks in development
  - [ ] No unbounded collections in service methods

- [ ] **API response times are acceptable**
  - [ ] GET requests: < 100ms
  - [ ] POST/PUT requests: < 500ms
  - [ ] Bulk operations: < 1 second

- [ ] **Pagination is implemented for list endpoints**
  ```bash
  GET /api/student?page=0&size=20
  # Should not return all records
  ```

---

## ✅ Testing

- [ ] **Integration tests pass**
  ```bash
  mvn test -Dtest=JpaRelationshipsIntegrationTest
  # All tests should pass
  ```

- [ ] **Test coverage is adequate**
  ```bash
  mvn clean test jacoco:report
  # Check: target/site/jacoco/index.html
  # Target: > 70% line coverage
  ```

- [ ] **Load testing is done** (if critical system)
  - [ ] Tested with 100 concurrent users
  - [ ] Response times within SLA
  - [ ] No database connection pool exhaustion

---

## 📖 Documentation

- [ ] **README.md is comprehensive**
  - [ ] Quick start instructions
  - [ ] Architecture overview
  - [ ] Technology stack versions
  - [ ] Deployment instructions

- [ ] **API documentation is complete**
  - [ ] All endpoints documented
  - [ ] Request/response examples
  - [ ] Error codes and meanings
  - [ ] Authentication requirements

- [ ] **Developer guide exists**
  - [ ] Code conventions documented
  - [ ] Common patterns explained
  - [ ] How to add new entities

- [ ] **Troubleshooting guide exists**
  - [ ] Common errors covered
  - [ ] Debug steps provided
  - [ ] Support contact information

---

## 🚀 Deployment

- [ ] **Docker image is created** (if using Docker)
  ```bash
  docker build -t miabeshop/gns:1.0.0 .
  docker run -p 8080:8080 miabeshop/gns:1.0.0
  ```

- [ ] **Environment variables are set**
  ```bash
  export SPRING_PROFILES_ACTIVE=prod
  export JWT_SECRET=<production-secret>
  export SPRING_DATASOURCE_PASSWORD=<db-password>
  # ... all other required variables
  ```

- [ ] **Application starts successfully**
  ```bash
  java -Dspring.profiles.active=prod -jar gns-0.0.1-SNAPSHOT.jar
  # Should start within 30 seconds
  # No errors in logs
  ```

- [ ] **Health check endpoint works**
  ```bash
  curl http://localhost:8080/actuator/health
  # Should return 200 with status "UP"
  ```

- [ ] **Rolling deployment tested**
  - [ ] Old version still handles requests during update
  - [ ] Database migrations are backward compatible
  - [ ] Zero-downtime deployment possible

---

## 📈 Monitoring

- [ ] **Logging is configured**
  ```properties
  # application-prod.properties
  logging.level.root=WARN
  logging.level.com.backend.gns=INFO
  logging.file.name=logs/gns.log
  logging.file.max-size=10MB
  logging.file.max-history=7
  ```

- [ ] **Metrics are collected**
  - [ ] Request count/duration
  - [ ] Error rates
  - [ ] Database connection pool stats
  - [ ] JVM heap usage

- [ ] **Alerts are configured**
  - [ ] Alert on high error rate (> 5%)
  - [ ] Alert on slow response times (> 500ms avg)
  - [ ] Alert on high memory usage (> 80%)
  - [ ] Alert on database connection pool exhaustion

- [ ] **Application monitoring is active**
  - [ ] Datadog/NewRelic/CloudWatch configured
  - [ ] APM traces collecting
  - [ ] Error tracking enabled (Sentry/etc)

---

## 🔄 Release Management

- [ ] **Version is bumped**
  ```bash
  # pom.xml should have version: <version>0.1.0</version>
  # Not SNAPSHOT for production
  mvn versions:set -DnewVersion=0.1.0
  ```

- [ ] **Git tag is created**
  ```bash
  git tag v0.1.0
  git push origin v0.1.0
  ```

- [ ] **Release notes are written**
  - [ ] New features listed
  - [ ] Bug fixes documented
  - [ ] Breaking changes noted (if any)
  - [ ] Upgrade instructions provided

- [ ] **Changelog is updated**
  - [ ] CHANGELOG.md updated
  - [ ] Release date included
  - [ ] Version number clear

---

## 🎯 Pre-Launch Checklist (24 hours before release)

- [ ] **Final code review completed**
- [ ] **All PRs merged and tested**
- [ ] **Final build successful**
  ```bash
  mvn clean package -DskipTests
  ```
- [ ] **Staging environment mirrors production**
- [ ] **Full end-to-end test on staging**
  1. User registration
  2. User login
  3. Create entities (Student, Merchant, etc.)
  4. Test relationships work
  5. Retrieve and update data
  6. Delete operations
- [ ] **Database backup created before deployment**
- [ ] **Rollback plan documented**
- [ ] **Support team briefed on changes**
- [ ] **Communication plan ready** (for users if applicable)

---

## 🎉 Launch Checklist

- [ ] **Production environment ready**
  - [ ] Server configured
  - [ ] Database online
  - [ ] SSL certificate installed
  - [ ] Domain DNS updated

- [ ] **Application deployed**
  ```bash
  java -Dspring.profiles.active=prod -jar gns-0.0.1-SNAPSHOT.jar
  ```

- [ ] **Health checks passing**
  ```bash
  curl https://api.miabeshop.cm/api/actuator/health
  # Returns: {"status":"UP"}
  ```

- [ ] **API endpoints responding**
  ```bash
  curl -X GET https://api.miabeshop.cm/api/swagger-ui.html
  # Should return 404 (Swagger disabled in prod) or 200 if left enabled
  ```

- [ ] **Authentication working**
  ```bash
  curl -X POST https://api.miabeshop.cm/api/user/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@example.com","password":"password"}'
  # Should return JWT token
  ```

- [ ] **Database is accessible**
  ```bash
  # From application logs, verify queries are executing
  tail -f logs/gns.log | grep "SELECT"
  ```

---

## 📊 Post-Launch Monitoring (First 24 hours)

- [ ] **Error rate is normal** (< 1%)
- [ ] **Response times are acceptable** (< 200ms average)
- [ ] **Database connection pool is stable**
- [ ] **Memory usage is stable** (no memory leaks)
- [ ] **No critical errors in logs**
- [ ] **User feedback is positive**
- [ ] **Backup procedure is working**

---

## ❌ Rollback Plan

If issues are found after launch:

1. **Immediate actions:**
   - Keep previous version binary and database backup
   - Document error symptoms
   - Stop incoming traffic (if severe)

2. **Rollback steps:**
   ```bash
   # 1. Stop current version
   kill <process-id>
   
   # 2. Restore database backup (if needed)
   # Custom restore script in docs/backups/
   
   # 3. Start previous version
   java -Dspring.profiles.active=prod -jar gns-0.0.0-SNAPSHOT.jar
   
   # 4. Verify application is working
   curl https://api.miabeshop.cm/api/actuator/health
   ```

3. **Post-rollback:**
   - Investigate root cause
   - Create hotfix branch
   - Fix issue
   - Re-test on staging
   - Prepare re-release

---

## 📞 Support During Launch

**Key Contact Information:**
- **DevOps Lead**: [Name] - [Slack] - [Phone]
- **Database Admin**: [Name] - [Slack] - [Phone]
- **Application Owner**: [Name] - [Slack] - [Phone]

**Escalation Path:**
1. Report issue to DevOps
2. Escalate to Tech Lead if critical
3. CEO notification if system down > 1 hour

---

## ✨ Final Verification

Run this final script before launching:

```bash
#!/bin/bash
set -e

echo "🔍 Running pre-launch verification..."

echo "✓ Checking compilation..."
mvn clean compile -q || exit 1

echo "✓ Running tests..."
mvn test -q || exit 1

echo "✓ Building final JAR..."
mvn package -DskipTests -q || exit 1

echo "✓ Checking for hardcoded secrets..."
grep -r "secret\|password" src/main --include="*.java" | grep -v "SECRET_KEY" && exit 1 || true

echo "✓ Checking Java version..."
java -version

echo "✓ Checking Maven version..."
mvn -version

echo ""
echo "🎉 All pre-launch checks passed!"
echo "Ready for deployment to production"
```

Save as `scripts/pre-launch-check.sh` and run:
```bash
chmod +x scripts/pre-launch-check.sh
./scripts/pre-launch-check.sh
```

---

## 📋 Sign-Off

- [ ] Tech Lead: _________________ Date: _______
- [ ] DevOps: _________________ Date: _______
- [ ] Product Owner: _________________ Date: _______

**Ready for Production**: ☐ YES ☐ NO

**Notes**: 
```
[Add any important notes or blockers here]
```

---

**Last Updated**: April 2024  
**Version**: 1.0  
**Next Review**: Before each production release
