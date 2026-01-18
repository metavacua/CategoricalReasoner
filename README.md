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

## License

This project is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**. See the `LICENSE` file for details.

## Semantic Web Technology Index (SWTI)

This project is evaluated against the **SWTI** (Nature Scientific Reports 2022) criteria. We aim for a high level of compliance in data standards, evaluation, and accessibility.

### Knowledge Processing & Evaluation

- **Validation**: Ontologies are validated using RDFS and SHACL (see `scripts/validate_rdf.py` and `tests/test_consistency.py`).
- **Benchmarks**: Reproducible SPARQL benchmarks are available in `benchmarks/queries/` and can be run with `benchmarks/run.py`.
- **Reasoning**: We use SHACL for consistency checking and categorical transformations as a multi-step processing workflow.

### Deployment & Access

The ontology is deployed along with the thesis:
- **RDF/OWL Access**: Available at `https://metavacua.github.io/CategoricalReasoner/ontology/`
- **SPARQL Benchmarks**: Available at `https://metavacua.github.io/CategoricalReasoner/benchmarks/queries/`

#### Local SPARQL Endpoint (No External Services)

A self-contained Java + Jena localhost server is provided under `java/`.
It loads all registered ontologies from `.catty/iri-config.yaml` into an in-memory dataset
and exposes a small SPARQL workbench UI.

```sh
cd java
mvn test
mvn exec:java
```

Then open:

- UI: `http://localhost:8080/`
- API:
  - `GET /api/ontologies`
  - `POST /api/query` (JSON body: `{ "query": "...", "format": "turtle|jsonld|rdfxml" }`)
  - `GET /api/graph?format=turtle|jsonld|rdfxml`
  - `POST /api/load` (post RDF; JSON-LD is checked for IRI safety)
  - `POST /api/rebind` (JSON body: `{ "target": "production|localhost", "content": "..." }`)

(Older Docker-based Blazegraph tooling is still available under `deployment/`, but is no longer required for local development.)

## Project Structure

```
├── thesis/                      # LaTeX thesis source
├── ontology/                    # RDF/OWL schemas and knowledge graphs
│   ├── catty-categorical-schema.jsonld     # Core categorical schema
│   ├── logics-as-objects.jsonld            # Logics as categorical objects
│   ├── morphism-catalog.jsonld              # Morphisms between logics
│   ├── two-d-lattice-category.jsonld        # 2D lattice as category
│   ├── curry-howard-categorical-model.jsonld # Curry-Howard equivalence
│   ├── catty-complete-example.jsonld         # Complete working example
│   ├── catty-shapes.ttl                    # SHACL validation constraints
│   ├── ontological-inventory.md            # Resource inventory
│   ├── README.md                           # Ontology documentation
│   └── queries/
│       └── sparql-examples.md              # SPARQL query examples
│
├── .catty/                      # Operational model (task/artifact system)
│   ├── operations.yaml          # Main operational model
│   ├── phases.yaml              # Dependency graph
│   ├── validation/              # Validation framework
│   └── *.md                     # Documentation
│
├── scripts/                     # Utility scripts
├── .github/workflows/           # CI/CD workflows
├── OPERATIONS_MODEL.md          # Operational model overview
└── README.md                    # This file
```

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

### Ontology Files

The `ontology/` directory contains:

- **Complete RDF/OWL Schema**: Category theory primitives, logic-specific classes
- **Knowledge Graph Data**: 10+ logics with categorical properties and morphisms
- **SHACL Validation**: Constraints for lattice order and morphism validity
- **SPARQL Examples**: 15+ queries for exploring the ontology

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

This project includes a **formal operational model** that defines the complete task/artifact system. See `OPERATIONS_MODEL.md` for an overview, or explore `.catty/` directory for:

- **Task specifications**: Unambiguous, executable instructions for creating all project artifacts
- **Dependency graph**: Complete task sequencing and parallelization opportunities
- **Validation framework**: Automated validation with SHACL shapes and testable acceptance criteria
- **Comprehensive documentation**: README, quick start guide, task execution guide, dependency graphs

**Quick validation:**
```sh
python .catty/validation/validate.py --artifact catty-categorical-schema
```

See `.catty/README.md` for complete documentation.
