# AGENTS.md - Schema and Constraints

## Scope
The `src/schema/` directory contains validation schemas, LLM constraints, and TeX → RDF provenance metadata mappings for thesis generation.

## Core Constraints
- **Citations**: Citation system is under development. Direct citation key validation is not currently enforced. See `docs/dissertation/bibliography/README.md` for Java/RO-Crate system requirements. Macros: `\cite{}`, `\citepage{}`, `\citefigure{}`, `\definedfrom{}`, `\provedfrom{}`.
- **ID Uniqueness**: All IDs globally unique. Patterns: `thm-[lowercase-hyphenated]`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **Domain Restriction**: Do not use `http://catty.org/`. The only associated webpage is the MetaVacua GitHub repository (`https://github.com/metavacua/CategoricalReasoner`).
- **Provenance Only**: Do not author local RDF schemas or instantiate ontology classes. Generate provenance metadata only (e.g., citation usage tracking).
- **Wikidata QIDs**: Do not guess or hallucinate `wd:Q...` identifiers. Use curl based SPARQL queries, the Wikidata API, or other means to retrieve or check QIDs.
- **SPARQL Syntax**: SPARQL queries must NOT be wrapped in LaTeX environments (like `lstlisting`) when being processed or saved for execution.
- **Query Quality**: Well-formed queries must return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures.
- **Validation**: TeX structure validated against `thesis-structure.schema.yaml`, citations against registry.

## Validation
Run schema validators as needed: `python src/schema/validators/validate_tex_structure.py` (if available).

Note: Citation and consistency validators are temporarily disabled pending Java/RO-Crate implementation. See `docs/dissertation/bibliography/README.md` for details.

Note: Current validation uses Python for pragmatic CI/CD orchestration. Long-term validation infrastructure should use Java (Jena SHACL support, JUnit).

## See Also
- `docs/dissertation/bibliography/AGENTS.md` - Citation registry management
