# AGENTS.md - Benchmarks and Evaluation

## Scope
The `src/benchmarks/` directory contains SPARQL queries and evaluation scripts for measuring semantic web data access performance against external sources.

## Core Constraints
- **Queries**: SPARQL files (`.sparql`) with descriptive names, optimized prefixes, and documented expected results.
- **Execution**: `run.py` executes all queries without manual intervention. Scripts measure execution time and validate result patterns.
- **Validation**: Query results compared against expected outputs. Performance metrics recorded for regression detection.
- **Data Sources**: Benchmarks run against external SPARQL endpoints (DBPedia, Wikidata) and static datasets, not local ontology files (which are examples only).

## Validation
Run `python src/benchmarks/run.py` to execute all benchmarks. Verify query syntax, expected results, and performance thresholds.

## See Also
- `src/scripts/AGENTS.md` - Script development guidelines
- `.catty/AGENTS.md` - Validation framework and acceptance criteria
