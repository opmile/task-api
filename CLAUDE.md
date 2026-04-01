# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Run the application (requires database connection via environment variables)
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=TaskControllerTest

# Run a specific test method
./mvnw test -Dtest=TaskControllerTest#shouldCreateTask

# Compile only
./mvnw compile
```

## Database Configuration

Production uses PostgreSQL with environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Tests use H2 in-memory database with `application-test.properties` (Flyway disabled, Hibernate manages schema).

## Architecture

**Layered architecture:** `Controller → Service → Repository → Database`

**Package structure:**
```
org.opmile.securitytodo
├── controller/        # REST endpoints (TaskController)
├── service/           # Business logic (TaskService)
├── repository/        # Spring Data JPA interfaces (TaskRepository)
├── domain/            # Entities and enums (Task, Status)
├── dto/               # Request/Response DTOs (TaskRequest, TaskResponse, TaskUpdateRequest)
├── mapper/            # DTO↔Entity conversion (TaskMapper)
└── infra.exception/   # Exception classes and GlobalExceptionHandler
```

**Key patterns:**
- DTO mapping via `TaskMapper` component - controllers never expose entities directly
- Global exception handling via `@RestControllerAdvice` returning `ErrorResponse` records
- JPA Auditing enabled (`@EnableJpaAuditing`) for automatic `createdAt`/`updatedAt` timestamps
- Race condition prevention: uniqueness enforced by database constraint, service catches `DataIntegrityViolationException`

**Entity auditing:** `Task` uses `@CreatedDate` and `@LastModifiedDate` with `AuditingEntityListener`.

## API Endpoints

```
GET    /api/tasks      - List all tasks
GET    /api/tasks/{id} - Get task by ID
POST   /api/tasks      - Create task (TaskRequest)
PUT    /api/tasks/{id} - Update task (TaskUpdateRequest)
DELETE /api/tasks/{id} - Delete task
```

## Database Migrations

Flyway migrations in `src/main/resources/db/migration/`. Named as `V{n}__description.sql`.

## Task Status Values

`PENDING`, `APPROVED`, `REJECTED`, `COMPLETED` (stored as lowercase strings in API responses).

## Date Formats

- `dueDate`: `dd/MM/yyyy`
- `createdAt`/`updatedAt`: `dd/MM/yyyy HH:mm:ss`