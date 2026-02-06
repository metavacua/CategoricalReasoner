FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy compiled JAR
COPY target/catty-0.0.0.jar /app/rocrate-helloworld.jar

# Run application
ENTRYPOINT ["java", "-jar", "rocrate-helloworld.jar"]
