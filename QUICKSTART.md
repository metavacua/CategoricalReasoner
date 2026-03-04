# Quick Start Guide - Catty RO-Crate HelloWorld

This guide helps you get started with the improved, reproducible Catty RO-Crate HelloWorld project.

## Prerequisites

### For Local Development
- Java 17 or higher
- Maven 3.6 or higher
- Internet connection (for Wikidata SPARQL endpoint)

### For Container Usage
- Docker 20.10 or higher
- Docker Compose (optional)

## Quick Start Options

### Option 1: Local Build and Run (Recommended)

The simplest way to build and run everything:

```bash
./run.sh
```

This single command will:
1. ✅ Build the JAR with reproducible Maven configuration
2. ✅ Copy JAR to crate root (automated)
3. ✅ Execute SPARQL query against Wikidata
4. ✅ Generate results with provenance metadata
5. ✅ Compute SHA-256 checksums for reproducibility

**Expected Output:**
```
========================================
Catty RO-Crate HelloWorld Build & Run
========================================

Step 1: Building JAR with Maven (reproducible)...
✓ Build completed

Step 2: Running SPARQL Query against Wikidata...
✓ Application execution completed

Step 3: Computing SHA-256 Checksums...
JAR SHA-256:  <hash>
TTL SHA-256:  <hash>

Step 4: Displaying Query Results (Turtle format)...
# ============================================
# Catty RO-Crate HelloWorld - SPARQL Results
# ============================================
# Generated: 2025-02-06 ...
# ...
```

### Option 2: Container Build and Run

Build a complete container from source:

```bash
# Build the image (includes full compilation)
docker build -t catty-rocrate:0.0.0 .

# Run the container (volume mount for output persistence)
docker run --rm -v "$PWD":/app catty-rocrate:0.0.0
```

**Note**: The container runs as non-root user `catty` for security.

### Option 3: Step-by-Step Local Build

For more control over the build process:

```bash
# 1. Clean build
mvn clean package -DskipTests

# 2. Verify JAR locations
ls -lh target/rocrate-helloworld.jar ./rocrate-helloworld.jar

# 3. Run the application
java -jar rocrate-helloworld.jar

# 4. Verify generated results
cat wikidata-rocrate-results.ttl

# 5. Compute checksums manually
sha256sum rocrate-helloworld.jar wikidata-rocrate-results.ttl
```

## Understanding the Outputs

### Generated Files

1. **rocrate-helloworld.jar** (at crate root)
   - Main executable artifact
   - Reproducibly built (fixed timestamp)
   - Part of RO-Crate distribution

2. **wikidata-rocrate-results.ttl**
   - SPARQL query results in Turtle format
   - Includes provenance header with:
     - Generation timestamp
     - Query endpoint
     - Query execution time
     - Query hash
     - Entity count
   - Language-tagged literals (`@en`)

3. **SHA-256 Checksums**
   - Computed and displayed by `run.sh`
   - Used for reproducibility verification
   - Should match between identical builds

## Verifying Reproducibility

### Check JAR Location
```bash
# Both should exist
ls target/rocrate-helloworld.jar  # Maven output
ls rocrate-helloworld.jar          # Crate root (auto-copied)
```

### Check Provenance in TTL
```bash
# View provenance header
head -15 wikidata-rocrate-results.ttl
```

Should see:
```turtle
# ============================================
# Catty RO-Crate HelloWorld - SPARQL Results
# ============================================
# Generated: 2025-02-06 ...
# Endpoint: https://query.wikidata.org/sparql
# Query Execution Time: ... ms
# Query Hash: ...
# Entities Retrieved: ...
# ============================================
```

### Verify Language Tags
```bash
# Check for @en language tags
grep "@en" wikidata-rocrate-results.ttl
```

### Verify RO-Crate Metadata
```bash
# Validate JSON structure
python3 -m json.tool ro-crate-metadata.json > /dev/null && echo "✓ Valid"
```

## Troubleshooting

### Maven Build Fails
```bash
# Check Maven version
mvn -version

# Clean and retry
mvn clean package -DskipTests

# Check Java version
java -version
```

### SPARQL Query Fails
```bash
# Check internet connection
ping -c 3 query.wikidata.org

# Check endpoint availability
curl -I https://query.wikidata.org/sparql

# Check timeout in Java code (default: 60s)
```

### Docker Build Fails
```bash
# Check Docker version
docker --version

# Verify build context
docker build --no-cache -t catty-rocrate:0.0.0 .

# Check for permission issues
# (Container runs as non-root user)
```

### Permission Issues with Output Files
```bash
# Generated files are owned by current user
# If container created them, change ownership:
sudo chown -R $USER:$USER *.ttl

# Or use volume mount with correct permissions:
docker run --rm -v "$PWD":/app -u $(id -u):$(id -g) catty-rocrate:0.0.0
```

## Understanding the Improvements

This project includes several reproducibility and standards compliance improvements:

1. **Reproducible Builds**
   - Fixed build timestamps
   - Deterministic JAR creation
   - Automated crate packaging

2. **Modern APIs**
   - Jena HTTP builder pattern
   - W3C RDF language tags
   - Explicit timeout configuration

3. **RO-Crate 1.1 Compliance**
   - Correct SPDX licensing
   - Proper artifact typing
   - Complete provenance metadata

4. **Container Hardening**
   - Multi-stage builds
   - Non-root execution
   - OCI metadata labels
   - Health checks

5. **Comprehensive Documentation**
   - Implementation details
   - Verification checklists
   - Quick start guides

For detailed information, see:
- `IMPLEMENTATION_SUMMARY.md` - Executive overview
- `REPRODUCIBILITY_SUMMARY.md` - Quick reference
- `REPRODUCIBILITY_IMPROVEMENTS.md` - Deep dive
- `IMPROVEMENTS_CHECKLIST.md` - Verification checklist

## Next Steps

1. **Explore the Code**
   - Read `src/main/java/org/metavacua/catty/RoCrateHelloWorld.java`
   - Check the SPARQL query in `src/main/resources/wikidata-rocrate-query.rq`

2. **Review the RO-Crate**
   - Examine `ro-crate-metadata.json`
   - Understand the artifact relationships

3. **Customize the Query**
   - Modify the SPARQL query
   - Rebuild and run
   - Compare checksums

4. **Explore Containerization**
   - Inspect the Dockerfile
   - Try different build options
   - Test health checks

5. **Verify Reproducibility**
   - Build on different machines
   - Compare SHA-256 checksums
   - Check provenance metadata

## Getting Help

### Documentation
- **Quick Start**: This file
- **Implementation Summary**: `IMPLEMENTATION_SUMMARY.md`
- **Detailed Improvements**: `REPRODUCIBILITY_IMPROVEMENTS.md`
- **Verification Checklist**: `IMPROVEMENTS_CHECKLIST.md`

### Standards References
- **Maven Reproducible Builds**: https://maven.apache.org/guides/mini/guide-reproducible-builds.html
- **RO-Crate 1.1**: https://www.researchobject.org/ro-crate/1.1/
- **SPDX Licenses**: https://spdx.org/licenses/
- **W3C RDF**: https://www.w3.org/RDF/
- **OCI Image Spec**: https://github.com/opencontainers/image-spec

### Project Links
- **Repository**: https://github.com/metavacua/CategoricalReasoner
- **License**: GNU Affero General Public License v3.0

## Summary

The Catty RO-Crate HelloWorld project now provides:
- ✅ Reproducible builds
- ✅ Industry-standard containerization
- ✅ RO-Crate 1.1 compliance
- ✅ Comprehensive provenance tracking
- ✅ Complete documentation

**Run `./run.sh` to see it in action!**
