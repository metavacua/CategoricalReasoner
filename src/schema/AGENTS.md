# AGENTS.md - Schema and Constraints

## Scope
The `src/schema/` directory contains validation schemas, LLM constraints, and TeX → RDF provenance metadata mappings for thesis generation.

## Core Constraints
- **Citations**: Use ONLY keys from `docs/dissertation/bibliography/citations.yaml`. Macros: `\cite{}`, `\citepage{}`, `\citefigure{}`, `\definedfrom{}`, `\provedfrom{}`. Forbidden: invent new keys or use unregistered sources.
- **ID Uniqueness**: All IDs globally unique. Patterns: `thm-[lowercase-hyphenated]`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **Provenance Only**: Do not author local RDF schemas or instantiate ontology classes. Generate provenance metadata only (e.g., citation usage tracking).
- **Wikidata QIDs**: Do not guess or hallucinate `wd:Q...` identifiers. Use curl based SPARQL queries, the Wikidata API, or other means to retrieve or check QIDs.
- **Validation**: TeX structure validated against `thesis-structure.schema.yaml`, citations against registry.

## Validation
Run schema validators: `python src/schema/validators/validate_tex_structure.py`, `validate_citations.py`, `validate_consistency.py`.

Note: Current validation uses Python for pragmatic CI/CD orchestration. Long-term validation infrastructure should use Java (Jena SHACL support, JUnit).

## See Also
- `docs/dissertation/bibliography/AGENTS.md` - Citation registry management
