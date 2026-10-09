# Country Integration API

A Spring Boot REST API that integrates with an external SOAP-based
Country Information service and persists country information in MySQL.

## 1. Overview

The application exposes REST endpoints for managing country information.
It retrieves country data from the SOAP service, maps the response into
the application's domain model, and persists the data in a relational
database.

### Key Features

- RESTful CRUD endpoints for country information.
- Integration with an external SOAP service using a WSDL.
- MySQL database persistence using Spring Data JPA.
- Request validation and centralized exception handling.
- Configurable SOAP connection and request timeouts.
- Application health endpoints using Spring Boot Actuator.
- Automated unit, controller, and integration tests.
- Docker containerization.
- Kubernetes deployment manifests and automation scripts.

## 2. Technology Stack

- Java 25
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- MySQL
- SOAP / WSDL
- Spring Boot Actuator
- JUnit and Mockito
- H2 for automated tests
- Docker
- Kubernetes and Minikube

## 3. Architecture

The application follows a layered architecture:

1. **Controller layer** — exposes REST endpoints and handles HTTP requests.
2. **Service layer** — coordinates SOAP integration and business logic.
3. **SOAP client layer** — communicates with the external SOAP service.
4. **Repository layer** — handles database operations using Spring Data JPA.
5. **Entity and DTO layer** — represents persisted data and API requests
   and responses.
6. **Exception handling layer** — translates application failures into
   appropriate HTTP responses.

Request flow:

REST Client → REST Controller → Service → SOAP Client / Repository
→ MySQL Database

## 4. Prerequisites

Install the following:

- Java 25
- Docker
- MySQL
- Git

For Kubernetes deployment, also install:

- kubectl
- Minikube

## 5. Configuration

Configure the following environment variables before starting the
application:

| Variable | Description |
|---|---|
| `DB_URL` | JDBC URL for the MySQL database |
| `DB_USERNAME` | MySQL application username |
| `DB_PASSWORD` | MySQL application password |
| `SOAP_COUNTRY_INFO_URL` | External SOAP service endpoint |
| `SOAP_CONNECT_TIMEOUT` | SOAP connection timeout in milliseconds |
| `SOAP_REQUEST_TIMEOUT` | SOAP request timeout in milliseconds |

The application defaults to port `8085`.

Do not commit database passwords or other credentials to source control.

## 6. Running Locally

Ensure the MySQL database and application user exist and have the
necessary permissions.

Export the database credentials:

```bash
export DB_URL='jdbc:mysql://localhost:3306/country_integration?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true'
export DB_USERNAME='country_app'
read -rsp "Database password: " DB_PASSWORD
echo
export DB_PASSWORD