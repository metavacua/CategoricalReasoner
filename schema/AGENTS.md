# AGENTS.md - Schema and Constraints

## Scope
The `schema/` directory contains validation schemas, LLM constraints, and TeX → RDF provenance metadata mappings for thesis generation.

## Core Constraints
- **Citations**: Use ONLY keys from `bibliography/citations.yaml`. Macros: `\cite{}`, `\citepage{}`, `\citefigure{}`, `\definedfrom{}`, `\provedfrom{}`. Forbidden: invent new keys or use unregistered sources.
- **ID Uniqueness**: All IDs globally unique. Patterns: `thm-[lowercase-hyphenated]`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **Provenance Only**: Do not author local RDF schemas or instantiate ontology classes. Generate provenance metadata only (e.g., citation usage tracking).
- **Validation**: TeX structure validated against `thesis-structure.schema.yaml`, citations against registry.

## Validation
Run schema validators: `python schema/validators/validate_tex_structure.py`, `validate_citations.py`, `validate_consistency.py`.

Note: Current validation uses Python for pragmatic CI/CD orchestration. Long-term validation infrastructure should use Java (Jena SHACL support, JUnit).

## See Also
- `bibliography/AGENTS.md` - Citation registry management
