# Catty RO-Crate HelloWorld

## What This Is

A minimal Research Object Crate (RO-Crate) that demonstrates:
- Java build system working (Maven)
- Apache Jena SPARQL integration
- Real semantic web data retrieval (not fabricated)
- RO-Crate metadata structure
- Containerization and reproducibility

## Reproduction Steps

### Prerequisites

- Maven 3.6+
- Java 17+
- Docker (optional, for containerized execution)
- Internet connection (required for Wikidata SPARQL endpoint)

### 1. Build Java JAR

```bash
mvn clean package
```

Expected output: `target/catty-0.0.0.jar`

### 2. Run JAR Directly

```bash
java -jar target/catty-0.0.0.jar
```

### 3. Build Docker Image

```bash
docker build -t catty:rocrate-helloworld .
```

### 4. Run Container

```bash
docker run --rm catty:rocrate-helloworld
```

### 5. Save Results to File

```bash
docker run --rm catty:rocrate-helloworld > wikidata-retrieval-results.txt
```

## Expected Output

The program retrieves Wikidata entities related to "Research Object Crate" and outputs:

```
Catty: RO-Crate HelloWorld
Retrieving Research Object Crate information from Wikidata...

Executing SPARQL query against Wikidata...

=== Retrieved Wikidata Entities ===

QID: https://www.wikidata.org/entity/Q...
Label: [entity name]
Description: [entity description]

...

Total entities retrieved: N

RO-Crate HelloWorld complete.
```

## Validation

### Validate Java Build

```bash
mvn clean compile
mvn clean package
```

Expected: `BUILD SUCCESS`

### Validate JSON-LD Structure

```bash
# Check if JSON is valid
jq empty ro-crate-metadata.json

# Validate RO-Crate structure (if ro-crate-validator is available)
ro-crate-validate ro-crate-metadata.json
```

### Verify Docker Build

```bash
docker build -t catty:rocrate-helloworld .
docker run --rm catty:rocrate-helloworld
```

Expected: Container builds successfully and executes query

### Verify Data Source

The SPARQL query must:
- Execute against real Wikidata endpoint: https://query.wikidata.org/sparql
- Search for items containing "research object crate" (not hardcoded QIDs)
- Complete within 60 seconds
- Return non-empty results with valid QID URIs

## Data Source

- **Wikidata Query Service**: https://query.wikidata.org/sparql
- **Query searches for**: Items with "research object crate" in label
- **Query limits to**: 5 results (avoids timeout, reasonable for HelloWorld)
- **Requirement**: Internet connectivity for SPARQL endpoint access

## RO-Crate Structure

This Research Object Crate contains:

- `ro-crate-metadata.json` - RO-Crate 1.1 metadata
- `pom.xml` - Maven build configuration
- `Dockerfile` - Container definition
- `src/main/java/org/metavacua/catty/RoCrateHelloWorld.java` - Java source code
- `src/main/resources/wikidata-rocrate-query.rq` - SPARQL query
- `README-ROCRATE.md` - This documentation

## Technical Details

### Java Stack

- **Language**: Java 17
- **Build System**: Maven
- **Dependencies**: Apache Jena ARQ 4.9.0
- **Packaging**: Fat JAR (includes all dependencies)

### Docker Stack

- **Base Image**: `eclipse-temurin:17-jre-alpine`
- **Runtime**: Java 17 JRE
- **Image Size**: Minimal (alpine-based)

### SPARQL Integration

- **Endpoint**: Wikidata Query Service
- **Protocol**: HTTP POST
- **Timeout**: 60 seconds
- **User-Agent**: `CattyRoCrateHelloWorld/1.0 (https://github.com/metavacua/CategoricalReasoner)`

## License

- Code: AGPL-3.0 (https://www.gnu.org/licenses/agpl-3.0)
- Documentation: CC BY-SA 4.0 (https://creativecommons.org/licenses/by-sa/4.0/)

## References

- **RO-Crate Specification 1.1**: https://www.researchobject.org/ro-crate-1.1
- **Apache Jena**: https://jena.apache.org/
- **Wikidata Query Service**: https://query.wikidata.org/sparql
- **Maven**: https://maven.apache.org/
- **Docker**: https://www.docker.com/

## Troubleshooting

### Build Fails

Check Java version:
```bash
java -version
```

Must be Java 17 or higher.

### Docker Build Fails

Ensure Docker is running:
```bash
docker ps
```

### Query Returns No Results

The query searches Wikidata dynamically. If "Research Object Crate" has no matching entities, the program will exit with error code 1.

### Query Times Out

The query has a 60-second timeout. Check network connectivity to Wikidata:
```bash
ping query.wikidata.org
curl -I https://query.wikidata.org/sparql
```

## Version

- **Version**: 0.0.0 (HelloWorld / pre-release)
- **Status**: Minimal reproducible example
- **Purpose**: Demonstration of Java + Jena + SPARQL integration
