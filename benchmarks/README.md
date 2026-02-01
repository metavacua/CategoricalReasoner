# Benchmarks Directory

## Purpose

The `benchmarks/` directory contains SPARQL query performance tests and evaluation scripts for measuring the performance of semantic web data access and processing.

## Data Sources

Benchmarks run against external SPARQL endpoints and linked data sources:

- **DBPedia**: Large-scale knowledge graph (https://dbpedia.org/sparql)
- **Wikidata**: Structured data repository (https://query.wikidata.org/sparql)
- **Custom endpoints**: Local or project-specific SPARQL endpoints for testing
- **Static datasets**: Pre-loaded RDF datasets for reproducible testing

## Relationship to Architecture

Benchmarks relate to the overall architecture in the following ways:

1. **External RDF Consumption**: Benchmarks measure how efficiently external ontologies can be queried and loaded
2. **Thesis Validation**: Indirectly validates that external data sources used in thesis are performant
3. **Code Generation**: Performance metrics inform code generation decisions (e.g., caching strategies, query optimization)

Benchmarks do **not** validate local ontologies (which are examples only) or thesis content directly.

## Core Components

### `queries/` Directory

Contains SPARQL query files with descriptive names and optimized prefixes.

**Query Types**:
- **Category Theory Queries**: Query for category theory concepts from DBPedia/Wikidata
- **Logic-Specific Queries**: Query for formal logic definitions and properties
- **Performance Tests**: Measure query execution time against different endpoints
- **Complex Join Queries**: Test performance of multi-join patterns

**Query Format**:
```sparql
PREFIX catty: <http://catty.org/ontology/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

# Query description
SELECT DISTINCT ?logic ?label
WHERE {
  ?logic a catty:Logic ;
         rdfs:label ?label .
}
```

### `run.py`

Benchmark execution script that runs all queries without manual intervention.

**Features**:
- Measure execution time for each query
- Validate result patterns against expected outputs
- Record performance metrics for regression detection
- Generate benchmark reports

**Usage**:
```bash
# Run all benchmarks
python benchmarks/run.py

# Run specific benchmark
python benchmarks/run.py --query benchmarks/queries/category-theory.sparql

# Run against specific endpoint
python benchmarks/run.py --endpoint https://dbpedia.org/sparql

# Verbose output
python benchmarks/run.py --verbose
```

## Benchmark Categories

### 1. Query Performance

Measures execution time of SPARQL queries against different endpoints.

**Metrics**:
- Execution time (ms)
- Result count
- Query complexity (triple patterns, joins, filters)

### 2. Data Access Patterns

Tests different data access patterns to inform Jena model loading strategies.

**Patterns**:
- Direct triple lookup
- Property path queries
- FILTER operations
- ORDER BY performance
- LIMIT/OFFSET performance

### 3. Endpoint Comparison

Compares performance across different SPARQL endpoints.

**Endpoints Tested**:
- DBPedia SPARQL endpoint
- Wikidata Query Service
- Local Blazegraph instance (if available)

## Running Benchmarks

### Prerequisites

- Python ≥3.8
- Network access to external endpoints (for remote benchmarks)
- Optional: Local SPARQL endpoint (e.g., Blazegraph) for local testing

### Execution

```bash
# Run all benchmarks
python benchmarks/run.py

# Run with verbose output
python benchmarks/run.py -v

# Run specific query file
python benchmarks/run.py --query category-theory.sparql

# Save results to file
python benchmarks/run.py --output benchmark-results.json
```

### Expected Output

```
======================================================================
Running benchmarks
======================================================================

Query: category-theory.sparql
Endpoint: https://dbpedia.org/sparql
Execution time: 245ms
Results: 42 rows
Status: PASS

Query: logic-definitions.sparql
Endpoint: https://dbpedia.org/sparql
Execution time: 512ms
Results: 7 rows
Status: PASS

======================================================================
All benchmarks completed successfully
======================================================================
```

## Benchmark Validation

Benchmarks validate:

1. **Query Syntax**: All SPARQL queries must be syntactically valid
2. **Result Patterns**: Query results must match expected patterns (structure, if not exact data)
3. **Performance Thresholds**: Query execution time must be within acceptable limits
4. **Regression Detection**: Performance metrics compared against previous runs

## Integration with CI/CD

Benchmarks can be integrated into CI/CD workflows to detect performance regressions:

```yaml
# Example GitHub Actions workflow
- name: Run benchmarks
  run: python benchmarks/run.py --output results.json

- name: Check performance
  run: |
    if python scripts/check-performance.py --threshold '10%' results.json; then
      echo "Performance check passed"
    else
      echo "Performance regression detected!"
      exit 1
    fi
```

## Technology Note

Benchmarks currently use Python for pragmatic reasons (easy SPARQL query execution, JSON output). Long-term, performance-critical benchmarking could use Java (Jena ARQ) for consistency with the primary technology stack.

## See Also

- `scripts/README.md` - Utility scripts and tools
- `.catty/README.md` - Operational model and validation
- Schema README files - Understanding how data is structured
