# Support Ticket Management API

A backend REST API for managing support tickets, users, authentication, authorization, and ticket comments.

The project is built with Java and Spring Boot and demonstrates backend development concepts such as JWT authentication, role-based access control, ownership-based authorization, PostgreSQL persistence, Flyway database migrations, pagination, validation, exception handling, automated testing, Docker, and OpenAPI documentation.

## Tech Stack

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT authentication
- PostgreSQL
- Flyway
- Docker / Docker Compose
- Maven
- JUnit
- Mockito
- MockMvc
- H2 for repository integration tests
- OpenAPI / Swagger UI

## Architecture

The application follows a layered architecture:

```text
Client / Swagger / Postman
          |
          v
      Controller
          |
          v
       Service
          |
          v
      Repository
          |
          v
      PostgreSQL
```

Authentication requests follow this flow:

```text
Login
  |
  v
JWT generated
  |
  v
Authorization: Bearer <token>
  |
  v
JwtAuthenticationFilter
  |
  v
Spring SecurityContext
  |
  v
Protected API endpoint
```

## Main Features

### Authentication

Users can:

- Register an account
- Login using email and password
- Receive a JWT access token

Passwords are stored using BCrypt hashing.

### Ticket Management

Authenticated users can:

- Create tickets
- View tickets
- Update tickets
- Delete tickets
- Filter tickets by status and priority
- Retrieve tickets using pagination

Ticket statuses include:

- `OPEN`
- `IN_PROGRESS`
- `RESOLVED`
- `CLOSED`

Ticket priorities include:

- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`

### Authorization

The application supports two roles:

- `USER`
- `ADMIN`

A normal user can access only their own tickets.

An administrator can access tickets belonging to any user.

Authorization is checked in the service layer through a centralized ticket access service.

### Ticket Comments

Authenticated users can add and retrieve comments for tickets they are authorized to access.

Comments are linked to:

- a ticket
- an author
- a creation timestamp

### Validation and Error Handling

Request DTOs use Jakarta Bean Validation.

The API handles errors such as:

- invalid request data
- malformed JSON
- invalid login credentials
- duplicate email addresses
- missing tickets
- unauthorized requests
- forbidden ticket access

The API returns appropriate HTTP status codes such as:

- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`
- `409 Conflict`

## Database

PostgreSQL runs inside Docker.

Spring Boot connects to PostgreSQL using:

```properties
spring.datasource.url=jdbc:postgresql://127.0.0.1:55432/ticketing
```

Docker maps:

```text
Host port 55432
        |
        v
Container port 5432
```

## Database Migrations

Database schema management is handled using Flyway.

Migration files are located in:

```text
src/main/resources/db/migration/
```

Current migrations:

```text
V1__create_users_table.sql
V2__create_tickets_table.sql
V3__create_ticket_comments_table.sql
```

Flyway tracks applied migrations using:

```text
flyway_schema_history
```

Hibernate is configured to validate the schema rather than create it:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

## API Documentation

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

Swagger supports JWT Bearer authentication through the **Authorize** button.

## Running the Project

### 1. Start PostgreSQL

```bash
docker compose up -d
```

Check the container:

```bash
docker compose ps
```

### 2. Configure JWT Secret

Generate a Base64 secret:

```bash
openssl rand -base64 32
```

Set it as an environment variable:

```bash
export JWT_SECRET='your-generated-secret'
```

The application reads it using:

```properties
jwt.secret=${JWT_SECRET}
```

### 3. Start Spring Boot

```bash
./mvnw spring-boot:run
```

The API will run at:

```text
http://localhost:8080
```

## Example API Flow

### Register

```http
POST /api/auth/register
```

Example request:

```json
{
  "name": "Sagar",
  "email": "sagar@example.com",
  "password": "password123"
}
```

### Login

```http
POST /api/auth/login
```

Example request:

```json
{
  "email": "sagar@example.com",
  "password": "password123"
}
```

The response contains a JWT token.

Use the token for protected requests:

```http
Authorization: Bearer <JWT_TOKEN>
```

### Create Ticket

```http
POST /api/tickets
```

Example request:

```json
{
  "title": "VPN disconnecting frequently",
  "description": "VPN connection drops every few minutes.",
  "status": "OPEN",
  "priority": "HIGH"
}
```

### Get Tickets

```http
GET /api/tickets
```

Filtering and pagination are supported:

```text
GET /api/tickets?status=OPEN&priority=HIGH&page=0&size=10
```

### Add Comment

```http
POST /api/tickets/{ticketId}/comments
```

Example:

```json
{
  "content": "Investigating the issue."
}
```

## Testing

The project contains unit, controller/security, JWT, and repository integration tests.

Examples include:

- `TicketServiceTest`
- `UserServiceTest`
- `TicketAccessServiceTest`
- `AuthControllerTest`
- `TicketControllerSecurityTest`
- `JwtAuthenticationFilterTest`
- `JwtServiceTest`
- `UserRepositoryTest`
- `TicketRepositoryTest`
- `TicketCommentRepositoryTest`

Run all tests using:

```bash
./mvnw test
```

Testing covers:

- successful and unsuccessful authentication
- password hashing
- duplicate registration
- ticket ownership
- admin access
- create/update/delete behavior
- unauthorized and forbidden requests
- JWT authentication
- JPA repository behavior
- filtering
- pagination
- ticket comments

## Design Decisions

### JWT Authentication

The application uses stateless JWT authentication instead of server-side HTTP sessions.

Spring Security reconstructs the authenticated user for each protected request.

### Database Role as Source of Truth

Although the JWT contains role information, authorization uses the current role stored in the database.

This avoids relying on potentially outdated authorization information inside an existing token.

### DTOs Instead of Exposing Entities

Controllers use request and response DTOs instead of directly exposing JPA entities.

This helps separate the external API contract from the persistence model.

### Ownership-Based Authorization

Users are prevented from accessing tickets owned by other users.

The authorization logic is centralized rather than repeated across controllers.

### Flyway Instead of Hibernate Schema Updates

Flyway controls database schema evolution using versioned SQL migrations.

Hibernate uses schema validation to ensure the JPA entities remain compatible with the database.

## Future Improvements

Possible future enhancements include:

- refresh tokens
- ticket assignment to support agents
- additional administrator endpoints
- search by title or description
- audit history
- email notifications
- integration tests using Testcontainers
- CI/CD pipeline
- cloud deployment

## Project Purpose

This project was created to practice building a production-style Java backend API and to demonstrate practical experience with:

- REST API design
- Spring Boot
- Spring Security
- JWT
- relational database design
- JPA/Hibernate
- authorization
- testing
- Docker
- database migrations
- API documentation
