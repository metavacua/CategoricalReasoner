# AGENTS.md - Bibliography Management

## Scope
The `docs/dissertation/bibliography/` directory contains the master citation registry for all thesis artifacts.

## Core Constraints
- **Key Format**: `[author][year][keyword]` - lowercase, hyphenated, globally unique (e.g., `girard1987linear`).
- **Required Fields**: `title`, `author`, `year`, `type` (book/article/journal/conference/incollection/thesis/report/preprint).
- **External Links**: Include `doi`, `url`, `wikidata` (QID), `dbpedia`, `arxiv` where available. Links must be resolvable, and they must resolve to semantically correct and relevant resources; e.g. a link to a paper on linear logic by a specific author should actually resolve to a representation or copy of that paper by that author not merely some random page that isn't 404.
- **TeX Integration**: Citation macros defined in thesis LaTeX files. Keys are case-sensitive.

## See Also
- `src/schema/AGENTS.md` - Citation usage constraints
- Root `AGENTS.md` - General citation constraints
