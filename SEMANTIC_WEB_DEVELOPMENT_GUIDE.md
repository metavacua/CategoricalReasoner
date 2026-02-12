# Catty Semantic Web Development Environment

## Overview

This document describes the comprehensive semantic web technology development environment built for the Catty Categorical Reasoner repository using Java, Apache Jena, and Javadoc.

**Last Updated:** January 15, 2026
**Jena Version:** 5.1.0
**Java Version:** 21

## Technology Stack

### Core Libraries

```
Apache Jena 5.1.0:
├── jena-core            RDF API and graph operations
├── jena-arq             SPARQL 1.1 query engine
├── jena-tdb2            Persistent RDF storage with transactions
├── jena-fuseki-main     SPARQL server with HTTP endpoints
├── jena-fuseki-webapp   Web UI for Fuseki
├── jena-shacl           RDF validation with SHACL shapes
├── jena-ontapi          Ontology API and reasoning
└── jena-rdfconnection   RDF connection utilities

Jetty 12.0.8:
├── jetty-server         HTTP server core
└── jetty-servlet        Servlet container

Jackson 2.16.1:
├── jackson-core         JSON processing
└── jackson-databind       JSON databinding

Testing & Logging:
├── JUnit Jupiter 5.10.2   Testing framework
└── SLF4J 2.0.12           Logging facade
```

## Localhost-First Development

All semantic web technologies are developed and tested to work via localhost before any external deployment.

### Default Localhost Endpoints

```
Primary SPARQL Server (Fuseki):
├─ SPARQL Query:  http://localhost:3030/catty/query
├─ SPARQL Update:  http://localhost:3030/catty/update
├─ Fuseki Web UI:  http://localhost:3030/catty
└─ Dataset:        /data (TDB2 persistence)

HTTP RDF Server (Jetty):
├─ RDF Serving:    http://localhost:8080/rdf/
├─ Graph Access:   http://localhost:8080/rdf/graph/{name}
├─ SPARQL Query:   http://localhost:8080/rdf/query
└─ SPARQL Update:  http://localhost:8080/rdf/update
```

### Starting the Servers

#### Option 1: Maven Execution

```bash
# Start SemanticWebServer (Fuseki SPARQL server)
mvn exec:java -Dexec.mainClass="org.catty.server.SemanticWebServer"

# Start standalone HTTP RDF server
mvn exec:java -Dexec.mainClass="org.catty.http.SemanticWebHttpServer"

# Generate Javadoc
mvn javadoc:javadoc

# Run tests
mvn test

# Create executable JAR with dependencies
mvn clean package
```

#### Option 2: Using the Executable JAR

```bash
# After building with Maven
java -jar target/categorical-reasoner-jar-with-dependencies.jar

# With custom host/port
java -jar target/categorical-reasoner-jar-with-dependencies.jar localhost 3030 /catty
```

### Docker Deployment

```bash
# Build Docker image
docker build -t catty/categorical-reasoner .

# Run with port mapping
docker run -p 3030:3030 -p 8080:8080 catty/categorical-reasoner

# Run with persistent data volume
docker run -p 3030:3030 -p 8080:8080 -v $(pwd)/data:/app/data catty/categorical-reasoner
```

### Docker Compose

```yaml
version: '3.8'
services:
  catty-reasoner:
    build: .
    ports:
      - "3030:3030"
      - "8080:8080"
    volumes:
      - ./data:/app/data
      - ./ontology:/app/ontology
    environment:
      - JAVA_OPTS=-server -Xmx4g -XX:+UseG1GC
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3030/catty/query"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
```

## Format Support

### RDF Serialization Formats

| Format | Content-Type | Extension | Jena Lang | Description |
|--------|-------------|-----------|-----------|-------------|
| Turtle | `text/turtle` | `.ttl` | TURTLE | Human-readable RDF format |
| JSON-LD | `application/ld+json` | `.jsonld` | JSONLD | JSON with Linked Data context |
| RDF/XML | `application/rdf+xml` | `.rdf`, `.owl` | RDFXML | XML serialization |
| N-Triples | `application/n-triples` | `.nt` | NTRIPLES | Simple line-based format |
| N3 | `text/n3` | `.n3` | N3 | Extended Turtle with rules |
| N-Quads | `application/n-quads` | `.nq` | NQUADS | Quads for named graphs |

### SPARQL Result Formats

| Format | Content-Type | Description |
|--------|-------------|-------------|
| JSON | `application/sparql-results+json` | SPARQL 1.1 JSON bindings |
| XML | `application/sparql-results+xml` | SPARQL 1.1 XML results |
| CSV | `text/csv` | Comma-separated values |
| TSV | `text/tab-separated-values` | Tab-separated values |

## Core Components Documentation

### 1. SemanticWebServer (org.catty.server)

**Purpose:** Main entry point for Fuseki-based SPARQL server

**Key Features:**
- TDB2 persistent storage with full ACID transactions
- SPARQL 1.1 Query and Update endpoints
- Ontology pre-loading from disk
- Localhost-only binding for security
- Graceful shutdown handling

**Configuration:**
```java
// Default configuration
String host = "localhost";           // Bind to localhost only
int port = 3030;                      // HTTP port
String datasetName = "/catty";       // Dataset path
String ontologyDir = "ontology";     // Ontology source directory
String dataDir = "data";              // TDB2 storage location
```

**Usage:**
```java
SemanticWebServer server = new SemanticWebServer();
server.start();

// Access endpoints
String queryUrl = "http://localhost:3030/catty/query";
String updateUrl = "http://localhost:3030/catty/update";
String uiUrl = "http://localhost:3030/catty";

// Stop server
server.stop();
```

### 2. RDFService (org.catty.rdf)

**Purpose:** Core RDF processing and serialization service

**Key Features:**
- Multi-format RDF serialization (Turtle, JSON-LD, RDF/XML, N-Triples)
- Named graph management
- Model creation with Catty namespace prefixes
- Format negotiation based on HTTP Accept headers
- Content-type to format mapping

**Format Negotiation:**
```java
// Content-Type negotiation example
String acceptHeader = "text/turtle, application/ld+json;q=0.9";
RDFContentType contentType = negotiateContentType(acceptHeader);
// Returns: RDFContentType.TURTLE (higher priority)
```

**Serialization:**
```java
RDFService rdfService = new RDFService(dataset);
Model model = rdfService.getDefaultModel();

// Serialize to multiple formats
String turtle = rdfService.serializeModel(model, RDFContentType.TURTLE);
String jsonld = rdfService.serializeModel(model, RDFContentType.JSON_LD);
String rdfxml = rdfService.serializeModel(model, RDFContentType.RDF_XML);
String ntriples = rdfService.serializeModel(model, RDFContentType.N_TRIPLES);
```

### 3. SPARQLService (org.catty.sparql)

**Purpose:** Comprehensive SPARQL query and update execution

**Key Features:**
- SPARQL 1.1 SELECT, CONSTRUCT, DESCRIBE, ASK queries
- SPARQL 1.1 UPDATE for modifying data
- Parameterized queries with variable substitution
- Multiple result formats (JSON, XML, CSV, TSV)
- Query validation and syntax checking
- Transaction support for updates
- Dataset statistics

**Query Execution:**
```java
SPARQLService sparqlService = new SPARQLService(dataset);

// SELECT query
String selectQuery = """
    SELECT ?s ?p ?o 
    WHERE { ?s ?p ?o }
    LIMIT 10
    """;
QueryResult result = sparqlService.executeQuery(selectQuery);

// CONSTRUCT query
String constructQuery = """
    CONSTRUCT { ?s rdf:type :MyClass }
    WHERE { ?s :hasProperty "value" }
    """;
Model constructed = sparqlService.executeConstruct(constructQuery);

// ASK query
String askQuery = "ASK { ?s rdf:type :Category }";
QueryResult askResult = sparqlService.executeQuery(askQuery);

// UPDATE query
String updateQuery = """
    INSERT DATA {
        :subject :predicate "value" .
    }
    """;
sparqlService.executeUpdate(updateQuery);
```

**Result Serialization:**
```java
// Serialize query results to different formats
String jsonResults = sparqlService.serializeResults(result, SPARQLResultFormat.JSON);
String xmlResults = sparqlService.serializeResults(result, SPARQLResultFormat.XML);
String csvResults = sparqlService.serializeResults(result, SPARQLResultFormat.CSV);
String tsvResults = sparqlService.serializeResults(result, SPARQLResultFormat.TSV);

// Query validation
ValidationResult validation = sparqlService.validateQuery(queryString);
if (validation.valid) {
    System.out.println("Query syntax is valid");
} else {
    System.out.println("Query error: " + validation.errorMessage);
}
```

### 4. SemanticWebHttpServer (org.catty.http)

**Purpose:** Standalone HTTP server with Jetty for RDF serving

**Key Features:**
- RESTful RDF endpoints with content negotiation
- Named graph access via HTTP
- SPARQL query and update endpoints
- CORS support for local development
- Jetty-based async request handling
- Integration with RDFService for serialization

**HTTP Endpoints:**

```
GET /rdf/                    - Get entire dataset (content negotiation)
GET /rdf/graph/{name}       - Get named graph
PUT /rdf/graph/{name}       - Replace named graph
DELETE /rdf/graph/{name}    - Delete named graph
POST /rdf/query             - Execute SPARQL query
POST /rdf/update            - Execute SPARQL update
```

**Content Negotiation:**
```
GET /rdf/graph/myGraph
Accept: text/turtle           → Returns Turtle format
Accept: application/ld+json   → Returns JSON-LD format
Accept: application/rdf+xml   → Returns RDF/XML format
```

**Usage:**
```java
SemanticWebHttpServer httpServer = new SemanticWebHttpServer(dataset);
httpServer.start();

// Access endpoints
// http://localhost:8080/rdf/
// http://localhost:8080/rdf/graph/{name}
// http://localhost:8080/rdf/query
// http://localhost:8080/rdf/update

httpServer.stop();
```

### 5. RDFHttpHandler (org.catty.http)

**Purpose:** HTTP servlet for RDF content serving with content negotiation

**Key Features:**
- Full HTTP verb support (GET, POST, PUT, DELETE, OPTIONS)
- Content-Type based RDF format parsing
- Accept header based format negotiation
- CORS headers for cross-origin local development
- SPARQL endpoint handlers
- Named graph management
- Proper HTTP status codes

**Request Examples:**

```bash
# Get entire dataset in Turtle
curl -H "Accept: text/turtle" http://localhost:8080/rdf/

# Get specific graph in JSON-LD
curl -H "Accept: application/ld+json" \
     http://localhost:8080/rdf/graph/mycategory

# SPARQL query
curl -X POST -H "Content-Type: application/sparql-query" \
     -H "Accept: application/sparql-results+json" \
     -d "SELECT * WHERE { ?s ?p ?o } LIMIT 10" \
     http://localhost:8080/rdf/query

# SPARQL update
curl -X POST -H "Content-Type: application/sparql-update" \
     -d "INSERT DATA { :s :p :o . }" \
     http://localhost:8080/rdf/update

# Replace named graph
curl -X PUT -H "Content-Type: text/turtle" \
     -d "@prefix : <http://example.org/> . :s :p \"value\" ." \
     http://localhost:8080/rdf/graph/mycategory
```

## Command-Line Tools

### RDF Processing Tools

#### riot - RDF Toolkit

**Purpose:** RDF format conversion, validation, and processing

**Usage:**
```bash
# Validate RDF files
riot --validate ontology.ttl
riot --validate ontology.jsonld

# Convert between formats
riot --output=TURTLE ontology.jsonld > output.ttl
riot --output=RDFXML ontology.ttl > output.rdf
riot --output=NTRIPLES ontology.rdf > output.nt
riot --output=JSONLD ontology.ttl > output.jsonld

# Pretty-printing
riot --formatted=TURTLE ontology.ttl  # Better formatting

# Count triples
riot --count ontology.jsonld

# Load into TDB2 database
tdb2.tdbloader --loc=data ontology/*.jsonld

# Dump from TDB2 database
tdb2.tdbdump --loc=data > dump.ttl
```

**Installation:**
```bash
# Download Jena binaries
wget https://archive.apache.org/dist/jena/binaries/apache-jena-5.1.0.tar.gz
tar -xzf apache-jena-5.1.0.tar.gz
export PATH=$PATH:$(pwd)/apache-jena-5.1.0/bin

# Verify installation
riot --version
```

#### arq - SPARQL Processor

**Purpose:** Execute SPARQL queries from command line

**Usage:**
```bash
# Run SPARQL query on RDF file
arq --data ontology.ttl --query queries/example.rq

# Run SPARQL query on multiple files
arq --data data1.ttl --data data2.ttl --query query.rq

# Use TDB2 database
arq --loc=data --query queries/sparql-examples.rq

# Result formats
arq --data ontology.ttl --query query.rq --results=JSON
arq --data ontology.ttl --query query.rq --results=XML
arq --data ontology.ttl --query query.rq --results=CSV

# Update commands
arq --data data.ttl --update update.ru
```

#### tdb2.tdbloader & tdb2.tdbquery

**tdb2.tdbloader** - Load RDF into TDB2 database:
```bash
# Create new database
tdb2.tdbloader --loc=data ontology/catty-categorical-schema.jsonld

# Append to existing database
tdb2.tdbloader --loc=data --graph=http://catty.org/graph2 ontology/morphism-catalog.jsonld

# Load multiple files
tdb2.tdbloader --loc=data ontology/*.jsonld

# Statistics
tdb2.tdbloader --loc=data --stats stats.txt ontology.ttl
```

**tdb2.tdbquery** - Query TDB2 database:
```bash
# Interactive query mode
tdb2.tdbquery --loc=data

# Execute query file
tdb2.tdbquery --loc=data --query=queries/select-all.rq

# Result formats
tdb2.tdbquery --loc=data --query=query.rq --results=JSON
```

#### shacl - RDF Validation

**Purpose:** Validate RDF data using SHACL shapes

**Usage:**
```bash
# Validate against SHACL shapes
shacl validate --shapes ontology/catty-shapes.ttl --data ontology/catty-categorical-schema.jsonld

# Output validation report
shacl validate --shapes shapes.ttl --data data.jsonld --report report.ttl

# Generate validation report in different format
shacl validate --shapes shapes.ttl --data data.jsonld --report-format=JSONLD
```

#### fuseki-server - Standalone SPARQL Server

**Purpose:** Run a standalone SPARQL server

**Usage:**
```bash
# Start server with in-memory dataset
fuseki-server --update --mem /ds

# Start server with TDB2 database
fuseki-server --loc=data /ds

# Configuration file
fuseki-server --config=config.ttl

# Custom port
fuseki-server --port=3030 --loc=data /catty

# UI and update support
fuseki-server --update --loc=data /catty

# Background daemon
fuseki-server --loc=data /catty &
```

## Javadoc Documentation

### Javadoc Generation

**Generate all Javadoc:**
```bash
# Maven Javadoc plugin
mvn javadoc:javadoc

# Full build with Javadoc
mvn clean compile test javadoc package

# View generated Javadoc
open target/site/apidocs/index.html
```

### Javadoc Configuration

```xml
<!-- Maven Javadoc Plugin Configuration -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.6.3</version>
    <configuration>
        <show>private</show>              <!-- Show all classes/methods -->
        <nohelp>false</nohelp>             <!-- Include help link -->
        <failOnError>true</failOnError>    <!-- Fail on Javadoc errors -->
        <detectJavaApiLink>true</detectJavaApiLink>  <!-- Link to JDK docs -->
        <links>
            <!-- Link to Jena Javadoc -->
            <link>https://jena.apache.org/documentation/javadoc/arq/</link>
            <link>https://jena.apache.org/documentation/javadoc/core/</link>
            <!-- Link to JDK docs -->
            <link>https://docs.oracle.com/en/java/javase/21/docs/api/</link>
        </links>
        <additionalOptions>
            <additionalOption>-Xdoclint:none</additionalOption>
        </additionalOptions>
    </configuration>
</plugin>
```

### Javadoc Best Practices

**Class Documentation Template:**
```java
/**
 * Brief description of the class purpose.
 * 
 * Detailed explanation including:
 * - Primary responsibilities
 * - Key design decisions
 * - Thread safety considerations
 * 
 * <h2>Example Usage</h2>
 * <pre>{@code
 * // Example code showing typical usage
 * YourClass instance = new YourClass();
 * instance.doSomething();
 * }</pre>
 * 
 * @author Catty Development Team
 * @version 1.0.0
 * @since 2026-01-15
 * 
 * @see RelatedClass
 */
public class YourClass {
    // Class implementation
}
```

**Method Documentation Template:**
```java
/**
 * Brief description of the method.
 * 
 * Detailed explanation including:
 * - What the method does
 * - Pre-conditions
 * - Post-conditions
 * - Side effects
 * 
 * @param parameter1 description of parameter1 (include null constraints)
 * @param parameter2 description of parameter2 (include null constraints)
 * @return description of return value (include null constraints)
 * @throws ExceptionType1 when this specific condition occurs
 * @throws ExceptionType2 when this different condition occurs
 * 
 * @implNote Implementation details or performance characteristics
 * 
 * @see #relatedMethod(String)
 */
public ReturnType methodName(ParamType1 parameter1, ParamType2 parameter2) 
        throws ExceptionType1, ExceptionType2 {
    // Method implementation
}
```

## Testing

### Test Coverage

**Unit Tests:**
- RDFService format conversion and serialization
- SPARQLService query execution and validation
- Format negotiation logic
- Namespace management
- Error handling edge cases

**Integration Tests:**
- Server startup and shutdown
- SPARQL endpoint accessibility
- RDF serving with content negotiation
- Concurrent request handling
- Transaction rollback scenarios
- Persistence with TDB2

**Performance Tests:**
- Query execution benchmarks
- Serialization throughput
- Concurrent load testing
- Memory usage under load

### Running Tests

```bash
# Run all tests
mvn test

# Run with test output
mvn test -Dtest=*Test

# Generate test reports
mvn surefire-report:report
open target/site/surefire-report.html

# Coverage reports
mvn jacoco:prepare-agent test jacoco:report
open target/site/jacoco/index.html
```

## Configuration

### Maven Configuration (pom.xml)

**Essential Jena Dependencies:**
```xml
<properties>
    <jena.version>5.1.0</jena.version>
    <jetty.version>12.0.8</jetty.version>
    <jackson.version>2.16.1</jackson.version>
</properties>

<dependencies>
    <!-- Jena Core -->
    <dependency>
        <groupId>org.apache.jena</groupId>
        <artifactId>jena-core</artifactId>
        <version>${jena.version}</version>
    </dependency>

    <!-- SPARQL Query Engine -->
    <dependency>
        <groupId>org.apache.jena</groupId>
        <artifactId>jena-arq</artifactId>
        <version>${jena.version}</version>
    </dependency>

    <!-- TDB2 Persistent Storage -->
    <dependency>
        <groupId>org.apache.jena</groupId>
        <artifactId>jena-tdb</artifactId>
        <version>${jena.version}</version>
    </dependency>

    <!-- Fuseki SPARQL Server -->
    <dependency>
        <groupId>org.apache.jena</groupId>
        <artifactId>jena-fuseki-main</artifactId>
        <version>${jena.version}</version>
    </dependency>

    <!-- SHACL Validation -->
    <dependency>
        <groupId>org.apache.jena</groupId>
        <artifactId>jena-shacl</artifactId>
        <version>${jena.version}</version>
    </dependency>
</dependencies>
```

**Maven Compiler Configuration (Java 21):**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.12.1</version>
    <configuration>
        <source>21</source>
        <target>21</target>
        <compilerArgs>
            <arg>--enable-preview</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

### JVM Configuration

**Production JVM Options:**
```bash
-server                    # Server mode
-Xmx4g                     # 4GB heap
-XX:+UseG1GC               # G1 garbage collector
-XX:+UseStringDeduplication # String deduplication
-Xlog:gc*                 # GC logging (Java 9+)
-XX:+HeapDumpOnOutOfMemoryError # Heap dump on OOM
-XX:HeapDumpPath=/var/log/catty-oom.hprof
```

**Development JVM Options:**
```bash
-Xmx2g                     # 2GB heap
-XX:+UseG1GC               # G1 garbage collector
-Xlog:gc*:file=gc.log      # GC logging to file
-XX:+ShowCodeDetailsInExceptionMessages # Java 14+
--enable-preview           # Enable preview features
```

## Semantic Web Best Practices

### RDF Modeling

**Use Consistent Naming:**
```java
// Use namespaces for all resources
String NS = "http://catty.example.org/cat#";
Resource category = model.createResource(NS + "mycategory");
Property hasName = model.createProperty(NS + "hasName");
```

**Prefer Literals with Datatypes:**
```java
// Typed literals
Literal integer = model.createTypedLiteral(42);
Literal date = model.createTypedLiteral("2026-01-15", XSDDatatype.XSDdate);
Literal string = model.createLiteral("Catty", "en");  // Language tag
```

**Use Reified Statements for Provenance:**
```java
Statement stmt = model.createStatement(subject, predicate, object);
ReifiedStatement reified = stmt.createReifiedStatement();
reified.addProperty(DC.date, "2026-01-15");
```

### SPARQL Querying

**Parameterized Queries:**
```java
Map<String, String> params = new HashMap<>();
params.put("category", "\"categorical\"");
String query = "SELECT ?s WHERE { ?s rdf:type ?category }";
QueryResult result = sparqlService.executeQuery(query, params);
```

**Use CONSTRUCT for RDF Generation:**
```java
String constructQuery = """
    CONSTRUCT {
        ?category rdf:type :Class .
        ?category :hasProperty ?property
    }
    WHERE {
        ?category rdf:type :CategoryClass .
        OPTIONAL { ?category :hasProperty ?property }
    }
    """;
Model resultModel = sparqlService.executeConstruct(constructQuery);
```

### Transaction Management

**Proper Transaction Pattern:**
```java
dataset.begin(ReadWrite.WRITE);
try {
    Model model = dataset.getDefaultModel();
    // ... perform updates ...
    dataset.commit();  // Commit transaction
} catch (Exception e) {
    dataset.abort();   // Rollback on error
    throw new RuntimeException(e);
} finally {
    dataset.end();     // Ensure transaction ends
}
```

## Performance Optimization

### TDB2 Database Optimization

**Configuration:**
```properties
# Located in data/tdb.cfg
# TDB2 optimized configuration
locator = "TDB2"
blocksize = 8192
# Block size for I/O operations

# Cache sizes can be adjusted based on available memory
nodeCacheSize = 100000      # Default: 50000
tripleCacheSize = 500000    # Default: 50000
```

**Bulk Loading:**
```bash
# Use tdb2.tdbloader for bulk operations
# Much faster than individual INSERT operations
tdb2.tdbloader --loc=data ontology/catty-*.jsonld
```

**Query Optimization:**
```java
// Use parameter binding for repeated queries
ParameterizedSparqlString pss = new ParameterizedSparqlString();
pss.setCommandText("SELECT * WHERE { ?s ?p ?o . FILTER(?o = ?value) }");
pss.setParam(\"value\", \"someValue\");
```

## Troubleshooting

### Common Issues

**Jena Version Conflicts:**
```xml
<!-- Ensure consistent Jena version across all modules -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.apache.jena</groupId>
            <artifactId>jena-bom</artifactId>
            <version>5.1.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**OutOfMemoryError:**
```bash
# Increase heap size
export MAVEN_OPTS="-Xmx4g"
# Or in Docker: JAVA_OPTS=-Xmx4g
```

**Port Already in Use:**
```bash
# Find process using port
lsof -i :3030
# Or
netstat -an | grep 3030

# Kill process
kill -9 <PID>

# Or use different port
java -jar catty.jar localhost 3031 /catty
```

**Format Conversion Issues:**
```bash
# Check file encoding
file -i ontology.jsonld

# Fix encoding if needed
iconv -f ISO-8859-1 -t UTF-8 ontology.jsonld > ontology-fixed.jsonld

# Validate RDF syntax
riot --validate ontology.jsonld
```

## Integration with Catty Thesis

### Loading Catty Ontologies

The server automatically loads RDF files from the `ontology/` directory:
```bash
ontology/
├── catty-categorical-schema.jsonld          # Main categorical schema
├── catty-complete-example.jsonld            # Complete examples
├── catty-thesis-shapes.shacl                # SHACL validation shapes
├── catty-shapes.ttl                        # Constraint shapes
├── curry-howard-categorical-model.jsonld    # Curry-Howard correspondence
├── logics-as-objects.jsonld               # Logic representations
├── morphism-catalog.jsonld               # Morphism definitions
├── citations.jsonld                      # Citation registry
├── citation-usage.jsonld                 # Citation usage
└── two-d-lattice-category.jsonld         # Category structure
```

### SPARQL Query Examples

**List All Logics:**
```sparql
SELECT ?logic ?name WHERE {
    ?logic rdf:type cat:Logic .
    ?logic rdfs:label ?name
}
```

**Find Morphisms Between Categories:**
```sparql
SELECT ?morphism ?from ?to WHERE {
    ?morphism rdf:type cat:Functor .
    ?morphism cat:source ?from .
    ?morphism cat:target ?to
}
```

**Validate Using SHACL:**
```bash
shacl validate --shapes ontology/catty-shapes.ttl \
               --data ontology/catty-categorical-schema.jsonld
```

## Migration from Python Semantic Web

### Key Differences

**Jena vs Python RDFLib:**
- **Performance:** Jena/TDB2 provides native storage vs memory-based
- **Concurrency:** Full ACID transactions vs limited Python threading
- **Scaling:** Handles billions of triples efficiently
- **SPARQL:** Native SPARQL 1.1 support with full optimization
- **Persistence:** Durable storage across restarts

**Migration Steps:**
1. Export Python models to JSON-LD or Turtle
2. Load into Jena using RDFDataMgr or tdb2.tdbloader
3. Translate SPARQL queries (Jena uses same SPARQL 1.1 standard)
4. Update code to use Jena APIs
5. Test thoroughly with integration tests

## Security Considerations

### Localhost-First Security

**Localhost Binding:**
```java
// FusekiServer configuration
server = FusekiServer.create()
    .add(datasetName, dataset, true)
    .loopback(true)      // Bind to localhost only
    .port(port)
    .build();

// Jetty configuration
HttpConfiguration httpConfig = new HttpConfiguration();
// Host header validation
httpConfig.addCustomizer(new ForwardedRequestCustomizer());
```

**Access Control:**
```java
// For production deployment, use Shiro authentication
server = FusekiServer.create()
    .add(datasetName, dataset, true)
    .staticFileBase("webapp")
    .jettyServerConfig()  // Custom security config
    .build();
```

## Deployment Checklist

### Development Setup
- [ ] Java 21 installed
- [ ] Maven 3.8+ installed
- [ ] Project builds: `mvn clean compile`
- [ ] Tests pass: `mvn test`
- [ ] Server starts: `mvn exec:java -Dexec.mainClass="org.catty.server.SemanticWebServer"`
- [ ] SPARQL endpoint accessible on http://localhost:3030/catty/query
- [ ] HTTP RDF server accessible on http://localhost:8080/rdf/

### Production Setup
- [ ] Docker image builds: `docker build -t catty/categorical-reasoner .`
- [ ] Server runs in Docker: `docker run -p 3030:3030 -p 8080:8080 catty/categorical-reasoner`
- [ ] Health check endpoints configured
- [ ] Logging configured (logback.xml)
- [ ] Metrics collection (Prometheus, etc.)
- [ ] Backup strategy for TDB2 data
- [ ] Monitoring for disk space, memory usage
- [ ] SSL/TLS certificates (if external access needed)

## Performance Benchmarks

### Expected Performance

**RDF Loading:**
- Small files (< 10MB): < 5 seconds
- Medium files (10-100MB): < 30 seconds
- Large files (100MB-1GB): < 5 minutes

**SPARQL Query Performance:**
- Simple SELECT (no JOINs): < 100ms
- Moderate complexity (1-2 JOINs): < 500ms
- Complex queries (3+ JOINs): < 2 seconds
- CONSTRUCT/DESCRIBE: < 1 second

**HTTP Endpoint Response Times:**
- RDF serving: < 200ms for < 1MB responses
- SPARQL query: < 500ms for typical queries
- SPARQL update: < 100ms for small updates

## Future Extensions

### Planned Features

**Enhanced Reasoning:**
- OWL reasoning integration (HermiT, Pellet)
- Custom rule-based reasoning
- RDFS inference

**Advanced Query Features:**
- Full-text search (Lucene integration)
- Geospatial queries
- Time-series RDF processing

**Replication & Distribution:**
- Dataset federation (Federation over SPARQL)
- Replication across multiple TDB2 instances
- Cloud storage integration (S3, Azure Blob)

**Management UI:**
- Web-based ontology browser
- SPARQL query editor
- Upload interface for RDF files
- Performance monitoring dashboard

## Contributing

### Code Style

- Java 21 with preview features
- Google Java Style Guide
- Maximum line length: 100 characters
- 2-space indentation
- Proper Javadoc for all public APIs

### Testing Requirements

- Unit tests for all new features
- Integration tests for HTTP endpoints
- Performance tests for data loading
- Javadoc coverage checks

### Documentation

- Update relevant AGENTS.md files
- Document new endpoints in this guide
- Add examples to ontology documentation
- Update configuration examples

## License

Apache License 2.0 - see LICENSE file for details

## Support

For issues and questions:
- Review AGENTS.md files in relevant directories
- Check operations.yaml for task definitions
- Consult QUICKSTART.md for basic setup
- Reference this guide for detailed usage

## Version History

- **v1.0.0 (2026-01-15)** - Initial semantic web development environment
  - Apache Jena 5.1.0 integration
  - SPARQL server with Fuseki
  - HTTP RDF serving with Jetty
  - Multi-format RDF serialization
  - Comprehensive Javadoc documentation
  - Complete test suite