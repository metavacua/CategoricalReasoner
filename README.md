# Catty

An algorithmic thesis exploring categorical foundations for formal logics and their morphisms.

## Abstract

Catty develops algorithms and implementations for modeling formal logics as categorical structures. The project provides computational tools for representing logics as objects in categories, with morphisms capturing sequent restrictions and structural rules. The repository delivers executable algorithms that operate on semantic web data consumed from external sources.

## About This Project

### What is an Algorithmic Thesis?

An algorithmic thesis is a software repository that embodies research contributions through executable algorithms, not static documents. The primary deliverables are working implementations, validation frameworks, and computational tools that realize theoretical concepts in code.

### What Catty Delivers

This repository provides algorithms and implementations for:

- **Categorical logic modeling**: Computational representations of formal logics (LM, LK, LJ, LDJ, linear logic) as objects in categories
- **Morphism algorithms**: Tools for defining and reasoning about relationships between logics via extension and interpretation
- **Semantic web integration**: Consumption and reasoning over external RDF/OWL ontologies and knowledge graphs
- **Validation frameworks**: Automated testing infrastructure for verifying categorical structures and consistency

The project implements a Java-centric architecture with proven semantic web technologies, supported by auxiliary Python scripts for CI/CD orchestration.

### Supporting Documentation

The `thesis/` directory contains LaTeX whitepapers that document the research and development process. These files explain theoretical foundations, design decisions, and algorithmic approaches. They are supporting documentation for the algorithmic contributions, not the primary deliverables themselves.

## Project Structure

```
├── thesis/                      # LaTeX whitepapers documenting research and development
├── benchmarks/                  # Performance tests and evaluation
├── ontology/                    # Reference materials and examples
├── schema/                      # Validation schemas and constraints
├── bibliography/                # Citation registry
├── scripts/                     # Utility scripts
├── tests/                       # Test suites
├── .catty/                      # Operational model and validation framework
└── README.md                    # This file
```

Each directory contains its own README with detailed information about that component's purpose, structure, and usage.

## Technology Stack

**Primary ecosystem: Java**
- Semantic web processing and reasoning
- Code generation for validation and transformation
- Unit testing and validation frameworks

**Auxiliary: Python**
- CI/CD orchestration and helper scripts
- Utility functions supporting the Java architecture

See individual directory READMEs for specific technology details and build instructions.

## Semantic Web Data

This project consumes semantic web data from external sources rather than authoring local RDF schemas. External ontologies and knowledge graphs are accessed via SPARQL endpoints, linked data services, and the Giant Global Graph (GGG; https://en.wikipedia.org/wiki/Giant_Global_Graph).

See the `ontology/` and `benchmarks/` directories for information about consumed data sources and integration approaches.

## Development and Contribution

For information about contributing to this project, see `CONTRIBUTING.md`.

## Operational Model

The `.catty/` directory contains a formal operational model defining tasks, artifacts, dependencies, and validation criteria. This system governs the generation of algorithms, validation code, and documentation.

See `.catty/README.md` for complete documentation.

## License

This project is licensed under the GNU Affero General Public License v3.0 (AGPL-3.0). See the `LICENSE` file for details.

## Special Files

- `AGENTS.md` - Machine-readable specifications for automated agents
- `CONTRIBUTING.md` - Contribution guidelines and workflows
- `LICENSE` - Full license text
