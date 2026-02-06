#!/bin/bash
# Reproducible build and run script for Catty RO-Crate HelloWorld
# This script builds the JAR with reproducible settings, runs the SPARQL query,
# and validates the output

set -e  # Exit on error

echo "========================================"
echo "Catty RO-Crate HelloWorld Build & Run"
echo "========================================"
echo

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    exit 1
fi

echo "Step 1: Building JAR with Maven (reproducible)..."
echo "------------------------------------"
mvn clean package -q -DskipTests
echo "✓ Build completed"
echo

# Verify the JAR was created in target (Maven output location)
if [ ! -f "target/rocrate-helloworld.jar" ]; then
    echo "ERROR: JAR file not found at target/rocrate-helloworld.jar"
    exit 1
fi
echo "✓ JAR file verified: target/rocrate-helloworld.jar"
echo

# Verify the JAR was copied to crate root (for RO-Crate distribution)
if [ ! -f "rocrate-helloworld.jar" ]; then
    echo "ERROR: JAR file not copied to crate root: rocrate-helloworld.jar"
    exit 1
fi
echo "✓ JAR file copied to crate root: rocrate-helloworld.jar"
echo

echo "Step 2: Running SPARQL Query against Wikidata..."
echo "------------------------------------------------"
# Run the Java application with QID argument (default to Q1995545 if not provided)
QID=${1:-"Q1995545"}
echo "Querying QID: $QID"
java -jar rocrate-helloworld.jar "$QID"
echo
echo "✓ Application execution completed"
echo

# Verify the results file was created
if [ ! -f "wikidata-rocrate-results.ttl" ]; then
    echo "ERROR: Results file not found at wikidata-rocrate-results.ttl"
    exit 1
fi
echo "✓ Results file verified: wikidata-rocrate-results.ttl"
echo

# Compute SHA-256 checksums for reproducibility
echo "Step 3: Computing SHA-256 Checksums..."
echo "----------------------------------------"
JAR_SHA256=$(sha256sum rocrate-helloworld.jar | awk '{print $1}')
TTL_SHA256=$(sha256sum wikidata-rocrate-results.ttl | awk '{print $1}')
echo "JAR SHA-256:  $JAR_SHA256"
echo "TTL SHA-256:  $TTL_SHA256"
echo

# Display the results
echo "Step 4: Displaying Query Results (Turtle format)..."
echo "---------------------------------------------------"
cat wikidata-rocrate-results.ttl
echo

echo "========================================"
echo "Build and Run: SUCCESS"
echo "========================================"
echo
echo "Summary:"
echo "  - JAR built (Maven): target/rocrate-helloworld.jar"
echo "  - JAR copied to crate root: rocrate-helloworld.jar"
echo "  - Query executed against: https://query.wikidata.org/sparql"
echo "  - Results written: wikidata-rocrate-results.ttl"
echo
echo "Reproducibility Artifacts:"
echo "  - JAR SHA-256: $JAR_SHA256"
echo "  - TTL SHA-256: $TTL_SHA256"
echo

