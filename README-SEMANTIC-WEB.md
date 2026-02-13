# Catty Semantic Web Integration

## Overview

This repository now includes a Java-based semantic web integration backbone that enables automated discovery and packaging of semantic web resources into Research Object Crates (RO-Crate) via GitHub releases.

## Components

### 1. Maven Project (`pom.xml`)
- **Group ID**: `org.metavacua`
- **Artifact ID**: `catty-core`
- **Version**: `1.0.0`
- **Dependencies**:
  - Apache Jena 5.0.0 (ARQ, Core, RDFConnection)
  - JavaPoet 1.13.0 (for code generation)
  - SLF4J 2.0.9 (logging)

### 2. Java Source Code

#### `org.metavacua.catty.Main`
- Entry point for the semantic web discovery process
- Configurable SPARQL endpoint via `SPARQL_ENDPOINT` environment variable
- Defaults to Wikidata SPARQL endpoint

#### `org.metavacua.catty.core.DiscoveryEngine`
- Executes SPARQL queries against external endpoints using Apache Jena
- Uses `RDFConnectionRemote` for connection management
- Reads queries from classpath resources

#### `org.metavacua.catty.core.CrateGenerator`
- Generates RO-Crate 1.2 compliant JSON-LD metadata
- Creates minimal crate structure with discovery results
- Handles empty result sets gracefully

### 3. SPARQL Query (`src/main/resources/queries/discovery.rq`)
Standard SPARQL 1.1 query for discovering Research Object Crate resources using schema.org vocabulary.

### 4. Docker Support (`Dockerfile`)
Multi-stage Docker build:
- Stage 1: Maven 3 + OpenJDK 17 for building
- Stage 2: OpenJDK 17 slim for runtime
- Configurable SPARQL endpoint via environment variable

### 5. GitHub Actions Workflow (`.github/workflows/release.yml`)
Automated CI/CD pipeline:
- Builds JAR on tag push or manual dispatch
- Executes semantic web discovery
- Creates GitHub releases with:
  - Compiled JAR (`catty-core-1.0.0.jar`)
  - Generated RO-Crate metadata (`ro-crate-metadata.json`)

## Usage

### Building Locally
```bash
mvn clean package
```

### Running Discovery
```bash
java -jar target/catty-core-1.0.0.jar
```

### Using Custom SPARQL Endpoint
```bash
export SPARQL_ENDPOINT=https://your-endpoint.org/sparql
java -jar target/catty-core-1.0.0.jar
```

### Docker Build
```bash
docker build -t catty-core:latest .
```

### Docker Run
```bash
docker run -e SPARQL_ENDPOINT=https://query.wikidata.org/sparql catty-core:latest
```

## Output

The system generates `ro-crate-metadata.json` conforming to RO-Crate 1.2 specification with:
- Metadata descriptor referencing RO-Crate 1.2 context
- Root dataset with name, description, license
- Discovery results as `hasPart` items
- ISO 8601 timestamps for all dates

## Architecture

```
┌─────────────────────────────────────────┐
│       GitHub Actions Workflow           │
│  (Tag Push / Manual Dispatch)           │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│       Maven Build Process               │
│  - Compile Java sources                 │
│  - Package with dependencies (Shade)    │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│       Semantic Web Discovery            │
│  Main → DiscoveryEngine → SPARQL        │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│       RO-Crate Generation               │
│  CrateGenerator → JSON-LD output        │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│       GitHub Release                     │
│  - catty-core-1.0.0.jar                 │
│  - ro-crate-metadata.json               │
└─────────────────────────────────────────┘
```

## Technology Stack

- **Language**: Java 17
- **Build Tool**: Apache Maven 3.8+
- **Semantic Web Framework**: Apache Jena 5.0.0
- **Container Runtime**: Docker
- **CI/CD**: GitHub Actions
- **Metadata Standard**: RO-Crate 1.2

## Compliance

This implementation follows the Catty repository constraints:
- ✅ Uses Java ecosystem for semantic web operations
- ✅ No local RDF schema authoring (external sources only)
- ✅ Standard SPARQL 1.1 syntax (no LaTeX wrappers)
- ✅ Automated validation via Maven build
- ✅ Docker containerization for reproducibility
- ✅ GitHub release automation

## Environment Details

**Temporal Baseline**: February 8, 2026
**Build Environment**: Ubuntu 24.04, OpenJDK 21.0.10, Maven 3.8.7
**Runtime**: OpenJDK 17 (Docker), OpenJDK 21 (local)

## Material Gaps Documented

- **Network Connectivity**: External SPARQL endpoint connectivity was blocked in the build environment
- **Contingency**: System creates minimal RO-Crate with empty results when connectivity fails
- **Testing**: Full SPARQL integration requires environment with outbound HTTPS access

## Future Enhancements

1. Add support for multiple SPARQL endpoints
2. Implement query result caching
3. Add SHACL validation for discovered RDF
4. Extend RO-Crate with provenance information
5. Add support for custom query templates
