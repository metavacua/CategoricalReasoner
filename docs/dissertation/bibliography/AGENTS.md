# AGENTS.md - Bibliography Management

## Scope
The `docs/dissertation/bibliography/` directory should contain the citation registry system for all thesis artifacts.

## Core Constraints
- **Status**: Citation system is under development. Java/RO-Crate implementation is required.
- **Key Format**: `[author][year][keyword]` - lowercase, hyphenated, globally unique (e.g., `girard1987linear`).
- **Required Fields**: `title`, `author`, `year`, `type` (book/article/conference/incollection/thesis/report).
- **External Links**: Include `doi`, `url`, `wikidata` (QID), `dbpedia`, `arxiv` where available. Links must be resolvable.
- **TeX Integration**: Citation macros defined in `docs/dissertation/macros/citations.tex`. Keys are case-sensitive.
- **Architecture Note**: Bibliography requires Java/RO-Crate implementation. No local RDF authoring required.

## Validation
Citation validation is temporarily disabled pending Java/RO-Crate implementation. See `README.md` for implementation requirements.

## See Also
- `src/schema/AGENTS.md` - Citation usage constraints
- `README.md` - Java/RO-Crate system requirements and missing components
