# AGENTS.md - Catty Thesis Repository

## Scope
This repository implements the Catty thesis: categorical foundations for logics and their morphisms. Agents must generate LaTeX thesis content, RDF/OWL ontologies (JSON-LD/Turtle), and validation artifacts following formal constraints in `.catty/operations.yaml`.

## Core Constraints
- **Formats**: Read/write `*.md`, `*.tex`, `*.jsonld`, `*.ttl`, `*.yaml`, `*.py`. Create directories only when specified.
- **Languages**: JSON-LD, Turtle RDF, RDFS, OWL, SHACL, LaTeX, Python ≥3.8.
- **Citations**: Use ONLY keys from `bibliography/citations.yaml`. Forbidden: invent new citation keys.
- **IDs**: Globally unique across corpus. Patterns: `thm-*`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **Validation**: All artifacts must pass automated validation via `.catty/validation/validate.py`. Acceptance criteria are boolean tests only.
- **Execution**: Load operations from `.catty/operations.yaml`, verify dependencies via `.catty/phases.yaml`, execute task descriptions, validate outputs.
- **RDF/OWL**: Instantiate only existing classes from `ontology/catty-categorical-schema.jsonld`. Validate against `ontology/catty-shapes.ttl`.

## Validation
Run `python .catty/validation/validate.py --artifact <id>` or `--task <id>` to verify compliance. All criteria must evaluate true.

## See Also
- `.catty/AGENTS.md` - Operational model constraints
- `schema/AGENTS.md` - Citation and ID constraints
