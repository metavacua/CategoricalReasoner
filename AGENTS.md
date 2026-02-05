# AGENTS.md - Catty Thesis Repository

## Scope
This repository implements the Catty thesis: categorical foundations for logics and their morphisms. Agents must generate LaTeX thesis content and validation artifacts following formal constraints in `.catty/operations.yaml`. Semantic web data is consumed from external sources (SPARQL endpoints, linked data, GGG) via Jena, not authored locally.

## Core Constraints
- **Formats**: Read/write `*.md`, `*.tex`, `*.yaml`, `*.py`. Create directories only when specified.
- **Languages**: LaTeX, Python ≥3.8 (auxiliary CI/CD only), Java (primary ecosystem for validation and transformation).
- **Reports**: Reports must be returned as `.tex` files (multi-part for non-trivial) or semantic HTML. `SEMANTIC_WEB_RAG_REPORT.md` is strictly forbidden and must be converted to TeX.
- **Citations**: Use ONLY keys from `docs/dissertation/bibliography/citations.yaml`. Forbidden: invent new citation keys.
- **IDs**: Globally unique across corpus. Patterns: `thm-*`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **Validation**: All artifacts must pass automated validation via `.catty/validation/validate.py`. Acceptance criteria are boolean tests only.
- **Execution**: Load operations from `.catty/operations.yaml`, verify dependencies via `.catty/phases.yaml`, execute task descriptions, validate outputs.
- **Technology Stack**: Core validation and transformation uses Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit). Python scripts are auxiliary for CI/CD orchestration only.
- **Semantic Web Data**: Consumed from external sources. Do not author local RDF schemas or instantiate ontology classes.
- **Domain Restriction**: Do not use `http://catty.org/`. The only associated webpage is the MetaVacua GitHub repository (`https://github.com/metavacua/CategoricalReasoner`). Any script or artifact using `catty.org` is invalid.
- **SPARQL Execution**: All documented queries must be actually ran against external endpoints. Evidence must be returned as valid TTL. No faking or internal generation of results.
- **SPARQL Syntax**: SPARQL queries must NOT be wrapped in LaTeX environments (like `lstlisting`) when being processed or saved for execution.
- **Query Quality**: Well-formed queries must return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures.
- **Extraction Protocol**: Follow the discovery and verification patterns for external QIDs and URIs. Document all difficulties and issues encountered during extraction.

## Validation
Run `python .catty/validation/validate.py --artifact <id>` or `--task <id>` to verify compliance. All criteria must evaluate true.

## See Also
- `.catty/AGENTS.md` - Operational model constraints
- `src/schema/AGENTS.md` - Citation and ID constraints
