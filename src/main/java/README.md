# Java Source Code

This directory contains the Java source code for the Catty thesis project.

## RO-Crate HelloWorld

The `org/metavacua/catty/RoCrateHelloWorld.java` file implements a minimal RO-Crate demonstrator that:

1. Queries Wikidata SPARQL endpoint for arbitrary QIDs
2. Retrieves entity labels and descriptions in Turtle RDF format
3. Generates provenance metadata with execution details
4. Supports LLM-assisted semantic mapping validation

### Usage

**Build and run with Maven:**
```bash
mvn clean package -DskipTests
java -jar rocrate-helloworld.jar Q1995545
```

**Use the convenience script:**
```bash
./run.sh Q1995545
```

### QID Input Parameter

The tool accepts a single argument: the Wikidata QID to query.

- **Format**: `Q<digits>` (e.g., Q1995545, Q80, Q5)
- **Validation**: The tool validates QID format before querying
- **Output**: Turtle RDF with provenance metadata

### Example QIDs

| QID | Label | Description |
|-----|-------|-------------|
| Q1995545 | software package | bundle containing software, data and associated information |
| Q80 | software | programs running on a computer |
| Q5 | human | any living or extinct member of the family Hominidae |
| Q288425 | open-source software | software with source code available under a license |

### LLM-Assisted Semantic Mapping

This tool enables LLMs to validate their semantic mapping proposals:

1. **Proposal Phase**: LLM suggests a QID for a concept
2. **Validation Phase**: Tool queries Wikidata and returns actual entity data
3. **Verification Phase**: Compare retrieved semantics with intended concept
4. **Iteration Phase**: Try alternative QIDs if mismatch found

This workflow ensures that LLMs actively verify their assumptions against live data, reducing downstream errors.

### Generated Output

The tool generates `wikidata-rocrate-results.ttl` with:

- Turtle RDF representation of the queried entity
- Provenance comments including:
  - Generation timestamp
  - Query endpoint URL
  - QID queried
  - Query execution time
  - Query hash for reproducibility
  - Entity count

### Technology Stack

- **Jena ARQ**: SPARQL query execution
- **Jena HTTP Builder**: Modern, explicit timeout configuration
- **W3C RDF Standards**: Language-tagged literals (`@en`)
- **Turtle Format**: Human-readable RDF serialization

### Error Handling

The tool handles various error conditions:

- **Invalid QID format**: Validates before querying
- **Network failures**: HTTP error handling with informative messages
- **Empty results**: Detects and reports missing entity data
- **Query timeout**: 60-second timeout with clear error messages

### Build Process

The Maven build process includes:

1. **Compilation**: Java source compilation with Java 17 target
2. **Dependency Shading**: Includes Jena libraries in standalone JAR
3. **JAR Creation**: Reproducible build with fixed timestamp
4. **Metadata Generation**: Auto-generates RO-Crate metadata from template
5. **Copy to Root**: Places JAR at project root for RO-Crate distribution

See `pom.xml` for complete build configuration.
