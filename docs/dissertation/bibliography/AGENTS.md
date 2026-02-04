# AGENTS.md - Bibliography Management

## Scope
The `docs/dissertation/bibliography/` directory contains the master citation registry (`citations.yaml`) for all thesis artifacts.

## Core Constraints
- **Registry**: `citations.yaml` is the single source of truth. All citation keys must exist in this registry.
- **Key Format**: `[author][year][keyword]` - lowercase, hyphenated, globally unique (e.g., `girard1987linear`).
- **Required Fields**: `title`, `author`, `year`, `type` (book/article/conference/incollection/thesis/report).
- **External Links**: Include `doi`, `url`, `wikidata` (QID), `dbpedia`, `arxiv` where available. Links must be resolvable.
- **TeX Integration**: Citation macros defined in `thesis/macros/citations.tex`. Keys are case-sensitive.
- **Architecture Note**: Bibliography is citation registry only, not connected to local `/src/ontology/` RDF files (which contain examples only). No RDF authoring required.

## Validation
Validate YAML syntax: `python -c "import yaml; yaml.safe_load(open('citations.yaml'))"`. Verify all citation keys used in thesis exist in registry.

## See Also
- `src/schema/AGENTS.md` - Citation usage constraints
