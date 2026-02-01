# Catty

Catty is a thesis on the categorical modeling of formal logics, where logics are objects in a category with morphisms representing sequent restrictions and structural rules. The thesis includes a comprehensive audit of RDF/OWL schemas and knowledge graphs that support this category-theoretic framework.

Deployed thesis (after running the Deploy workflow): https://metavacua.github.io/CategoricalReasoner/

## Overview

Catty organizes logics as a **parametric category** where:
- **Objects**: Formal logics (LM, LK, LJ, LDJ, linear logic, etc.)
- **Morphisms**: Relationships between logics via extension, interpretation, and structural rule configuration
- **Functors**: Maps between proof systems
- **Structure**: A two-dimensional lattice (horizontal: sequent restrictions; vertical: structural rules) formalized as a categorical structure
- **Curry-Howard**: A categorical equivalence between logics (as categories), types, proofs, and programs

## Architecture Overview

Catty is a Java-centric system that consumes semantic web data from external sources and generates LaTeX thesis content with RDF metadata/provenance. The architecture is:

1. **External RDF consumption**: Semantic web ontologies and knowledge graphs are discovered and accessed via SPARQL endpoints, linked data services, and the Generalized Knowledge Graph (GGG)
2. **Jena Model loading**: Apache Jena loads external RDF into Model objects, with OntModel providing OWL 2 DL profile support
3. **OpenLlet reasoning**: OpenLlet reasoner performs consistency checking, classification, and inference over loaded ontologies
4. **TeX as primary artifact**: The thesis is the core deliverable; RDF is used only for metadata and provenance tracking extracted from TeX content

The system does **not** author local RDF schemas or instantiate ontology classes. All ontology data comes from external sources.

## Technology Stack

**Primary**: Java ecosystem
- **Apache Jena**: RDF/OWL processing and SPARQL query execution
- **OpenLlet**: OWL 2 DL reasoning (consistency checking, classification, inference)
- **JavaPoet**: Code generation for validation and transformation logic
- **JUnit**: Unit testing and validation framework
- **Maven**: Build and dependency management

**Auxiliary**: Python scripts for CI/CD orchestration
- Python scripts support the Java-primary architecture by handling auxiliary tasks
- Examples: extracting citation metadata, running validation orchestration, transformation helpers
- **Normative rule**: Do not propose custom implementations of existing Java library functionality

**Rationale**: Java libraries provide proof-theoretic guarantees (OWL 2 DL compliance), mature validation infrastructure, and avoid DIY reimplementation of complex semantic web technologies.

## Semantic Web Data Pipeline

The data flow for consuming external semantic web data:

1. **Discovery and Access**
   - SPARQL endpoints (e.g., DBPedia, Wikidata)
   - Linked data dereferencing
   - Generalized Knowledge Graph (GGG) queries
   - Static RDF/OWL files from external repositories

2. **Loading and Transformation** (Apache Jena)
   - Jena Model reads RDF/XML, Turtle, JSON-LD formats
   - OntModel with OWL 2 DL profile enables ontology-specific operations
   - Prefix management and namespace resolution
   - Conversion to Java objects via Jena's API

3. **Reasoning** (OpenLlet)
   - Consistency checking: Verify ontology is satisfiable
   - Classification: Automatically infer class hierarchy
   - Inference: Deduce implicit statements from axioms
   - Rule-based reasoning for custom constraints

4. **Code Generation and Validation** (JavaPoet + JUnit)
   - Generate Java validation code based on ontology constraints
   - Generate JUnit tests for data integrity
   - Compile and run validation as part of CI/CD

## Development Direction

**Architectural principle**: Default to Java ecosystem for all core functionality. Python is acceptable for CI/CD helper scripts only.

**Guidelines**:
- When implementing validation, transformation, or reasoning: Use Jena, OpenLlet, or JavaPoet
- When adding utility scripts: Prefer Java unless Python provides clear benefits for CI/CD integration
- Do not propose custom implementations of existing Java library functionality
- All ontology data comes from external sources; local RDF schemas are not authored

## License

This project is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**. See the `LICENSE` file for details.

## Semantic Web Technology Index (SWTI)

This project is evaluated against the **SWTI** (Nature Scientific Reports 2022) criteria. We aim for a high level of compliance in data standards, evaluation, and accessibility.

### Knowledge Processing & Evaluation

- **Thesis Validation**: LaTeX structure, citation registry, and ID uniqueness validated via Python scripts (temporary CI/CD helpers). Long-term validation will use Java (Jena SHACL support, JUnit).
- **Benchmarks**: SPARQL query performance tests run against external SPARQL endpoints and linked data sources. See `benchmarks/` for details.
- **Reasoning**: OpenLlet provides OWL 2 DL reasoning over external ontologies loaded via Jena.

### Deployment & Access

The thesis and supporting materials are deployed to GitHub Pages:
- **Thesis (HTML/PDF)**: Available at `https://metavacua.github.io/CategoricalReasoner/`
- **Benchmarks**: SPARQL query examples available at `https://metavacua.github.io/CategoricalReasoner/benchmarks/queries/`

## Project Structure

```
├── thesis/                      # LaTeX thesis source (primary artifact)
├── .catty/                      # Operational model (task/artifact system)
│   ├── operations.yaml          # Main operational model
│   ├── phases.yaml              # Dependency graph
│   └── validation/              # Validation framework
├── schema/                      # Validation schemas and constraints
│   ├── thesis-structure.schema.yaml    # LaTeX structure schema
│   ├── tex-rdf-mapping.yaml             # TeX → RDF provenance mapping
│   └── validators/                     # Python validation scripts (temporary)
├── bibliography/                # Citation registry (citations.yaml)
├── benchmarks/                  # SPARQL query performance tests
├── scripts/                     # Python utility scripts (auxiliary CI/CD)
├── tests/                       # Test suite (thesis validation, future Java tests)
└── README.md                    # This file
```

**Note**: The `ontology/` directory contains example/reference materials and is not part of the core architecture. Ontologies are consumed from external sources (SPARQL endpoints, linked data).

## Key Deliverables

### Categorical Semantic Audit

The thesis includes a comprehensive audit (Chapter 1) of:

1. **Category Theory Foundation** - RDF/OWL representations from DBPedia, Wikidata
2. **Logics as Categorical Objects** - Modeling LK, LJ, LDJ, linear logic as objects
3. **Morphism Catalog** - Sequents restriction morphisms, structural rule morphisms
4. **Two-Dimensional Lattice** - Formalized as a poset category
5. **Curry-Howard Model** - Equivalence of logic and type theory categories
6. **Reusable Ontologies** - Inventory of 11+ resources with license compatibility
7. **Integration Roadmap** - How to import and extend external resources

### External Ontology Consumption

The project consumes semantic web ontologies and knowledge graphs from external sources:

- **SPARQL Endpoints**: DBPedia, Wikidata, and other public endpoints
- **Linked Data**: Ontologies and datasets accessible via HTTP content negotiation
- **Generalized Knowledge Graph (GGG)**: Large-scale integrated knowledge graphs
- **Static Repositories**: Curated RDF/OWL files from external projects

These external sources are loaded via Jena, reasoned over with OpenLlet, and used to generate thesis content and validation code.

## Build (PDF)

Requirements: a LaTeX distribution (e.g. TeX Live) and `make`.

```sh
cd thesis
make
```

The output PDF is `thesis/main.pdf`.

## Deploy (HTML to GitHub Pages)

This repository includes a manual GitHub Actions workflow:

1. GitHub UI → **Actions**
2. Select **Deploy**
3. **Run workflow**

If this is the first deployment, ensure GitHub Pages is set to deploy from Actions:
**Settings → Pages → Build and deployment → Source: GitHub Actions**.

The workflow builds `main.pdf`, converts the expanded LaTeX source to `index.html` using Pandoc, and deploys the resulting site to GitHub Pages.

## Operational Model

This project includes a **formal operational model** that defines the complete task/artifact system for thesis generation and code generation. Explore `.catty/` directory for:

- **Task specifications**: Unambiguous, executable instructions for creating thesis artifacts
- **Dependency graph**: Complete task sequencing and parallelization opportunities
- **Validation framework**: Automated validation with testable acceptance criteria
- **Comprehensive documentation**: README, quick start guide, task execution guide, dependency graphs

The operational model describes thesis generation and external RDF consumption, not local ontology authoring.

See `.catty/README.md` for complete documentation.
