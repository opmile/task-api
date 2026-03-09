# Task API

API REST para gerenciamento de tarefas desenvolvida com **Spring Boot** e **Java**, com foco na aplicação de boas práticas de engenharia de software no desenvolvimento de backends.

O projeto foi construído buscando refletir padrões comuns em aplicações reais, incluindo validação de dados, tratamento consistente de exceções, versionamento de banco de dados, testes automatizados e separação clara entre camadas da aplicação.

---

# Visão Geral

A Task API fornece endpoints para criação, consulta, atualização e remoção de tarefas, garantindo:

* Validação de dados nas requisições
* Tratamento padronizado de erros
* Operações seguras no banco de dados
* Testes automatizados
* Separação entre modelos de persistência e contratos da API

A arquitetura segue uma organização em camadas:

```
Controller → Service → Repository → Database
```

O mapeamento entre DTOs e entidades é feito por uma camada dedicada de **mapper**, evitando o acoplamento entre a estrutura interna da aplicação e os objetos expostos pela API.

---

# Tecnologias Utilizadas

Principais tecnologias e ferramentas utilizadas no projeto:

* Java
* Spring Boot
* Spring Data JPA
* Bean Validation
* Flyway (database migrations)
* Lombok
* H2 Database (para testes)
* JUnit
* MockMvc

---

# Destaques de Arquitetura

## Camada de Mapeamento (DTO ↔ Entidade)

A API utiliza um **mapper** responsável por converter objetos entre:

* `TaskRequest` → `Task` (entidade)
* `Task` → `TaskResponse`

Esse padrão permite:

* evitar exposição direta de entidades JPA
* manter contratos da API desacoplados da persistência
* centralizar a lógica de conversão de objetos

---

# Validação de Dados de Entrada

As requisições recebidas pela API são validadas utilizando **Bean Validation**.

As classes DTO contêm anotações de validação e os controllers utilizam `@Valid` para acionar automaticamente a verificação dos dados recebidos.

Exemplo:

```java
public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request)
```

Caso os dados enviados sejam inválidos, uma exceção de validação é lançada e tratada globalmente.

---

# Tratamento Global de Exceções

A aplicação utiliza um **GlobalExceptionHandler** para centralizar o tratamento de erros e garantir respostas padronizadas da API.

Entre os cenários tratados estão:

* dados de entrada inválidos
* recurso não encontrado
* tentativa de criação de tarefa duplicada
* erros inesperados

Exemplo de tratamento de erro de validação:

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

O formato padrão da resposta de erro inclui:

```
timestamp
status
error
message
path
```

---

# Prevenção de Race Condition na Criação de Tarefas

Durante a criação de novas tarefas, a API delega ao banco de dados a responsabilidade de garantir a **unicidade de títulos**, evitando problemas de concorrência (race condition).

Caso ocorra uma violação de integridade no banco de dados, a exceção é capturada e convertida em uma exceção de domínio da aplicação.

Exemplo:

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

Essa abordagem garante consistência mesmo em cenários com múltiplas requisições concorrentes.

---

# Auditoria Automática (createdAt / updatedAt)

A entidade de tarefas possui campos de auditoria para registrar automaticamente:

* momento de criação (`createdAt`)
* última atualização (`updatedAt`)

Configuração na entidade:

```java
@EntityListeners(AuditingEntityListener.class)
```

Campos:

```java
@CreatedDate
@Column(nullable = false, updatable = false)
private LocalDateTime createdAt;

@LastModifiedDate
@Column(nullable = false)
private LocalDateTime updatedAt;
```

Para habilitar o mecanismo de auditoria, a aplicação utiliza:

```
@EnableJpaAuditing
```

---

# Versionamento do Banco de Dados

O projeto utiliza **Flyway** para gerenciar migrations de banco de dados.

As migrations são executadas automaticamente ao iniciar a aplicação, permitindo:

* controle de versão do schema
* evolução segura do banco de dados
* reprodutibilidade entre ambientes

---

# Estratégia de Testes

A aplicação possui testes automatizados em diferentes camadas.

## Testes da Camada de Serviço

A lógica de negócio é testada de forma isolada para verificar:

* criação de tarefas
* regras de negócio
* comportamento de exceções

---

## Testes de Integração da Camada de Controller

Os endpoints da API são testados utilizando **MockMvc** com o contexto completo do Spring.

Configuração utilizada:

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
@Transactional
```

Essa configuração permite:

* simular requisições HTTP
* executar testes com o contexto real do Spring
* utilizar banco de dados em memória
* garantir rollback automático ao final de cada teste

---

# Estrutura do Projeto

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

# Executando o Projeto

## Clonar o repositório

```
git clone https://github.com/opmile/task-api.git
```

## Executar a aplicação

```
./mvnw spring-boot:run
```

ou

```
mvn spring-boot:run
```

---

# Executando os Testes

```
mvn test
```

Os testes utilizam **H2 em memória**, garantindo que não haja impacto em bancos de dados externos.

---

# Possíveis Evoluções

Algumas melhorias possíveis para o projeto incluem:

* autenticação e autorização
* paginação e filtros para tarefas (prioridade alta)
* documentação automática da API com OpenAPI/Swagger (prioridade alta)
* containerização com Docker
* integração contínua (CI/CD)

---

# Autor

Projeto desenvolvido como prática de engenharia de backend utilizando Spring Boot.
