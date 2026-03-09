# Task API

REST API for task management built with **Spring Boot** and **Java**, focused on good backend engineering practices such as input validation, exception handling, database migrations, automated testing, and transactional consistency.

The project was designed as a robust example of a backend service following common production patterns in modern Java applications.

---

# Overview

Task API provides endpoints to create, retrieve, update and delete tasks while ensuring:

* Data validation at the API boundary
* Consistent error handling
* Safe database operations
* Automated testing across layers
* Clean separation between DTOs and entities

The architecture follows a layered structure with:

```
Controller → Service → Repository → Database
```

DTO mapping is handled explicitly through a mapper layer to maintain separation between API contracts and persistence models.

---

# Technologies

Main technologies used in this project:

* Java
* Spring Boot
* Spring Data JPA
* Bean Validation
* Flyway (database migrations)
* Lombok
* H2 (for testing)
* JUnit + MockMvc (testing)

---

# Architecture Highlights

## DTO Mapping Layer

The API uses a **mapper component** responsible for converting:

* `TaskRequest` → `Task` (Entity)
* `Task` → `TaskResponse`

This approach ensures:

* Controllers do not expose internal entities
* Persistence models remain decoupled from API contracts
* DTO validation remains isolated from business logic

---

# Input Validation

The API validates request payloads using **Bean Validation**.

DTOs contain validation annotations and controllers use `@Valid` to enforce them automatically.

Example:

```java
public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request)
```

Invalid requests trigger a validation exception handled by the global exception handler.

---

# Global Exception Handling

A centralized exception handler (`GlobalExceptionHandler`) ensures consistent error responses across the API.

Handled scenarios include:

* Invalid request payloads
* Resource not found
* Duplicate task creation
* Unexpected errors

Example handling for validation errors:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex,
        HttpServletRequest request) {

    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((msg1, msg2) -> msg1 + "; " + msg2)
            .orElse("Validation failed");

    return ResponseEntity.status(400).body(new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            errorMessage,
            request.getRequestURI()
    ));
}
```

Error responses follow a structured format:

```
timestamp
status
error
message
path
```

---

# Preventing Race Conditions

When creating a new task, the API avoids race conditions by delegating uniqueness enforcement to the **database constraint**.

The service layer catches database integrity violations and translates them into domain-specific exceptions.

```java
@Transactional
public Task createTask(TaskRequest request) {
    Task task = taskMapper.toEntity(request);

    try {
        return taskRepository.save(task);
    } catch (DataIntegrityViolationException e) {
        throw new TaskAlreadyExistsException(
            "Task with the same title already exists: " + request.title()
        );
    }
}
```

This pattern ensures correctness in concurrent environments.

---

# Auditing (createdAt / updatedAt)

The entity includes automatic auditing fields:

```
createdAt
updatedAt
```

These timestamps are automatically managed by Spring Data JPA.

Entity configuration:

```java
@EntityListeners(AuditingEntityListener.class)
```

Fields:

```java
@CreatedDate
@Column(nullable = false, updatable = false)
private LocalDateTime createdAt;

@LastModifiedDate
@Column(nullable = false)
private LocalDateTime updatedAt;
```

Auditing is enabled in the application configuration:

```
@EnableJpaAuditing
```

---

# Database Migrations

Database schema changes are versioned using **Flyway**.

Migrations are executed automatically on application startup.

Benefits:

* Versioned database schema
* Reproducible environments
* Safe evolution of the database structure

---

# Testing Strategy

The project includes automated tests across multiple layers.

## Service Layer Tests

Business logic is tested in isolation to verify:

* Task creation
* Business rules
* Exception behavior

---

## Controller Integration Tests

Controller endpoints are tested using **MockMvc** with a full Spring context.

Configuration:

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
@Transactional
```

This setup provides:

* HTTP request simulation
* Real Spring context
* In-memory database
* Transaction rollback after tests

---

# Project Structure

```
src/main/java
 ├── controller
 ├── service
 ├── repository
 ├── domain
 ├── dto
 ├── mapper
 └── infra.exception

src/test/java
 ├── service
 ├── infra.exception
 └── controller
```

---

# Running the Project

## Clone the repository

```
git clone https://github.com/opmile/task-api.git
```

## Run the application

```
./mvnw spring-boot:run
```

or

```
mvn spring-boot:run
```

---

# Running Tests

```
mvn test
```

The test suite uses an **H2 in-memory database**, ensuring tests do not affect external environments.

---

# Future Improvements

Possible extensions for the project:

* Authentication and authorization
* Pagination and filtering for tasks (high priority)
* OpenAPI / Swagger documentation (high priority)
* Docker containerization
* CI/CD pipeline integration

---

# Author

Developed as a backend engineering practice project focused on building production-grade APIs using Spring Boot.
