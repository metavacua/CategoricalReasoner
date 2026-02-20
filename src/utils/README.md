# SPARQL Categorical Research Tool

## Overview

This utility queries Wikidata, DBpedia, and other SPARQL endpoints for categorical definitions, theorems, and axioms related to structural rules in logic.

## Features

- **Multi-endpoint querying**: Wikidata, DBpedia, FactGrid, ISIDORE
- **Rate limiting**: Respects endpoint policies with configurable delays
- **User-Agent compliance**: Follows Wikidata User Agent Policy
- **Data consistency verification**: Cross-references sources for validation
- **Export to TeX**: Generates LaTeX fragments for monograph integration

## Usage

### Basic Research Session

```bash
python3 sparql_categorical_research.py
```

This will:
1. Query all configured endpoints
2. Verify data consistency
3. Save results to `categorical_research_results.json`
4. Export TeX fragment to `research_import.tex`

### Export Existing Results to TeX

```bash
python3 sparql_categorical_research.py --export-tex
```

### As Module

```python
from sparql_categorical_research import SPARQLClient, CategoricalResearchQueries

client = SPARQLClient(rate_limit_delay=1.0)

# Query single endpoint
result = client.query(
    'wikidata',
    CategoricalResearchQueries.CATEGORY_THEORY_CONCEPTS,
    'category_theory'
)

# Query all endpoints
results = client.query_all_endpoints(
    CategoricalResearchQueries.LOGIC_THEOREMS,
    'logic_theorems'
)
```

## SPARQL Endpoints

### Primary Endpoints

| Endpoint | URL | Rate Limit |
|----------|-----|------------|
| Wikidata | https://query.wikidata.org/sparql | 1 req/sec |
| DBpedia | https://dbpedia.org/sparql | 0.5 req/sec |

### Secondary Endpoints (from Wikidata graph)

| Endpoint | URL | Rate Limit |
|----------|-----|------------|
| FactGrid | https://database.factgrid.de/sparql | 1 req/sec |
| ISIDORE | https://isidore.science/sparql | 1 req/sec |

## User Agent Policy

The tool implements the [Wikidata User Agent Policy](https://meta.wikimedia.org/wiki/User-Agent_policy):

```
CategoricalReasoner/1.0 (https://github.com/metavacua/CategoricalReasoner; research@metavacua.org) sparql-research/1.0
```

## Query Categories

1. **Category Theory Concepts** - Foundational categorical definitions
2. **Logic Theorems** - Theorems related to logic and proof theory
3. **Structural Rules** - Weakening, Contraction, Exchange
4. **Formal Logic** - Formal logic concepts and systems
5. **Proof Theory** - Sequent calculus, natural deduction

## Data Consistency

The tool verifies consistency by:
- Checking concept presence across multiple sources
- Flagging concepts with incomplete coverage
- Reporting source coverage statistics

## Output Format

### JSON Results

```json
{
  "endpoint": "wikidata",
  "query_name": "category_theory",
  "data": [...],
  "bindings": ["concept", "conceptLabel", "description"],
  "timestamp": 1708456789.123,
  "source_url": "https://query.wikidata.org/sparql"
}
```

### TeX Fragment

Auto-generated LaTeX section with itemized references suitable for inclusion in the monograph.

## Integration with Monograph

1. Run research session to generate `research_import.tex`
2. Include in monograph with `\input{research_import}`
3. Verify and curate results before final publication

## Rate Limiting

The tool enforces rate limits to be respectful to endpoints:
- Wikidata: 1 second between requests
- DBpedia: 0.5 seconds between requests
- Other endpoints: 1 second between requests

## Error Handling

- HTTP errors are logged and skipped
- URL errors trigger retry with exponential backoff
- Missing data is handled gracefully
- SSL verification can be configured per endpoint

## Dependencies

- Python 3.8+
- Standard library only (urllib, json, ssl, time, logging)

## See Also

- `docs/structural-rules/AGENTS.md` - Monograph structure
- [Wikidata Query Service](https://query.wikidata.org/)
- [DBpedia SPARQL Endpoint](https://dbpedia.org/sparql)
