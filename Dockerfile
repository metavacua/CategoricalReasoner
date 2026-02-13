FROM maven:3-openjdk-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /build/target/catty-core-1.0.0.jar /app/catty-core.jar
ENV SPARQL_ENDPOINT=https://query.wikidata.org/sparql
ENTRYPOINT ["java", "-jar", "/app/catty-core.jar"]
