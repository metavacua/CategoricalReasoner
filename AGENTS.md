# AGENTS.md - Catty Thesis Repository

## Scope

This repository implements the Catty thesis: categorical foundations for logics and their morphisms. Agents generate Markdown manuscript content processed by Quarto. Semantic web data is consumed from external sources (SPARQL endpoints, linked data, GGG) via Jena, not authored locally.

## Document Hierarchy (AMS-Aligned)

### Structural Hierarchy

Documents follow a coherent structural hierarchy from largest to smallest unit:

| Level | LaTeX Command | Quarto/MD Equivalent | Example |
|-------|---------------|----------------------|---------|
| 1 | `\book` | `_quarto.yml` project | The complete thesis |
| 2 | `\part` | Part divisions in `_quarto.yml` | "Foundations" |
| 3 | `\chapter` | Level 1 heading `#` | `# Introduction` |
| 4 | `\section` | Level 2 heading `##` | `## Motivation` |
| 5 | `\subsection` | Level 3 heading `###` | `### Key Concepts` |
| 6 | `\paragraph` | Level 4 heading `####` | `#### Special Case` |

### Logical Hierarchy (Mathematical Environments)

Mathematical statements follow the traditional AMS theorem environment ordering:

| Type | Purpose | Numbering |
|------|---------|-----------|
| **Axiom** | Foundational assumptions | Within chapter: Axiom 2.1, 2.2... |
| **Definition** | Precise meaning of terms | Within chapter: Definition 2.1.1, 2.1.2... |
| **Theorem** | Major results | Within chapter: Theorem 2.2.1, 2.2.2... |
| **Lemma** | Supporting results | Shares theorem numbering: Lemma 2.2.3 |
| **Corollary** | Direct consequences | Shares theorem numbering: Corollary 2.2.4 |
| **Proposition** | Minor results | Shares theorem numbering: Proposition 2.2.5 |
| **Example** | Concrete instantiations | Within chapter: Example 2.3.1... |
| **Remark** | Observations and notes | Within chapter: Remark 2.4.1... |
| **Proof** | Verification of claims | QED symbol, unnumbered or explicitly referenced |

## Core Constraints

- **Primary Format**: All manuscript content in `*.md` files with YAML frontmatter. Quarto processes these identically to `.qmd` while enabling GitHub-native rendering.
- **Secondary Formats**: `*.tex` (LaTeX snippets for math packages), `*.yaml` (configuration), `*.py` (auxiliary CI/CD).
- **Languages**: Markdown with LaTeX math, Python >=3.8 (CI/CD only), Java (primary ecosystem for validation and transformation).
- **Citation System**: Under development. Direct citation key validation is not currently enforced. See `docs/dissertation/bibliography/README.md` for Java/RO-Crate system requirements.
- **IDs**: Hierarchical numbering replaces legacy `thm-*`, `def-*` patterns. Cross-references use Quarto syntax: `@sec-introduction`, `@thm-category-theorem`.
- **MSC2020 Metadata**: All chapters should include Mathematics Subject Classification codes in YAML frontmatter:
  - `msc-primary`: Single primary classification (e.g., `03B22`)
  - `msc-secondary`: List of secondary classifications (e.g., `["03G30", "18D15"]`)
- **Validation**: Successful Quarto compilation serves as validation. No custom validation scripts required.
- **Semantic Web Data**: Consumed from external sources. Do not author local RDF schemas or instantiate ontology classes.
- **Domain Restriction**: Do not use `http://catty.org/`. The only associated webpage is the MetaVacua GitHub repository (`https://github.com/metavacua/CategoricalReasoner`). Any script or artifact using `catty.org` is invalid.
- **SPARQL Execution**: All documented queries should be run against external endpoints. Evidence should be returned as valid TTL.
- **SPARQL Syntax**: SPARQL queries should NOT be wrapped in LaTeX environments when being processed or saved for execution.
- **Query Quality**: Well-formed queries should return error-free non-empty results. Empty result sets or timeouts (over 60s) are considered failures.

## Quarto Configuration

The manuscript uses Quarto for building HTML and PDF outputs:

- **Configuration**: `docs/_quarto.yml` defines the book structure
- **LaTeX Preamble**: `docs/preamble.tex` contains AMS theorem environments and essential packages:
  - `amsmath`, `mathtools`, `amsthm`, `amssymb` (core AMS)
  - `ebproof` or `bussproofs` (proof trees)
  - `stmaryrd` (denotational semantics)
  - `tikz-cd` (commutative diagrams)
  - `turnstile` (sequent calculus notation)
- **Math Rendering**: LaTeX math syntax is supported in both HTML (via MathJax) and PDF outputs

## RO-Crate Integration

Research data packaging follows OAIS-aligned procedures:

- **When to Generate**: On publication, deposit, or milestone completion
- **How to Generate**: Via Java implementation (see `docs/dissertation/bibliography/README.md`)
- **Purpose**: Create Submission Information Package (SIP) for long-term preservation
- **Integration**: Quarto YAML metadata feeds into RO-Crate generation

## File Structure

```
docs/
├── _quarto.yml          # Book configuration
├── preamble.tex         # LaTeX preamble for math environments
├── index.md             # Front matter (abstract, toc)
├── chapters/
│   ├── introduction.md
│   ├── sequents.md
│   └── ...
└── RO-CRATE.md          # Research data packaging procedures
```

## Validation

All artifacts should pass automated validation. Acceptance criteria are boolean tests only.

## See Also

- `docs/dissertation/AGENTS.md` - Thesis-specific Markdown workflow
- `src/schema/AGENTS.md` - Citation and ID constraints
- `docs/RO-CRATE.md` - Research data packaging procedures
