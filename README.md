# Catty: The Logic-Witness Formalism

**A Categorical Framework for Formal Logics Grounded in Physical Realizability**

Catty presents a rigorous metamathematical framework for modeling formal logics as objects in a **two-dimensional lattice category**. This categorical structure connects proof theory, quantum mechanics, and computational semantics through the witness functor.

**Deployed Documentation**: https://metavacua.github.io/Catty/

## Overview

The logic-witness formalism organizes logics as a **lattice of lattices** where:

- **Objects**: Categorizable formal logics (LK, LJ, LDJ, Monotonic, Linear, etc.)
- **Morphisms**: Extension and interpretability relationships respecting sequent restrictions and structural rules
- **Horizontal Dimension**: Sequent restrictions (Classical ↔ Intuitionistic ↔ Dual-Intuitionistic ↔ Monotonic)
- **Vertical Dimension**: Structural rule configurations (Full Structural ↔ Affine ↔ Relevant ↔ Linear ↔ Non-Structural)
- **Witness Functor**: Maps logic-theory pairings to executable programs or physical circuits (Curry-Howard extension)
- **Physical Grounding**: Quantum-safe logics (W=0, C=0) embody no-cloning and no-erasure theorems

## License

This project is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**. See the `LICENSE` file for details.

## Project Structure

```
├── thesis/                      # LaTeX thesis source
│   ├── main.tex                # Main document
│   ├── preamble.tex            # Document preamble
│   ├── Makefile                # Build automation
│   ├── frontmatter/            # Title page, abstract, acknowledgments
│   ├── chapters/               # 6 comprehensive chapters + introduction + conclusions
│   │   ├── introduction.tex
│   │   ├── chapter2-logical-formalism.tex
│   │   ├── chapter3-theoretical-formalism.tex
│   │   ├── chapter4-witness-formalism.tex
│   │   ├── chapter5-semantic-web.tex
│   │   ├── chapter6-roadmap.tex
│   │   └── conclusions.tex
│   └── backmatter/             # Bibliography
│       └── bibliography.bib
│
├── ontology/                    # RDF/OWL schemas and knowledge graphs
│   ├── catty-categorical-schema.jsonld     # Core categorical schema
│   ├── logics-as-objects.jsonld            # Logics as categorical objects
│   ├── morphism-catalog.jsonld              # Morphisms between logics
│   ├── two-d-lattice-category.jsonld        # 2D lattice as category
│   ├── curry-howard-categorical-model.jsonld # Curry-Howard equivalence
│   ├── catty-shapes.ttl                    # SHACL validation constraints
│   ├── external-ontologies.md              # Catalog of external resources
│   ├── ontological-inventory.md            # Resource inventory
│   ├── examples/                           # Canonical logic instances
│   │   ├── classical-logic.ttl
│   │   ├── intuitionistic-logic.ttl
│   │   ├── dual-intuitionistic-logic.ttl
│   │   ├── monotonic-logic.ttl
│   │   └── linear-logic.ttl
│   └── queries/
│       └── sparql-examples.md              # SPARQL query examples
│
├── tools/                       # Validation and utility scripts
│   ├── validate-rdf.py         # RDF syntax validation
│   └── README.md               # Tool documentation
│
├── .github/
│   ├── workflows/              # CI/CD workflows
│   │   └── deploy.yml
│   └── ISSUE_TEMPLATE/         # Issue templates
│       ├── theorem.md
│       ├── chapter.md
│       ├── ontology.md
│       ├── proof.md
│       └── feature.md
│
├── index.md                     # GitHub Pages landing page
├── CONTRIBUTING.md              # Contribution guidelines
└── README.md                    # This file
```

## Key Deliverables

### White Paper: The Logic-Witness Formalism

A comprehensive white paper (6 chapters + introduction + conclusions) covering:

**Part I: Metamathematical Formalism**
1. **Chapter 2**: Logical Formalism and the Category of Categorizable Logics
2. **Chapter 3**: Theoretical Formalism (Tarski-Mostowski-Robinson Framework)
3. **Chapter 4**: Witness Formalism (Curry-Howard-Kleene-Lambek Correspondence)

**Part II: Technical Implementation**
4. **Chapter 5**: Semantic Web as Integration Interface
5. **Chapter 6**: Technical Roadmap and Future Work

**Key Contributions**:
- Two-dimensional lattice category structure (correct abstract formalism)
- Logical signatures for categorizable logics
- Physical grounding via quantum no-cloning and no-erasure theorems
- Witness functor mapping logic-theory pairings to executable programs
- Robinson arithmetic as canonical case study across all logics
- Semantic web integration with RDF/OWL ontologies

### Ontology Files

The `ontology/` directory contains:

- **Complete RDF/OWL Schema**: Category theory primitives, logic-specific classes
- **Knowledge Graph Data**: Logics with categorical properties and morphisms
- **Example Instances**: Classical, Intuitionistic, Dual-Intuitionistic, Monotonic, Linear logics
- **External Alignments**: DBpedia and Wikidata references for grounded definitions
- **SHACL Validation**: Constraints for lattice order and morphism validity
- **SPARQL Examples**: Queries for exploring the ontology

### Validation Tools

The `tools/` directory contains:

- **validate-rdf.py**: RDF syntax validation using RDFLib
- **README.md**: Tool documentation and future tool plans

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
