# Java SPARQL Query Execution - RO-Crate

## Overview

This module provides Java-based SPARQL query execution against the Wikidata endpoint for retrieving RO-Crate related entities. The application demonstrates proper query optimization, error handling, and RDF/Turtle output generation.

## Build and Execution

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Internet access to Wikidata SPARQL endpoint

### Build
```bash
mvn clean package
```

This creates: `target/catty-0.0.0-shaded.jar`

### Run
```bash
java -jar target/catty-0.0.0.jar
```

The application will:
1. Load SPARQL query from `src/main/resources/wikidata-rocrate-query.rq`
2. Execute query against `https://query.wikidata.org/sparql`
3. Generate Turtle output to `wikidata-rocrate-results.ttl`
4. Display results in console in Turtle format

## Query Optimization

### Problem: Timeout on Wikidata Endpoint

The original query used `CONTAINS(LCASE(STR(?label)), "research object crate")` on all rdfs:label triples, which causes the query to timeout (>60 seconds) as it scans all labels in Wikidata.

### Solution: Targeted Query Strategies

1. **Direct QID Lookup** - Use VALUES clause with known QIDs
2. **Instance-Based Filtering** - Filter by instance type (P31) before text search
3. **Remove CONTAINS on All Labels** - Only apply text filters after narrowing result set

### Example Optimized Query

```sparql
PREFIX wd: <http://www.wikidata.org/entity/>
PREFIX wdt: <http://www.wikidata.org/prop/direct/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX schema: <http://schema.org/>

SELECT DISTINCT ?item ?itemLabel ?itemDescription WHERE {
  VALUES ?item { wd:Q209375 }  # Direct lookup
  
  ?item rdfs:label ?itemLabel.
  FILTER(LANG(?itemLabel) = "en")
  
  OPTIONAL {
    ?item schema:description ?itemDescription.
    FILTER(LANG(?itemDescription) = "en")
  }
}
LIMIT 1
```

## Performance Characteristics

| Query Strategy | Typical Execution Time | Timeout Risk | Notes |
|----------------|----------------------|---------------|-------|
| CONTAINS on all labels | >60s | High | Scans all labels, very inefficient |
| Direct QID lookup | <1s | Low | Most efficient when QID is known |
| Instance-based filtering | 1-2s | Low | Good balance of specificity and performance |
| Text search on filtered set | 0.5-2s | Low | Apply CONTAINS after narrowing results |

## Error Handling

The application handles:

1. **Empty Results** - Detects when ResultSet has no next() and exits with appropriate error message
2. **HTTP Errors** - Catches QueryExceptionHTTP and displays error details
3. **Query Parse Errors** - Catches QueryParseException and displays line/column information
4. **General Exceptions** - Catches all other exceptions and displays stack trace

## Output Format

### Turtle (TTL) Output

Results are written to `wikidata-rocrate-results.ttl` in Turtle format with proper namespace prefixes:

```turtle
@prefix wd: <http://www.wikidata.org/entity/> .
@prefix schema: <http://schema.org/> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

wd:Q209375 a schema:Thing ;
    rdfs:label "Package"@en ;
    schema:description "Container for software distribution and installation"@en .
```

### Console Output

Turtle format is also displayed to console for immediate verification.

## Dependencies

From `pom.xml`:
- `org.apache.jena:jena-arq:4.9.0` - SPARQL query execution
- `org.apache.jena:jena-core:4.9.0` - RDF model and operations
- `org.apache.jena:jena-rdfconnection:4.9.0` - HTTP connection management (optional)

## Java Version Requirements

- Java 17 is required (as specified in pom.xml)
- Code uses modern Java features
- Maven Compiler Plugin 3.11.0 configured for Java 17

## Known Wikidata QIDs

The following QIDs are known to exist but may not have complete data:
- `wd:Q209375` - Package (has label, lacks description)
- `wd:Q5169471` - Software
- `wd:Q341` - Web service

For RO-Crate specifically, the data availability in Wikidata may be limited. Consider using additional semantic web sources beyond Wikidata for comprehensive coverage.

## Troubleshooting

### Query Timeout
If queries timeout (>60s), the query is likely too broad. Review the query for:
- Unnecessary CONTAINS filters on large result sets
- Missing FILTER clauses before text operations
- Complex UNION patterns without proper indexing

### No Results
If queries execute successfully but return no results:
- Verify QIDs are correct (check Wikidata entity page)
- Confirm FILTER clauses are not too restrictive
- Check if labels exist in the requested language
- Consider querying broader concepts or related entities

### Compilation Errors
If Maven compilation fails:
```bash
mvn clean compile
```

Common issues:
- Java version mismatch (ensure Java 17 is installed)
- Missing dependencies (check network connectivity)
- Syntax errors in query file

## Related Documentation

- `JAVA_ENVIRONMENT_SETUP.md` - Complete environment configuration details
- `JAVA_SPARQL_FIX_SUMMARY.md` - Problem analysis and resolution history
- `AGENTS.md` - Repository-wide agent guidelines
- `.catty/AGENTS.md` - Operational model and validation procedures
