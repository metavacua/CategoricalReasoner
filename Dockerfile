# Multi-stage build for reproducible containerization
# Stage 1: Builder - Compiles Java source with Maven
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Install Maven
RUN apk add --no-cache maven

# Copy pom.xml and download dependencies first for layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source code
COPY src ./src

# Build the application (this will copy JAR to crate root via Maven)
RUN mvn clean package -q -DskipTests

# Stage 2: Runtime - Minimal JRE with built JAR
FROM eclipse-temurin:17-jre-alpine

# OCI metadata labels for image provenance
LABEL org.opencontainers.image.title="Catty RO-Crate HelloWorld"
LABEL org.opencontainers.image.description="Minimal Java program querying Wikidata via SPARQL"
LABEL org.opencontainers.image.version="0.0.0"
LABEL org.opencontainers.image.licenses="AGPL-3.0-only"
LABEL org.opencontainers.image.vendor="MetaVacua"
LABEL org.opencontainers.image.source="https://github.com/metavacua/CategoricalReasoner"

# Create non-root user for security
RUN addgroup -S catty && adduser -S catty -G catty

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /build/rocrate-helloworld.jar /app/rocrate-helloworld.jar
COPY --from=builder /build/wikidata-rocrate-results.ttl /app/wikidata-rocrate-results.ttl 2>/dev/null || true

# Change ownership to non-root user
RUN chown -R catty:catty /app

# Switch to non-root user
USER catty

# Health check - verify JAR is accessible and can show version/help
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD java -jar /app/rocrate-helloworld.jar || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "/app/rocrate-helloworld.jar"]
