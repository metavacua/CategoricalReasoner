# AGENTS.md - Catty Thesis Repository

## Scope
This repository implements the Catty thesis: categorical foundations for logics and their morphisms. Agents generate LaTeX thesis content and computational artifacts. Semantic web data is consumed from external sources (SPARQL endpoints, linked data, GGG) via Jena, not authored locally.

## Core Constraints
- **Formats**: Read/write `*.md`, `*.tex`, `*.yaml`, `*.py`. Create directories only when specified.
- **Languages**: LaTeX, Python ≥3.8 (auxiliary CI/CD only), Java (primary ecosystem for validation and transformation).
- **Reports**: Reports must be returned as `.tex` files (multi-part for non-trivial) or semantic HTML. `SEMANTIC_WEB_RAG_REPORT.md` is strictly forbidden and must be converted to TeX.
- **Citations**: Use ONLY keys from `docs/dissertation/bibliography/citations.yaml`. Citation management (adding verified citations) is a normal PR activity. Forbidden: inventing citation keys without verification.
- **IDs**: Globally unique across corpus. Patterns: `thm-*`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **Validation**: Artifacts should pass validation. Acceptance criteria are boolean tests where applicable.
- **Technology Stack**: Core validation and transformation uses Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit). Python scripts are auxiliary for CI/CD orchestration only.
- **Semantic Web Data**: Consumed from external sources. Do not author local RDF schemas or instantiate ontology classes.
- **Domain Restriction**: Do not use `http://catty.org/`. The only associated webpage is the MetaVacua GitHub repository (`https://github.com/metavacua/CategoricalReasoner`). Any script or artifact using `catty.org` is invalid.
- **SPARQL Execution**: All documented queries must be actually run against external endpoints. Evidence must be returned as valid TTL. No faking or internal generation of results.
- **SPARQL Syntax**: SPARQL queries must NOT be wrapped in LaTeX environments (like `lstlisting`) when being processed or saved for execution.
- **Query Quality**: Well-formed queries must return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures.
- **Extraction Protocol**: Follow the discovery and verification patterns for external QIDs and URIs. Document all difficulties and issues encountered during extraction.

## Citation Management

Adding new citations to `docs/dissertation/bibliography/citations.yaml` is permitted and encouraged when:
1. The citation is verified via Crossref, Wikidata, or other authoritative source
2. The DOI, URL, or other identifier is validated
3. The author name, title, and year are confirmed accurate

Citation management is a normal repository maintenance activity.

## Directory Structure

```
├── docs/dissertation/          # LaTeX thesis (correct location)
│   ├── bibliography/           # Citation registry (citations.yaml)
│   ├── chapters/               # Thesis chapters
│   └── ...
├── src/ontology/               # Reference materials only (no local authorship)
├── src/schema/                 # Validation schemas
├── src/benchmarks/             # Performance tests
├── src/scripts/                # Utility scripts
├── src/tests/                  # Test suites
├── .catty/                     # DEPRECATED (was operational model)
└── README.md                   # This file
```

**Note**: The `thesis/` directory is deprecated. Use `docs/dissertation/` instead.

## See Also
- `docs/dissertation/bibliography/citations.yaml` - Verified citation registry
- `docs/external-ontologies.md` - External semantic web data sources
- `src/ontology/AGENTS.md` - Ontology directory constraints
