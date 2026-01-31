# AGENTS.md - Schema and Constraints

## Scope
The `schema/` directory contains validation schemas, LLM constraints, and bidirectional TeX-RDF mappings for thesis generation.

## Core Constraints
- **Citations**: Use ONLY keys from `bibliography/citations.yaml`. Macros: `\cite{}`, `\citepage{}`, `\citefigure{}`, `\definedfrom{}`, `\provedfrom{}`. Forbidden: invent new keys or use unregistered sources.
- **ID Uniqueness**: All IDs globally unique. Patterns: `thm-[lowercase-hyphenated]`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **RDF Classes**: Instantiate only existing classes from `ontology/catty-categorical-schema.jsonld`. Forbidden: define new `owl:Class` or `owl:ObjectProperty`.
- **TeX-RDF Consistency**: Every TeX element with ID must have corresponding RDF resource with `dct:identifier`. Citations must have RDF provenance links.
- **Validation**: TeX structure validated against `thesis-structure.schema.yaml`, RDF against SHACL shapes, citations against registry.

## Validation
Run schema validators: `python schema/validators/validate_tex_structure.py`, `validate_citations.py`, `validate_rdf.py`, `validate_consistency.py`.

## See Also
- `bibliography/AGENTS.md` - Citation registry management
- `ontology/AGENTS.md` - RDF class definitions
