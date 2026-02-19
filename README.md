# Catty

An algorithmic thesis exploring categorical foundations for formal logics and their morphisms.

## Abstract

Catty develops algorithms and implementations for modeling formal logics as categorical structures. The project provides computational tools for representing logics as objects in categories, with morphisms capturing sequent restrictions and structural rules. The repository delivers executable algorithms that operate on semantic web data consumed from external sources.

## About This Project

### What is an Algorithmic Thesis?

An algorithmic thesis is a software repository that embodies research contributions through executable algorithms, not static documents. The primary deliverables are working implementations and computational tools that realize theoretical concepts in code; Java and SPARQL are the validation framework through well-formed executable code and queries; compilation to Java provides adequate compile time guarentees without DIY ad-hoc validation infrastructure; well-formed SPARQL queries provide the operational and computational semantics that can be checked through the SPARQL to Java records construction via the Java compiler.

Catty is not under any instutition, is not homework or any such instrument, is not subject to a thesis defense committee, or similar organizational commitments; it is a thesis in the sense of being held to formal standards for dissertation work at the level of the formation and operation of the instrument itself with voluntarily adopted academic standards for citations and scholarship. It is under the auspice of the Invisible College; the Invisible College is contemporaneously a socialogical concept related to the informal conduct of scholarship outside of established institutions, and it has direct roots into the common practice of science in the 19th and 20th century; it is in the tradition of Rutherford and Tesla.

The adoption of the structure of "algorithmic thesis" is not a dilution of the thesis but a deliberate restriction on it. Unlike a typical academic thesis that would be held to grading standards and the internal politics of an arbitrary tenured committee, this thesis must conform to academic and industrial standards, and the governing bodies that it must survive are the International standards bodies, private peers and entities, interdisciplinary communities, and the web of academic institutions that may or may not elect to participate in its development or judgement. If and when a committee or committees are formed to judge it then they too will be beholden to the highest intersectional standards, norms, and canonical methods of judgement.

### What Catty Delivers

This repository provides algorithms and implementations for:

- **Categorical logic modeling**: Computational representations of formal logics (LM, LK, LJ, LDJ, linear logic) as objects in categories
- **Morphism algorithms**: Tools for defining and reasoning about relationships between logics via extension and interpretation
- **Semantic web integration**: Consumption and reasoning over external RDF/OWL ontologies and knowledge graphs

The project implements a Java-centric architecture with proven semantic web technologies, supported by auxiliary Python scripts for CI/CD orchestration.

### Supporting Documentation

The `docs/dissertation/` directory contains LaTeX source for the thesis and supporting documentation. These files explain theoretical foundations, design decisions, and algorithmic approaches. They are supporting documentation for the algorithmic contributions, not the primary deliverables themselves.

## Project Structure

```
├── docs/
│   ├── dissertation/         # LaTeX thesis and documentation
│   │   ├── chapters/              # Thesis chapter files
│   │   ├── architecture/          # Architecture documentation (TeX)
│   │   ├── bibliography/          # Citation registry
│   │   └── macros/               # LaTeX macros
│   └── structural-rules/     # Monograph on Structural Rules
│       ├── Makefile            # Build instructions
│       ├── README.md           # Project overview
│       ├── main.tex            # Main document
│       ├── preamble.tex        # LaTeX preamble
│       └── parts/              # Individual chapters/sections
│           ├── part-contraction.tex
│           ├── part-exchange.tex
│           └── part-weakening.tex
├── src/
│   ├── benchmarks/            # SPARQL queries and evaluation
│   ├── schema/                # Validation schemas and constraints
│   ├── scripts/               # Utility Python scripts
│   └── tests/                # Test suites
├── AGENTS.md                 # Agent specifications
└── README.md                 # This file
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

See the `src/benchmarks/` directory for information about consumed data sources and integration approaches.

## Development and Contribution

For information about contributing to this project, see `CONTRIBUTING.md`.

## License

This project is licensed under the GNU Affero General Public License v3.0 (AGPL-3.0). See the `LICENSE` file for details.

## Special Files

- `AGENTS.md` - Machine-readable specifications for automated agents
- `CONTRIBUTING.md` - Contribution guidelines and workflows
- `LICENSE` - Full license text
