# Test Automation Guide

## Test Structure

```
src/test/java/com/example/spvms/
├── controller/          # Controller unit tests (MockMvc)
├── service/             # Service unit tests (Mockito)
├── repository/          # Repository tests (Testcontainers)
└── integration/         # Full integration tests
```

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn test -Dtest=VendorServiceImplTest
mvn test -Dtest=VendorControllerTest
mvn test -Dtest=VendorRepositoryTest
```

### Run Tests by Category
```bash
# Unit tests only (fast)
mvn test -Dtest=*Test

# Integration tests only
mvn test -Dtest=*IntegrationTest
```

### Generate Coverage Report
```bash
mvn clean test jacoco:report
```

View report: `target/site/jacoco/index.html`

## Coverage Targets

- **Minimum**: 60% overall
- **Recommended**: 
  - Service Layer: 80%+
  - Controller Layer: 70%+
  - Repository Layer: 60%+

## Test Profiles

Tests use H2 in-memory database by default (fast).
Repository tests use Testcontainers with MySQL (real database).

## Important Notes

✅ Tests are isolated - no production data affected
✅ Each test rolls back automatically (@Transactional)
✅ Testcontainers starts/stops MySQL automatically
✅ Frontend APIs remain unchanged

## Troubleshooting

**Docker not running**: Repository tests need Docker for Testcontainers
**Port conflicts**: Tests use random ports, no conflicts with running app
**Slow tests**: First run downloads MySQL image (~200MB)
