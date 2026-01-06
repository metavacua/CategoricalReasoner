# Agent Guidelines for Catty Thesis Development

## Overview

This repository implements the Catty thesis project, a formal investigation of categorical foundations for logics and their morphisms. All agent interactions with this codebase must follow the Model Context Protocol (MCP) specifications outlined in this file and referenced directory-specific AGENTS.md files.

## Core Agent Roles

### 1. Thesis Coding Agent
**Primary Purpose**: Execute formal task specifications to produce thesis artifacts
**Key Capabilities Required**:
- LaTeX document generation and compilation
- RDF/OWL ontology development (JSON-LD and Turtle formats)
- SHACL validation implementation
- SPARQL query writing
- Academic writing with precise mathematical notation

### 2. Validation Agent
**Primary Purpose**: Validate all artifacts against formal specifications
**Key Capabilities Required**:
- Automated artifact validation using JSON schemas and SHACL shapes
- Dependency graph analysis
- Acceptance criteria testing
- Error reporting and diagnostics

### 3. Documentation Agent
**Primary Purpose**: Maintain and extend documentation while preserving formal specifications
**Key Capabilities Required**:
- Technical writing in Markdown and LaTeX
- Understanding of semantic web standards
- Mathematical notation and formal logic concepts

## MCP-Compliant Specifications

### Agent Capabilities

#### Required Technical Capabilities
```json
{
  "file_operations": {
    "read": ["*.md", "*.tex", "*.jsonld", "*.ttl", "*.yaml"],
    "write": ["*.md", "*.tex", "*.jsonld", "*.ttl", "*.yaml", "*.py"],
    "create_dirs": true,
    "delete": false
  },
  "compilation": {
    "pdflatex": true,
    "biber": true,
    "python_validation": true
  },
  "rdf_processing": {
    "jsonld": true,
    "turtle": true,
    "shacl_validation": true,
    "sparql_queries": true
  }
}
```

#### Required Knowledge Domains
- **Category Theory**: Functors, natural transformations, categorical constructions
- **Mathematical Logic**: Classical, intuitionistic, and substructural logics
- **Semantic Web Technologies**: RDF, OWL, SHACL, SPARQL
- **Academic Writing**: LaTeX, mathematical notation, thesis structure

### Agent Responsibilities

#### Task Execution Protocol
1. **Read operational specifications** from `.catty/operations.yaml`
2. **Verify dependencies** using `.catty/phases.yaml`
3. **Execute tasks** following operational descriptions
4. **Validate outputs** using automated validation scripts
5. **Report results** with diagnostic information

#### Quality Assurance
- All artifacts must pass automated validation
- No subjective quality assessments allowed
- All outputs must meet testable acceptance criteria
- Dependencies must be resolved before task execution

### Error Handling

#### Validation Failures
```
PROTOCOL: When validation fails
1. Read specific error message from validation script
2. Identify exact artifact and failure point
3. Apply fixes according to operational specifications
4. Re-run validation until all criteria pass
5. Report final validation status
```

#### Missing Dependencies
```
PROTOCOL: When dependencies are missing
1. Check depends_on list in operations.yaml
2. Find task that produces missing artifact
3. Execute prerequisite task(s) first
4. Verify dependency resolution
5. Proceed with original task
```

## Directory-Specific Agent Guidelines

### [.catty/AGENTS.md](./.catty/AGENTS.md)
Detailed operational model for task execution and artifact validation

### [schema/AGENTS.md](./schema/AGENTS.md)
LLM constraints and citation requirements for thesis generation

### [thesis/AGENTS.md](./thesis/AGENTS.md)
LaTeX structure and compilation guidelines

### [ontology/AGENTS.md](./ontology/AGENTS.md)
RDF/OWL development and validation procedures

### [bibliography/AGENTS.md](./bibliography/AGENTS.md)
Citation management and registry protocols

### [scripts/AGENTS.md](./scripts/AGENTS.md)
Script development and utility guidelines

### [benchmarks/AGENTS.md](./benchmarks/AGENTS.md)
SPARQL query development and evaluation procedures

### [tests/AGENTS.md](./tests/AGENTS.md)
Testing framework and validation procedures

## Key Operational Files

### Primary Specifications
- **`.catty/operations.yaml`**: Complete artifact and task registry
- **`.catty/phases.yaml`**: Dependency graph and execution order
- **`.catty/validation/`**: Automated validation framework

### Validation Framework
- **`.catty/validation/validate.py`**: Unified validation script
- **`schema/thesis-structure.schema.yaml`**: LaTeX structure schema
- **`ontology/*-shapes.shacl`**: RDF validation shapes

### Citation Management
- **`bibliography/citations.yaml`**: Master citation registry
- **`thesis/macros/citations.tex`**: Citation macros
- **`schema/LLM_CONSTRAINTS.md`**: Citation constraints

## Execution Environment

### Required Tools
```bash
# Python packages
pip install pyyaml rdflib pyshacl

# LaTeX tools
pdflatex
biber
```

### Repository Structure
```
catty-thesis/
├── .catty/                 # Operational model (MCP specifications)
├── bibliography/           # Citation registry
├── ontology/               # RDF/OWL ontologies
├── schema/                 # Validation schemas and constraints
├── thesis/                 # LaTeX thesis source
└── scripts/                # Utility scripts
```

## Validation Protocol

### Automated Validation Steps
1. **Load specifications** from `.catty/operations.yaml`
2. **Execute task** according to operational description
3. **Run validation** using appropriate validation script
4. **Verify acceptance criteria** (boolean tests)
5. **Report results** with diagnostic information

### Validation Success Criteria
- All files exist at specified paths
- All files parse without errors
- All required content is present
- All formal constraints are satisfied
- All acceptance criteria evaluate to true

## MCP Integration

### Context Protocol
All agent interactions must:
1. **Initialize context** by reading relevant AGENTS.md files
2. **Load specifications** from .catty/ directory
3. **Execute tasks** following operational protocols
4. **Validate outputs** using automated frameworks
5. **Report completion** with validation status

### Context Switching
When working in different directories:
- Read the directory-specific AGENTS.md file
- Load relevant operational specifications
- Apply domain-specific constraints
- Follow validation protocols

## Security and Safety

### Content Safety
- Only create artifacts specified in operations.yaml
- Follow citation registry protocols exactly
- Do not modify validation frameworks
- Preserve existing semantic web standards compliance

### Repository Integrity
- Maintain formal specification compliance
- Preserve dependency relationships
- Do not break validation chains
- Follow version control protocols

## Emergency Procedures

### Unresolved Issues
If encountering situations not covered by specifications:

1. **STOP** - Do not proceed without guidance
2. **DOCUMENT** - Clearly describe the issue and context
3. **REFER** - Point to relevant specifications or constraints
4. **REQUEST** - Ask for clarification or new specifications

### Critical Failure Protocol
On validation failure or dependency issues:
1. Record specific error messages
2. Identify root cause in operational specifications
3. Apply corrective actions systematically
4. Re-validate until all criteria pass
5. Report completion with validation status

## Contact and Support

For questions about agent protocols:
- Review directory-specific AGENTS.md files
- Consult operational specifications in .catty/
- Reference validation frameworks and acceptance criteria
- Follow emergency procedures for unresolved issues

---

**Version**: 1.0  
**Compliance**: Model Context Protocol (MCP)  
**Last Updated**: 2025-01-06