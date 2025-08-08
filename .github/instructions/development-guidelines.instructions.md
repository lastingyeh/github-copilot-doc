---
applyTo: '**'
---
# Development Guidelines

## 0. Language

- - **Traditional Chinese** must be used for all communications and documents

## 1. API Structure

- Use RESTFul API design principles

## 2. Code Standards

- Style Guide: Follow Google Java Style Guide, and implement the `clean architecture` principles better.
- Indentation: 4 spaces (no tabs)
- Naming:
    - Classes: PascalCase
    - Methods/Variables: camelCase
    - Constants: UPPER_SNAKE_CASE
    - DTOs: Suffix with Request/Response/Dto
    - Repositories: XxxRepository
    - Enums: UPPER_SNAKE_CASE

## 3. Directory Structure

```text
.
├── docker      # Docker-related files (e.g., Dockerfile, docker-compose.yml)
├── docs        # Project documentation files
└── src/main/java/com/example/project
    ├── config      # Configuration files (e.g., Beans, Security)
    ├── controller  # Controllers for handling HTTP requests (API endpoints)
    ├── exception   # Global exception handlers and custom exception classes
    ├── model
    │   ├── dto     # Data Transfer Objects for request/response bodies
    │   └── entity  # Database entities (e.g., JPA)
    ├── repository  # Repository interfaces for data access
    ├── service     # Business logic layer
    └── util        # Utility classes
```

## 4. Development Practices

- Controller Layer: Input validation and service delegation
- Service Layer: Contains business logic
- Repository Layer: Data access via Spring Data JPA
- Constants: Use instead of magic numbers
- Optional: Prefer over null returns
- Logging: Use @Slf4j (Lombok)
- Documentation: JavaDoc for public APIs
- Dependency Injection: Constructor-based (no @Autowired)
- Object Creation: Use @Builder for complex objects

## 5. Testing

- Coverage: Unit tests for all service methods
- Framework: JUnit 5 with Mockito
- Test Class Modifier: Make test class package-private scope
- Test Documentation: Use @DisplayName for clarity
- Test Cases:
    - Positive: Expected successful behavior
    - Negative: Error and exception scenarios

## 6. Technologies

- Java 17
- Maven: Use the Maven Wrapper (`mvnw`)
- Spring Boot 3+
- Docker: Prefer the new `docker compose` command syntax
- Lombok Annotations:
    - @Getter, @Setter, @Builder, @Slf4j
    - Avoid field injection
- SpringDoc OpenAPI for documentation
- Database (Use Postgres if no special preference):
  - Postgres
  - MongoDB
- Cache (Optional, can be used for data caching or sessions):
  - Redis
- Message Queue (Optional, use only when needed):
  - RabbitMQ
  - Kafka
  - Pubsub for GCP

## 7. Best Practices

- Comments: Only for non-obvious logic
- Documentation: Focus on "why" not "what"
- Error Messages: Clear and descriptive
- Exception Handling: Implement globally
- Immutability: Prefer when possible
- Static Methods: Limited to utilities
- Performance and Best Practices: All development tasks should consider performance and follow industry best practices.
- Leverage Off-the-Shelf Tools: Prioritize stable and reliable packages or libraries to avoid reinventing the wheel.

## 8. Validation

- Use Jakarta Validation annotations
- Controller Class: @Validated
- Request Bodies: @Valid
- Error Messages: Clear and descriptive

## 9. API Documentation

- Use SpringDoc OpenAPI
- Controllers: @Tag annotation
- Endpoints: @Operation annotation
- DTO Fields: @Schema with descriptions

## 10. Git Commit Convention

- Commit Message Structure
  ```text
  <type>: <subject>

  <body>
  ```
    - Example
      ```text
      fix: Fix incorrect time format in employee management

      1. Updated time conversion logic in TimeUtils to subtract time zone offset from timestamp.
      ```
- Type Definitions
    - feat: Add or modify a feature.
    - fix: Fix a bug.
    - docs: Update or add documentation.
    - style: Code style changes that do not affect behavior (e.g., formatting, whitespace, missing semicolons).
    - refactor: Code refactoring (business logic remains unchanged).
    - perf: Code change that improves performance.
    - test: Add or update tests.
    - chore: Changes to build process, tools, or maintenance tasks.
    - revert: Revert a previous commit (e.g., revert: feat(auth): support OAuth2 login).
- Subject
    - A short summary of the change (imperative mood, no period at the end).
- Body
    - Optional. A bullet-point list explaining the change in detail:
        - What was changed and why
        - Impact or context (if needed)
