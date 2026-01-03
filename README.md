# Catty

Catty is a thesis on the categorical modeling of formal logics, where logics are objects in a category with morphisms representing sequent restrictions and structural rules. The thesis includes a comprehensive audit of RDF/OWL schemas and knowledge graphs that support this category-theoretic framework.

Deployed thesis (after running the Deploy workflow): https://<owner>.github.io/Catty/

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
- **RDF/OWL Access**: Available at `https://<owner>.github.io/Catty/ontology/`
- **SPARQL Benchmarks**: Available at `https://<owner>.github.io/Catty/benchmarks/queries/`

#### Local SPARQL Endpoint

You can run a local SPARQL endpoint (Blazegraph) using Docker Compose:

```sh
cd deployment
docker-compose up -d
```

Access the Blazegraph workbench at `http://localhost:9999/blazegraph/`. You can then upload the files from the `ontology/` directory to query them.

## Project Structure

```
├── thesis/                      # LaTeX thesis source
├── ontology/                    # RDF/OWL schemas and knowledge graphs
├── benchmarks/                  # S4: SPARQL benchmarks and datasets
├── deployment/                  # S9: Docker/K8s deployment configs
├── tests/                       # S6: Consistency and validation tests
├── scripts/                     # Utility scripts
├── results/                     # S4: Benchmark results
├── .github/workflows/           # CI/CD (SWTI Validation and Deploy)
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
