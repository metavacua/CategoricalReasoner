# AGENTS.md - Bibliography Management

## Scope
The `docs/dissertation/bibliography/` directory contains the master citation registry (`citations.yaml`) for all thesis artifacts.

## Core Constraints
- **Registry**: `citations.yaml` is the single source of truth. All citation keys must exist in this registry.
- **Key Format**: `[author][year][keyword]` - lowercase, hyphenated, globally unique (e.g., `girard1987linear`).
- **Required Fields**: `title`, `author`, `year`, `type` (book/article/journal/conference/incollection/thesis/report/preprint).
- **External Links**: Include `doi`, `url`, `wikidata` (QID), `dbpedia`, `arxiv` where available. Links must be resolvable.
- **TeX Integration**: Citation macros defined in thesis LaTeX files. Keys are case-sensitive.
- **No Java/RO-Crate**: The citation system uses a simple YAML registry. Do not create Java source code or RO-Crate files.

## Validation
Validate YAML syntax: `python -c "import yaml; yaml.safe_load(open('citations.yaml'))"`. Verify all citation keys used in thesis exist in registry.

## See Also
- `src/schema/AGENTS.md` - Citation usage constraints
- Root `AGENTS.md` - General citation constraints
