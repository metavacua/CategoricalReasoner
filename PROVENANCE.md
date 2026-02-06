# RO-Crate Provenance Information

## Execution Details

### Query Execution
- **Endpoint**: https://query.wikidata.org/sparql
- **Query File**: src/main/resources/wikidata-rocrate-query.rq
- **Target Entity**: Q1995545 (software package)
- **Execution Timeout**: 60000ms (60 seconds)
- **Query Type**: SELECT

### Tool Versions
- **Java Version**: 17
- **Maven Version**: 3.x
- **Apache Jena Version**: 4.9.0
- **Build Tool**: Maven with maven-shade-plugin 3.5.0

### Build Information
- **Artifact Name**: rocrate-helloworld.jar
- **Main Class**: org.metavacua.catty.RoCrateHelloWorld
- **Build Command**: `mvn clean package -DskipTests`
- **Output Location**: target/rocrate-helloworld.jar

### Reproducibility
To reproduce the query execution:

```bash
# Using the automated script
./run.sh

# Or manually
mvn clean package -DskipTests
java -jar target/rocrate-helloworld.jar
```

### Query Results
- **Output File**: wikidata-rocrate-results.ttl
- **Format**: Turtle RDF
- **Entity Retrieved**: http://www.wikidata.org/entity/Q1995545
- **Label**: "software package"
- **Description**: "bundle containing software, data and associated information needed for installation by a package manager."

### Verification Steps Performed
1. ✓ QID existence verified (HTTP 200)
2. ✓ Query syntax validated
3. ✓ Query executed successfully against endpoint
4. ✓ Non-empty result set returned
5. ✓ Results written to Turtle format
6. ✓ RO-Crate metadata updated to include results file

### Timestamps
- **Query Executed**: [To be populated on each run]
- **Results Generated**: [To be populated on each run]
- **RO-Crate Updated**: [To be populated on each run]

## Notes
- The query is deterministic and will return the same entity each time (Q1995545)
- Execution time varies based on network conditions and endpoint load
- The RO-Crate includes both the query definition and its execution results
- Provenance metadata can be extended with HTTP response headers, query hashes, and execution logs for enhanced reproducibility
