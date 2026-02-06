# Reproducibility and Canonical Artifacts Improvements

This document describes improvements made to the Catty RO-Crate HelloWorld project to achieve reproducible builds and canonical artifacts.

## Overview

The following issues have been addressed to improve reproducibility, canonical artifact generation, and adherence to industry standards:

### 1. Maven / Java Build Improvements

#### 1.1 Reproducible JAR Configuration
- **Added `project.build.outputTimestamp`**: Fixed timestamp (`2024-01-01T00:00:00Z`) ensures deterministic JAR creation
- **Maven shade plugin filters**: Excludes signature files (*.SF, *.DSA, *.RSA) that vary between builds
- **Create sources jar disabled**: Reduces build variation

#### 1.2 Crate Packaging Automation
- **Added maven-resources-plugin**: Automatically copies `target/rocrate-helloworld.jar` to crate root (`./rocrate-helloworld.jar`)
- **Build phase integration**: Packaging occurs during Maven `package` phase
- **RO-Crate compliance**: JAR now exists at the location referenced in `ro-crate-metadata.json`

#### 1.3 Maven Coordinates Alignment
- **Current state**: `artifactId=catty`, `version=0.0.0`, finalName=`rocrate-helloworld`
- **Design decision**: The `finalName` divergence is intentional for RO-Crate distribution requirements
- **Documentation**: This divergence is documented and automated via Maven resources plugin

### 2. Java/Jena Execution Improvements

#### 2.1 HTTP Query Execution Builder Pattern
- **Updated from**: `QueryExecutionFactory.sparqlService()` (legacy API)
- **Updated to**: `QueryExecutionHTTP.newBuilder()` (modern builder pattern)
- **Benefits**:
  - More explicit timeout configuration
  - Better portability across Jena versions
  - Clearer API semantics
  - Industry best practice

#### 2.2 RDF Literal Normalization
- **Added language tags**: All literals now preserve `@en` language tags from SPARQL results
- **Implementation**: `model.createLiteral(label, "en")` instead of plain string literals
- **Standards compliance**: Aligns with W3C RDF best practices

### 3. RO-Crate (1.1) Modeling Improvements

#### 3.1 License Modeling
- **Previous**: Used Wikidata Q341 ("free software") with `@type: SoftwareSourceCode` (semantically incorrect)
- **Corrected**: Uses SPDX license URL `https://spdx.org/licenses/AGPL-3.0-only.html`
- **Type**: `CreativeWork` (appropriate for license resources)
- **Name**: "GNU Affero General Public License v3.0"
- **Benefits**: Industry-standard licensing, RO-Crate compliant, clearly identifies the actual license

#### 3.2 JAR Artifact Type
- **Previous**: `@type: SoftwareSourceCode` (inappropriate for binary artifacts)
- **Corrected**: `@type: File` with `encodingFormat: application/java-archive`
- **Added relationship**: `isBasedOn` links to the Java source file
- **Benefits**: Proper semantic modeling, distinguishes source from binary

#### 3.3 Generated Artifacts Provenance
- **Added TTL header comments**: Each output includes:
  - Generation timestamp
  - Query endpoint URL
  - Query execution time
  - Query hash (for verification)
  - Entity count
  - Reproducibility note
- **Benefits**: Clear provenance tracking, reproducibility verification

### 4. Containerization (Docker) Improvements

#### 4.1 Multi-Stage Build
- **Stage 1 (builder)**: Uses JDK+Maven to compile the JAR
- **Stage 2 (runtime)**: Minimal JRE image with only the built JAR
- **Benefits**:
  - Can build from source without pre-built artifacts
  - Smaller final image
  - Reproducible build process
  - Industry best practice

#### 4.2 Container Hardening
- **Non-root user**: Creates and runs as `catty:catty` user
- **OCI metadata labels**: Includes standard labels for image provenance:
  - Title, description, version
  - License, vendor, source URL
- **Health check**: Configured to verify JAR accessibility
- **Benefits**: Security best practices, better container maintainability

### 5. Directory and File Structure

#### 5.1 Build Output Management
- **Maven output**: `target/rocrate-helloworld.jar` (standard Maven location)
- **Crate distribution**: `./rocrate-helloworld.jar` (copied by Maven)
- **Generated results**: `./wikidata-rocrate-results.ttl` (at root for RO-Crate)

#### 5.2 Git Configuration
- **JAR handling**: `rocrate-helloworld.jar` is tracked (part of RO-Crate)
- **Results handling**: `wikidata-rocrate-results.ttl` can be tracked with provenance comments
- **Documentation**: `.gitignore` includes comments explaining file handling decisions

## Reproducibility Workflow

### Building Locally

```bash
# Build and run the complete workflow
./run.sh
```

This will:
1. Build JAR with reproducible Maven configuration
2. Copy JAR to crate root (automated)
3. Execute SPARQL query against Wikidata
4. Generate results with provenance header
5. Compute SHA-256 checksums of outputs

### Building with Docker

```bash
# Build the image (includes full compilation)
docker build -t catty-rocrate:0.0.0 .

# Run the container (volume mount for output persistence)
docker run --rm -v "$PWD":/app catty-rocrate:0.0.0
```

### Verifying Reproducibility

The following artifacts provide reproducibility evidence:

1. **JAR SHA-256**: Computed and displayed by `run.sh`
2. **TTL SHA-256**: Computed and displayed by `run.sh`
3. **Query hash**: Embedded in TTL provenance header
4. **Build timestamp**: Fixed in `pom.xml` via `outputTimestamp`
5. **Execution metadata**: Documented in TTL provenance header

## Standards Compliance

### Maven
- ✅ Reproducible builds: `project.build.outputTimestamp`
- ✅ Standard directory layout: `src/main/java`, `src/main/resources`
- ✅ Proper plugin configuration: shade, resources, compiler

### Java/Jena
- ✅ Modern Jena API: HTTP builder pattern
- ✅ W3C RDF compliance: Language-tagged literals
- ✅ Proper resource management: Query execution cleanup

### RO-Crate 1.1
- ✅ License modeling: SPDX URL with CreativeWork type
- ✅ Artifact typing: File for binary, SoftwareSourceCode for source
- ✅ Metadata completeness: All required fields populated
- ✅ Context resolution: Proper @context definitions

### Docker/OCI
- ✅ Multi-stage builds: Separate build and runtime stages
- ✅ OCI labels: Standard metadata labels included
- ✅ Security: Non-root user execution
- ✅ Health checks: Container health monitoring

### Git
- ✅ Appropriate ignores: Build artifacts excluded, tracked files intentional
- ✅ Documentation: `.gitignore` comments explain decisions

## Remaining Considerations

### Timestamp Normalization
While `project.build.outputTimestamp` provides build reproducibility, the SPARQL results TTL includes execution timestamps (for provenance). To achieve fully reproducible research results:

1. Consider using Wikidata dumps with versioned snapshots
2. Pin specific Wikidata dataset versions if available
3. Document expected result variations due to endpoint changes

### Container Image Digests
For maximum reproducibility, consider:
1. Pinning base image digests: `FROM eclipse-temurin:17-jre-alpine@sha256:...`
2. Using buildx for multi-platform reproducibility
3. Signing images with cosign or similar

### Dependency Locking
For absolute reproducibility across environments:
1. Consider Maven Enforcer plugin for dependency verification
2. Use `mvn dependency:tree` to document full dependency graph
3. Consider using tools like `mvnd` for faster, reproducible builds

## Summary

All major issues identified in the code review have been addressed:

- ✅ RO-Crate metadata now matches actual build outputs
- ✅ JAR builds are reproducible with fixed timestamps
- ✅ Java code uses modern Jena HTTP builder pattern
- ✅ RDF literals include proper language tags
- ✅ License modeling follows RO-Crate and SPDX standards
- ✅ Dockerfile provides full multi-stage build
- ✅ Container hardening includes non-root user and metadata
- ✅ Generated artifacts include comprehensive provenance
- ✅ Directory structure is documented and intentional

The project now provides a solid foundation for reproducible research object creation with canonical artifacts.
