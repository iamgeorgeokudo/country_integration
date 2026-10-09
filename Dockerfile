# Stage 1: Build the application
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

COPY src/ src/

RUN ./mvnw -B clean package -DskipTests


# Stage 2: Run the application
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create a non-root user
RUN groupadd --system spring \
    && useradd --system --gid spring spring

# Copy the packaged application
COPY --from=builder /app/target/country-integration-*.jar app.jar

# Run as a non-root user
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]