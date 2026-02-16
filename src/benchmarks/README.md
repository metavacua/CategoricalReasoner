# Benchmarks Directory

## Purpose

The `src/benchmarks/` directory contains SPARQL query performance tests and evaluation scripts for measuring the performance of semantic web data access and processing.

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
PREFIX wd: <http://www.wikidata.org/entity/>
PREFIX wdt: <http://www.wikidata.org/prop/direct/>

# Query description
SELECT DISTINCT ?logic ?label
WHERE {
  ?logic wdt:P31 wd:Q8078 .
  ?logic rdfs:label ?label .
  FILTER(LANG(?label) = "en")
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
python src/benchmarks/run.py

# Run specific benchmark
python src/benchmarks/run.py --query src/benchmarks/queries/category-theory.sparql

# Run against specific endpoint
python src/benchmarks/run.py --endpoint https://dbpedia.org/sparql

# Verbose output
python src/benchmarks/run.py --verbose
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
- `requests` and `rdflib` libraries
- Network access to external endpoints (for remote benchmarks)

### Execution

```bash
# Run all benchmarks against local ontology
python src/benchmarks/run.py

# Run against Wikidata
python src/benchmarks/run.py --endpoint https://query.wikidata.org/sparql --query wikidata-logics.rq

# Run against DBPedia
python src/benchmarks/run.py --endpoint https://dbpedia.org/sparql --query dbpedia-category-theory.rq
```

### Challenges and Lessons Learned

1. **User-Agent Filtering**: Public endpoints like Wikidata require a descriptive `User-Agent` header. Failing to provide one often results in 403 Forbidden or 429 Too Many Requests errors.
2. **Query Timeouts**: Complex queries against large graphs (Wikidata/DBPedia) can easily timeout. It is recommended to use `LIMIT`, efficient property paths, and to avoid large cross-joins.
3. **Format Negotiation**: When using `CONSTRUCT` queries, explicitly requesting `text/turtle` ensures the results are returned in a compact, human-readable semantic format.
4. **Data Integrity**: External data often contains noise. Validation of extracted triples against local SHACL shapes is crucial for maintaining knowledge graph quality.

## Recommendations for Semantic Web RAG

1. **Hybrid Retrieval**: Combine SPARQL for structured fact retrieval with Vector Search for unstructured text context.
2. **Graph-Aware Prompting**: Use the extracted TTL (Turtle) as context for LLMs, as its hierarchical nature is well-suited for representing relationships compared to flat JSON.
3. **Dynamic Discovery**: Implement the "Discovery Pattern" (as seen in `docs/WIKIDATA_DISCOVERY.md`) to resolve ambiguous labels to authoritative URIs before performing RAG.
4. **Agentic Validation**: Coding agents should automatically run and validate queries against endpoints during the generation process to detect and fix "SPARQL hallucinations".


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
  run: python src/benchmarks/run.py --output results.json

- name: Check performance
  run: |
    if python src/scripts/check-performance.py --threshold '10%' results.json; then
      echo "Performance check passed"
    else
      echo "Performance regression detected!"
      exit 1
    fi
```

## Technology Note

Benchmarks currently use Python for pragmatic reasons (easy SPARQL query execution, JSON output). Long-term, performance-critical benchmarking could use Java (Jena ARQ) for consistency with the primary technology stack.

## See Also

- `src/scripts/README.md` - Utility scripts and tools
- Schema README files - Understanding how data is structured
