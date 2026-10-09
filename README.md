# Country Integration API

A Spring Boot REST API that integrates with an external SOAP-based Country Information service, retrieves country details, and persists the results in MySQL. The application exposes RESTful CRUD endpoints and includes automated tests, Docker support, and Kubernetes deployment resources.

## 1. Features

- RESTful API for creating, retrieving, updating, and deleting country records.
- SOAP integration using a WSDL-based Country Information service.
- Country name normalization before SOAP requests.
- ISO country code lookup using the `CountryISOCode` operation.
- Country details retrieval using the `FullCountryInfo` operation.
- MySQL persistence using Spring Data JPA.
- Country and language data models.
- Request validation and centralized exception handling.
- Configurable SOAP connection and request timeouts.
- Health checks and metrics through Spring Boot Actuator.
- Unit, controller, and integration tests.
- Docker containerization.
- Kubernetes deployment manifests and automation scripts.

## 2. Technology Stack

| Technology | Purpose |
|---|---|
| Java 25 | Application development |
| Spring Boot | Application framework |
| Spring Web | REST API endpoints |
| Spring Data JPA | Database persistence |
| MySQL | Production database |
| H2 | Automated testing database |
| SOAP / WSDL | External country information integration |
| JUnit and Mockito | Automated testing |
| Spring Boot Actuator | Health checks and metrics |
| Maven | Dependency management and builds |
| Docker | Application containerization |
| Kubernetes | Container orchestration |
| Minikube | Local Kubernetes environment |

## 3. Architecture

The application follows a layered architecture to separate responsibilities and simplify testing and maintenance.

### Application layers

1. **Controller layer:** Receives HTTP requests, validates input, and returns HTTP responses.
2. **Service layer:** Implements business logic and coordinates country lookups and persistence.
3. **SOAP client layer:** Communicates with the external SOAP service.
4. **Repository layer:** Provides database access through Spring Data JPA.
5. **Model and DTO layer:** Represents country information, language information, and API request and response data.
6. **Exception handling layer:** Converts application and integration failures into appropriate HTTP responses.

### Request flow

```text
REST Client
    |
    v
Country REST Controller
    |
    v
Country Integration Service
    |
    +----> SOAP Country Information Service
    |          |
    |          +----> CountryISOCode
    |          |
    |          +----> FullCountryInfo
    |
    v
Country and Language Mapping
    |
    v
Spring Data JPA Repository
    |
    v
MySQL Database
```

The application separates external service communication from persistence and HTTP handling, making the individual components easier to test and maintain.

## 4. Prerequisites

Install the following tools:

- Java 25
- Git
- MySQL
- Docker

For Kubernetes deployment,  install:

- kubectl
- Minikube

Verify your Java and Maven wrapper setup:

```bash
java -version
./mvnw -version
```

## 5. Configuration

The application supports environment-based configuration.

| Variable | Description | Default |
|---|---|---|
| `DB_URL` | JDBC connection URL | Local MySQL database URL |
| `DB_USERNAME` | MySQL username | `country_app` |
| `DB_PASSWORD` | MySQL password | Must be configured |
| `SOAP_COUNTRY_INFO_URL` | SOAP service endpoint | Country Information service endpoint |
| `SOAP_CONNECT_TIMEOUT` | SOAP connection timeout in milliseconds | `5000` |
| `SOAP_REQUEST_TIMEOUT` | SOAP request timeout in milliseconds | `10000` |

The application listens on port `8085` by default.

 database URL:

```text
jdbc:mysql://localhost:3306/country_integration?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```



### Step 1: Configure MySQL

Create the `country_integration` database and a dedicated application user. Grant that user the permissions required by the application.

 SQL:

```sql
CREATE DATABASE country_integration;

CREATE USER 'country_app'@'localhost'
IDENTIFIED BY '';

GRANT ALL PRIVILEGES ON country_integration.*
TO 'country_app'@'localhost';

FLUSH PRIVILEGES;
```

Adjust the MySQL host permissions if the application connects from a different host or network.

### Step 2: Configure environment variables

From the project root, run:

```bash
export DB_URL='jdbc:mysql://localhost:3306/country_integration?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true'
export DB_USERNAME='country_app'

read -rsp "MySQL password: " DB_PASSWORD
echo
export DB_PASSWORD
```


### Step 3: Start the application

```bash
./mvnw spring-boot:run
```

The API should be available at:

```text
http://localhost:8085
```

### Step 4: Verify application health

```bash
curl -i http://localhost:8085/actuator/health
```

A healthy application returns HTTP `200 OK`.

### Step 5: Run automated tests

Execute the test suite:

```bash
./mvnw clean test
```

Build the application:

```bash
./mvnw package
```

The executable JAR will be generated under `target/`.

When you no longer need the exported database password, remove it from the current shell:

```bash
unset DB_PASSWORD
```

## 7. REST API Reference

Base URL:

```text
http://localhost:8085/api/v1/countries
```

### Available endpoints

| HTTP Method | Endpoint | Description | Success Status |
|---|---|---|---|
| POST | `/api/v1/countries` | Retrieve country details through SOAP and save the result | `201 Created` |
| GET | `/api/v1/countries` | Retrieve all saved countries | `200 OK` |
| GET | `/api/v1/countries/{id}` | Retrieve a country by database ID | `200 OK` |
| PUT | `/api/v1/countries/{id}` | Update an existing country | `200 OK` |
| DELETE | `/api/v1/countries/{id}` | Delete a country | `204 No Content` |

### 7.1 Create a country

**Request**

```http
POST /api/v1/countries
Content-Type: application/json
```

 request body:

```json
{
  "name": "Tanzania"
}
```

 command:

```bash
curl -i -X POST http://localhost:8085/api/v1/countries \
  -H 'Content-Type: application/json' \
  -d '{"name":"Tanzania"}'
```

The application normalizes the country name and performs the following operations:

1. Calls the SOAP `CountryISOCode` operation using `sCountryName`.
2. Retrieves the ISO country code.
3. Calls `FullCountryInfo` using `sCountryISOCode`.
4. Maps the returned country and language information into application models.
5. Persists the country information in MySQL.
6. Returns the created resource.

The response contains the country information returned by the integration service and saved by the application.

### 7.2 Retrieve all countries

```bash
curl -i http://localhost:8085/api/v1/countries
```

Returns the countries currently stored in the database.

### 7.3 Retrieve a country by ID

Replace `1` with the ID of an existing record.

```bash
curl -i http://localhost:8085/api/v1/countries/1
```

If the requested country does not exist, the API returns `404 Not Found`.

### 7.4 Update a country

```bash
curl -i -X PUT http://localhost:8085/api/v1/countries/1 \
  -H 'Content-Type: application/json' \
  -d '{"name":"Tanzania"}'
```


### 7.5 Delete a country

```bash
curl -i -X DELETE http://localhost:8085/api/v1/countries/1
```

A successful deletion returns `204 No Content`.

### HTTP error handling

| Status | Meaning |
|---|---|
| `400 Bad Request` | Invalid request or validation failure |
| `404 Not Found` | Requested country does not exist |
| `409 Conflict` | Database integrity conflict |
| `502 Bad Gateway` | Failure communicating with the SOAP service |
| `500 Internal Server Error` | Unexpected server-side error |

## 8. SOAP Integration

The application uses the Country Information SOAP service.

**WSDL location in the project:**

```text
src/main/resources/wsdl/CountryInfoService.wsdl
```

**Default SOAP endpoint:**

```text
http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso
```

WSDL URL for inspection in SoapUI:

```text
http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL
```

### Integration workflow

The country lookup follows this sequence:

1. Receive the country name from the REST client.
2. Normalize the country name.
3. Invoke `CountryISOCode` with the `sCountryName` parameter.
4. Obtain the ISO country code from the SOAP response.
5. Invoke `FullCountryInfo` with the `sCountryISOCode` parameter.
6. Map the response into the application's country and language models.
7. Persist the resulting information using Spring Data JPA.
8. Return the result to the REST client.

The endpoint and timeout values can be overridden through environment variables.

```bash
export SOAP_COUNTRY_INFO_URL='http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso'
export SOAP_CONNECT_TIMEOUT=5000
export SOAP_REQUEST_TIMEOUT=10000
```

The SOAP service is an external dependency. New country lookups require the service to be reachable, while retrieval of previously saved records can be performed from the database.

## 9. Testing 

The project includes automated tests for application behavior at different layers.

| Test Type | Purpose |
|---|---|
| Unit tests | Validate service logic and business rules |
| Controller tests | Verify HTTP endpoints, request validation, and responses |
| Integration tests | Exercise the REST-to-service-to-repository flow |
| Application context tests | Verify that the Spring application context starts correctly |

The test profile uses H2 to support automated testing without requiring the production MySQL database.

Run all tests:

```bash
./mvnw clean test
```

Run the country integration tests specifically:

```bash
./mvnw -Dtest=CountryIntegrationIT test
```

Build the application:

```bash
./mvnw clean package
```

## 10. Docker

The application includes a Dockerfile that builds the application and packages it into a runtime image.

### Step 1: Build the image

Run from the project root:

```bash
docker build -t country-integration:1.0.0 .
```

### Step 2: Run the container


```bash
docker run --rm --name country-integration \
  -p 8085:8085 \
  -e DB_URL='jdbc:mysql://addr:3306/country_integration?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true' \
  -e DB_USERNAME='country_app' \
  -e DB_PASSWORD='' \
  country-integration:1.0.0
```

Replace `addr` and the password placeholder with values appropriate for your environment.


### Step 3: Check the container

```bash
docker ps
docker logs country-integration
```

Check application health:

```bash
curl -i http://localhost:8085/actuator/health
```

## 11. Kubernetes Deployment

The project contains Kubernetes manifests and automation scripts under `k8s/` and `scripts/`.

### Prerequisites

- A running Kubernetes cluster.
- `kubectl` configured to access the cluster.
- A MySQL instance accessible from the application pods.
- The application image available to the cluster.
- A database password stored in a Kubernetes Secret.

### 11.1 Start Minikube

```bash
minikube start
kubectl get nodes
```

Confirm that the node is ready.

### 11.2 Configure the database connection

Review:

```text
k8s/configmap.yaml
```

Ensure the database URL points to a MySQL host reachable from the Kubernetes pods. Do not use `localhost` unless MySQL runs in the same pod.

Confirm that the MySQL account is permitted to connect from the Kubernetes network.

### 11.3 Create the database Secret

Create or update the Secret without writing the password into a manifest:

```bash
read -rsp "MySQL password for country_app: " DB_PASSWORD
echo

kubectl create secret generic country-integration-db \
  --from-literal=DB_PASSWORD="$DB_PASSWORD" \
  --dry-run=client -o yaml | kubectl apply -f -

unset DB_PASSWORD
```

The Secret name and key must match the references in `k8s/deployment.yaml`.


### 11.4 Build the image for Minikube

Make the image available in Minikube's Docker environment:

```bash
eval "$(minikube docker-env)"
docker build -t country-integration:1.0.0 .
```


### 11.5 Deploy the application

Run the provided deployment script:

```bash
bash scripts/deploy-k8s.sh
```

If you need to apply the manifests manually:

```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

### 11.6 Verify the deployment

```bash
kubectl get deployments
kubectl get pods
kubectl get services
```

Run the project's verification script:

```bash
bash scripts/verify-k8s.sh
```

Confirm that the deployment has available replicas and that the application pod is running and ready.

### 11.7 Access the application

Forward a local port to the Kubernetes service:

```bash
kubectl port-forward service/country-integration 18085:8085
```

Keep this command running. In another terminal, test the health endpoint:

```bash
curl -i http://localhost:18085/actuator/health
```

Test the API:

```bash
curl -i http://localhost:18085/api/v1/countries
```

If the Service has a different name in `k8s/service.yaml`, substitute that name in the port-forward command.

## 12. Kubernetes Troubleshooting

### 12.1 Pod is not starting

Check pod status and events:

```bash
kubectl get pods
kubectl describe pod <pod-name>
kubectl get events --sort-by=.metadata.creationTimestamp
```

Inspect the application logs:

```bash
kubectl logs <pod-name>
```

If the container has restarted, inspect its previous logs:

```bash
kubectl logs <pod-name> --previous
```

Look for missing configuration, database connection failures, image pull errors, or health probe failures.

### 12.2 Database connection failures

Check the following:

- `DB_URL` points to the correct MySQL host and database.
- The username and password match the configured database account.
- The Secret exists and its name and key match the Deployment.
- MySQL is listening on the expected interface and port.
- Firewall and network rules allow connections from Kubernetes.
- MySQL grants allow the application to connect from the pod or node network, rather than only from `localhost`.


```bash
kubectl describe deployment country-integration
kubectl logs deployment/country-integration
kubectl get configmap
kubectl get secrets
```

The last command displays Secret names, not their values.

### 12.3 SOAP requests fail

Check that the SOAP endpoint is reachable from the cluster and that the configured URL is correct.

Inspect application logs for SOAP faults, connection failures, or timeouts. Verify the timeout configuration and confirm that the upstream service is operational.

The application should return an appropriate gateway error when an upstream SOAP failure prevents a country lookup.

### 12.4 Port-forward fails

If the selected local port is already in use, choose another port:

```bash
kubectl port-forward service/country-integration 18086:8085
```

Then use:

```text
http://localhost:18086
```

### 12.5 Inspect deployment status

```bash
kubectl rollout status deployment/country-integration
kubectl describe deployment country-integration
kubectl get pods -o wide
kubectl get events --sort-by=.metadata.creationTimestamp
```

## 13. Reliability, Observability, and Scalability

The application uses separate layers for request handling, business logic, SOAP integration, and database persistence. Configurable connection and request timeouts help prevent SOAP calls from waiting indefinitely.

Spring Boot Actuator exposes health and metrics endpoints. Kubernetes startup, readiness, and liveness probes help determine whether a container has started, is ready to receive traffic, or needs restarting.

The REST application is designed to avoid relying on in-memory session state, which supports horizontal scaling. Multiple replicas can be deployed when they share the appropriate database and configuration.



## Project Structure

```text
country-integration/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ncba/countryintegration/
│   │   └── resources/
│   │       └── wsdl/
│   │           └── CountryInfoService.wsdl
│   └── test/
│       └── java/
├── k8s/
│   ├── configmap.yaml
│   ├── deployment.yaml
│   └── service.yaml
├── scripts/
│   ├── deploy-k8s.sh
│   └── verify-k8s.sh
├── Dockerfile
├── .dockerignore
├── .gitignore
├── mvnw
├── pom.xml
└── README.md
```

