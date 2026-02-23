# WireMock Template Projects

This repository contains practical examples of WireMock integration with different testing approaches in Java/Spring Boot applications. It demonstrates three main use cases for mocking external APIs during testing.

## 📋 Use Cases

### 1. WireMock + JUnit 5 + Spring Boot
Unit and integration tests using WireMock with JUnit 5 extension to mock external API dependencies.

### 2. WireMock + RestAssured
Integration tests combining WireMock for API mocking and RestAssured for HTTP request testing.

### 3. WireMock + Docker
Standalone WireMock server running in Docker for development and testing purposes.

## 🚀 Getting Started

### Prerequisites
- Java 25
- Maven 3.x
- Docker and Docker Compose (for Docker use case)

## 📁 Project Structure

```
wiremock-template-projects/
├── junit-wiremock/          # Spring Boot application with WireMock tests
└── wiremock docker/         # Docker-based WireMock standalone server
```

## 🔧 Use Case 1 & 2: JUnit 5 + Spring Boot + RestAssured

### Application Overview

The `junit-wiremock` project is a Spring Boot REST API for user management that validates email addresses through an external API.

### API Endpoints

#### Get All Users
```http
GET /api/users
```
Returns a list of all users.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  }
]
```

#### Get User by ID
```http
GET /api/users/{id}
```
Returns a specific user by ID.

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
```

#### Create User
```http
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com"
}
```
Creates a new user after validating the email through an external API.

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
```

#### Update User
```http
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "Jane Doe",
  "email": "jane@example.com"
}
```
Updates an existing user.

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Jane Doe",
  "email": "jane@example.com"
}
```

#### Delete User
```http
DELETE /api/users/{id}
```
Deletes a user by ID.

**Response:** `204 No Content`

### Running the Application

```bash
cd junit-wiremock
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Running Tests

#### Run all tests
```bash
cd junit-wiremock
./mvnw test
```

#### Run specific test classes

**Unit tests with WireMock:**
```bash
./mvnw test -Dtest=UserServiceWithWireMockTest
```

**Integration tests with RestAssured:**
```bash
./mvnw test -Dtest=UserControllerIntegrationTest
```

### Test Examples

#### WireMock + JUnit 5
The `UserServiceWithWireMockTest` class demonstrates:
- Mocking external email validation API
- Testing successful email validation
- Testing invalid email scenarios
- Testing API failures (404, timeouts)

#### WireMock + RestAssured
The `UserControllerIntegrationTest` class demonstrates:
- Full integration testing with Spring Boot
- Using RestAssured for HTTP requests
- WireMock for external API mocking
- End-to-end API testing

## 🐳 Use Case 3: WireMock + Docker

### Overview

Standalone WireMock server running in Docker with pre-configured stubs for the User API.

### Starting WireMock Server

```bash
cd "wiremock docker"
docker-compose up -d
```

The WireMock server will be available at `http://localhost:8081`

### Stopping WireMock Server

```bash
docker-compose down
```

### Viewing Logs

```bash
docker-compose logs -f wiremock
```

### Available Mock Endpoints

All endpoints are accessible at `http://localhost:8081`

#### Get All Users
```bash
curl http://localhost:8081/api/users
```

#### Get User by ID (ID = 1)
```bash
curl http://localhost:8081/api/users/1
```

#### Get User Not Found (ID = 999)
```bash
curl http://localhost:8081/api/users/999
```

#### Create User
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"New User","email":"newuser@example.com"}'
```

#### Update User
```bash
curl -X PUT http://localhost:8081/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated User","email":"updated@example.com"}'
```

#### Delete User
```bash
curl -X DELETE http://localhost:8081/api/users/1
```

### WireMock Configuration

The Docker setup includes:
- **Mappings**: JSON files defining request/response stubs (`wiremock/mappings/`)
- **Files**: Static response files (`wiremock/__files/`)
- **Response Templating**: Enabled for dynamic responses
- **Verbose Logging**: Enabled for debugging

### Customizing Stubs

You can add or modify stubs by editing files in:
- `wiremock/mappings/` - Request/response mappings
- `wiremock/__files/` - Response body files

Changes are automatically picked up by WireMock (no restart required).

## 📚 Technologies Used

- **Spring Boot 4.0.2** - Application framework
- **WireMock 3.13.0** - HTTP mocking library
- **RestAssured 5.5.0** - REST API testing
- **JUnit 5** - Testing framework
- **H2 Database** - In-memory database
- **Lombok** - Boilerplate code reduction
- **Docker** - Containerization

## 📖 Additional Resources

- [WireMock Documentation](https://wiremock.org/docs/)
- [RestAssured Documentation](https://rest-assured.io/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

## 📝 Notes

- The Spring Boot application uses an H2 in-memory database
- Email validation is mocked in tests using WireMock
- The Docker WireMock instance runs on port 8081 to avoid conflicts
- Response templating is enabled in Docker setup for dynamic responses
