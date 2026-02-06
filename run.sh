#!/bin/bash
# Reproducible build and run script for Catty RO-Crate HelloWorld
# This script builds the JAR, runs the SPARQL query, and validates the output

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

echo "Step 1: Building JAR with Maven..."
echo "------------------------------------"
mvn clean package -q -DskipTests
echo "✓ Build completed"
echo

# Verify the JAR was created
if [ ! -f "target/rocrate-helloworld.jar" ]; then
    echo "ERROR: JAR file not found at target/rocrate-helloworld.jar"
    exit 1
fi
echo "✓ JAR file verified: target/rocrate-helloworld.jar"
echo

echo "Step 2: Running SPARQL Query against Wikidata..."
echo "------------------------------------------------"
# Run the Java application
java -jar target/rocrate-helloworld.jar
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

# Display the results
echo "Step 3: Displaying Query Results (Turtle format)..."
echo "---------------------------------------------------"
cat wikidata-rocrate-results.ttl
echo

echo "========================================"
echo "Build and Run: SUCCESS"
echo "========================================"
echo
echo "Summary:"
echo "  - JAR built: target/rocrate-helloworld.jar"
echo "  - Query executed against: https://query.wikidata.org/sparql"
echo "  - Results written: wikidata-rocrate-results.ttl"
echo
