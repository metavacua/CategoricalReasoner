FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy compiled JAR
COPY target/rocrate-helloworld.jar /app/rocrate-helloworld.jar

# Run application
ENTRYPOINT ["java", "-jar", "rocrate-helloworld.jar"]
