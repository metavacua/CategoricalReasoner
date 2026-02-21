# AGENTS.md - Schema and Constraints

## Scope

The `src/schema/` directory contains validation guidance, LLM constraints, and provenance metadata mappings for thesis generation. The Markdown-centric workflow uses Quarto compilation as the primary validation mechanism.

## Validation Philosophy

### Quarto-Based Validation

In the Markdown-centric workflow, validation is performed by Quarto itself:

- **Compilation as Validation**: Successful `quarto render` validates Markdown syntax, YAML frontmatter, and LaTeX math
- **Off-the-Shelf Tooling**: No custom validation scripts required
- **CI/CD Integration**: GitHub Actions runs Quarto and reports build status

### Legacy Schema Deprecation

The following legacy validation components are removed:

- `thesis-structure.schema.yaml` — replaced by Quarto project configuration
- `validate_tex_structure.py` — replaced by Quarto build process
- `validate_rdf.py` — RDF validation via standard Jena tooling where needed

## Citation Constraints

### Citation System Status

- Citation system is under development
- Direct citation key validation is not currently enforced
- See `docs/dissertation/bibliography/README.md` for Java/RO-Crate system requirements

### Citation Macros (Quarto/Markdown)

Citations use standard Pandoc/Quarto syntax:

- Inline citation: `@girard1987linear`
- Parenthetical: `[@girard1987linear]`
- With page: `[@girard1987linear, p. 42]`
- Multiple: `[@girard1987linear; @lambek1988introduction]`

### ID Patterns

The legacy ID patterns are replaced with hierarchical numbering:

| Type | Legacy Pattern | New Convention |
|------|----------------|----------------|
| Theorem | `thm-[descriptor]` | `thm-category-equivalence` (label) + Theorem X.Y.Z (number) |
| Definition | `def-[descriptor]` | `def-sequent-calculus` (label) + Definition X.Y.Z (number) |
| Lemma | `lem-[descriptor]` | `lem-structural-property` (label) + Lemma X.Y.Z (number) |
| Example | `ex-[descriptor]` | `ex-natural-deduction` (label) + Example X.Y.Z (number) |
| Section | `sec-[descriptor]` | `sec-introduction` (anchor) + Section X (number) |
| Subsection | `subsec-[descriptor]` | `subsec-motivation` (anchor) + Section X.Y (number) |

## Domain Restrictions

- **No catty.org**: Do not use `http://catty.org/`. The only associated webpage is the MetaVacua GitHub repository (`https://github.com/metavacua/CategoricalReasoner`).
- **Wikidata QIDs**: Do not guess or hallucinate `wd:Q...` identifiers. Use curl-based SPARQL queries, the Wikidata API, or other means to retrieve or check QIDs.

## SPARQL Guidelines

- **Syntax**: SPARQL queries should NOT be wrapped in LaTeX environments when being processed or saved for execution
- **Query Quality**: Well-formed queries should return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures
- **Execution**: All documented queries should be run against external endpoints. Evidence should be returned as valid TTL

## Provenance Metadata

- **Purpose Only**: Do not author local RDF schemas or instantiate ontology classes
- **Generation**: Generate provenance metadata only (e.g., citation usage tracking)
- **RO-Crate**: Research data packaging follows OAIS-aligned procedures (see `docs/RO-CRATE.md`)

## Technology Stack

| Purpose | Technology |
|---------|------------|
| Manuscript | Markdown + Quarto |
| Math Rendering | LaTeX (via MathJax/PDF) |
| Validation | Quarto compilation |
| CI/CD | GitHub Actions |
| Semantic Web | Java (Jena) for consumption only |

## See Also

- `../../AGENTS.md` - Root repository policies
- `../../docs/dissertation/AGENTS.md` - Thesis-specific Markdown workflow
- `../../docs/dissertation/bibliography/AGENTS.md` - Citation registry management
- `../../docs/RO-CRATE.md` - Research data packaging procedures
