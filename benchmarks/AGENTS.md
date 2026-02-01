# AGENTS.md - Benchmarks and Evaluation

## Scope
The `benchmarks/` directory contains SPARQL queries and evaluation scripts for ontology testing and performance measurement.

## Core Constraints
- **Queries**: SPARQL files (`.sparql`) with descriptive names, optimized prefixes, and documented expected results.
- **Execution**: `run.py` executes all queries without manual intervention. Scripts measure execution time and validate result patterns.
- **Validation**: Query results compared against expected outputs. Performance metrics recorded for regression detection.
- **Integration**: Benchmarks use ontology files from `ontology/` and validate against SHACL constraints.

## Validation
Run `python benchmarks/run.py` to execute all benchmarks. Verify query syntax, expected results, and performance thresholds.

## See Also
- `ontology/AGENTS.md` - Ontology structure and namespaces
