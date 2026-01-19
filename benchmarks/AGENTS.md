# Agent Guidelines for Benchmarks and Evaluation

## Overview

The `benchmarks/` directory contains evaluation scripts and SPARQL queries for testing and benchmarking the Catty ontology. This AGENTS.md provides MCP-compliant specifications for agents developing and executing benchmark tests.

## Agent Role Definition

### Benchmark Development Agent
**Primary Purpose**: Develop and maintain SPARQL queries and evaluation scripts for ontology testing

**Key Capabilities Required**:
- SPARQL query development and optimization
- Performance testing and benchmarking
- Automated evaluation script creation
- Result analysis and reporting
- Integration with validation frameworks

## MCP-Compliant Benchmark Specifications

### Required Agent Capabilities

#### SPARQL Query Development
```json
{
  "sparql_operations": {
    "query_types": ["SELECT", "CONSTRUCT", "ASK", "DESCRIBE"],
    "optimization": true,
    "performance_testing": true,
    "result_validation": true
  },
  "benchmark_framework": {
    "automated_execution": true,
    "performance_metrics": true,
    "result_comparison": true,
    "reporting": true
  }
}
```

### Directory Structure

```
benchmarks/
├── run.py                    # Main benchmark execution script
├── queries/                  # SPARQL query files
└── results/                  # Benchmark results and reports
```

### Benchmark Development Protocols

#### SPARQL Query Standards
- **File naming**: Descriptive names with `.rq` extension
- **Query structure**: Well-documented with comments
- **Performance optimization**: Use appropriate prefixes and indexes
- **Result validation**: Verify expected output patterns

#### Evaluation Script Standards
- **Automated execution**: Scripts run without manual intervention
- **Result comparison**: Compare against expected results
- **Performance metrics**: Measure query execution time and complexity
- **Error reporting**: Detailed failure analysis and diagnostics

### Quality Assurance

#### Query Validation
- [ ] SPARQL syntax is correct
- [ ] Queries return expected result patterns
- [ ] Performance is acceptable for production use
- [ ] Results are validated against known correct outputs
- [ ] Documentation explains query purpose and expected results

---

**Version**: 1.0  
**Compliance**: Model Context Protocol (MCP)  
**Dependencies**: ontology/, scripts/  
**Last Updated**: 2025-01-06
