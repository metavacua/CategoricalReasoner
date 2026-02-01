# AGENTS.md - Catty Thesis Repository

## Scope
This repository implements the Catty thesis: categorical foundations for logics and their morphisms. Agents must generate LaTeX thesis content and validation artifacts following formal constraints in `.catty/operations.yaml`. Semantic web data is consumed from external sources (SPARQL endpoints, linked data, GGG) via Jena, not authored locally.

## Core Constraints
- **Formats**: Read/write `*.md`, `*.tex`, `*.yaml`, `*.py`. Create directories only when specified.
- **Languages**: LaTeX, Python ≥3.8 (auxiliary CI/CD only), Java (primary ecosystem for validation and transformation).
- **Citations**: Use ONLY keys from `bibliography/citations.yaml`. Forbidden: invent new citation keys.
- **IDs**: Globally unique across corpus. Patterns: `thm-*`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **Validation**: All artifacts must pass automated validation via `.catty/validation/validate.py`. Acceptance criteria are boolean tests only.
- **Execution**: Load operations from `.catty/operations.yaml`, verify dependencies via `.catty/phases.yaml`, execute task descriptions, validate outputs.
- **Technology Stack**: Core validation and transformation uses Java ecosystem (Jena, OpenLlet, JavaPoet, JUnit). Python scripts are auxiliary for CI/CD orchestration only.
- **Semantic Web Data**: Consumed from external sources. Do not author local RDF schemas or instantiate ontology classes.

## Validation
Run `python .catty/validation/validate.py --artifact <id>` or `--task <id>` to verify compliance. All criteria must evaluate true.

## See Also
- `.catty/AGENTS.md` - Operational model constraints
- `schema/AGENTS.md` - Citation and ID constraints
