# AGENTS.md - Bibliography Management

## Scope

The `docs/dissertation/bibliography/` directory contains the citation registry system for all thesis artifacts. The system uses Java/RO-Crate for implementation with Quarto/Markdown integration.

## Core Constraints

### Citation System Status

- **Status**: Citation system is under development
- **Implementation**: Java/RO-Crate implementation is required
- **Integration**: Citations work with Quarto's Pandoc-based citation processing

### Key Format

- **Pattern**: `[author][year][keyword]` — lowercase, hyphenated, globally unique
- **Examples**: `girard1987linear`, `lambek1988introduction`, `jacobs1999categorical`
- **Case Sensitivity**: Keys are case-sensitive

### Required Fields

| Field | Description |
|-------|-------------|
| `title` | Full title of the work |
| `author` | Author(s) in BibTeX format |
| `year` | Publication year |
| `type` | book / article / conference / incollection / thesis / report |

### External Links

Include where available:
- `doi` — Digital Object Identifier
- `url` — Canonical URL
- `wikidata` — QID (e.g., `Q12345`)
- `dbpedia` — DBpedia resource URI
- `arxiv` — arXiv identifier

Links should be resolvable.

### MSC2020 Integration

Bibliography entries should include relevant MSC2020 codes when available:
- Stored in RO-Crate metadata
- Enables subject-based discovery
- Example: `03B22` for abstract deductive systems

## Quarto/Markdown Integration

### Citation Syntax

In Markdown files:

```markdown
Linear logic was introduced by @girard1987linear as a refinement of
classical logic [@girard1987linear, pp. 1-15]. Subsequent work by
@girard1995proofs extended these ideas.
```

### Bibliography File

Quarto uses standard bibliography formats:
- `.bib` — BibTeX format
- `.json` — CSL JSON format
- `.yaml` — YAML format

The bibliography file is referenced in `_quarto.yml`:

```yaml
bibliography: bibliography/references.bib
```

## RO-Crate Requirements

The bibliography system requires Java/RO-Crate implementation per `README.md`:

- **Citation Registry**: Persistent storage of citation metadata
- **Validation**: Link checking and metadata completeness
- **Export**: BibTeX/CSL JSON generation for Quarto
- **OAIS Alignment**: Research data packaging for preservation

## Architecture Note

- Bibliography requires Java/RO-Crate implementation
- No local RDF authoring required for citations
- Quarto handles citation rendering in both HTML and PDF outputs

## Validation

Citation validation is temporarily disabled pending Java/RO-Crate implementation. See `README.md` for implementation requirements.

## See Also

- `../../AGENTS.md` - Root repository policies
- `../../docs/dissertation/AGENTS.md` - Thesis-specific Markdown workflow
- `src/schema/AGENTS.md` - Citation usage constraints
- `README.md` - Java/RO-Crate system requirements and missing components
