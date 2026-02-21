# AGENTS.md - Thesis Development

## Scope

The `docs/dissertation/` directory contains Markdown source for the Catty thesis: chapters, bibliography, and rendered outputs (HTML/PDF). The workflow uses Quarto for building publication-quality documents from `.md` files with YAML frontmatter.

## Markdown-First Workflow

### File Format

- **Primary**: `.md` files with YAML frontmatter (NOT `.qmd`)
- **Rationale**: GitHub renders `.md` natively; Quarto processes both identically
- **Structure**: YAML frontmatter followed by Markdown content with LaTeX math

### YAML Frontmatter Template

```yaml
---
title: "Chapter Title"
abbrev-title: "Short Title"  # For running headers
msc-primary: "03B22"         # Primary MSC2020 code
msc-secondary: ["03G30", "18D15"]  # Secondary codes
abstract: |
  Multi-line abstract describing chapter content.
keywords: [categorical logic, sequent calculus, keyword3]
---
```

## Structural Hierarchy

### Chapter Organization

Chapters follow the pattern `chapter-[lowercase-hyphenated].md`:

```
chapters/
├── chapter-introduction.md
├── chapter-sequents.md
├── chapter-genesis.md
├── chapter-architecture.md
├── chapter-morphisms.md
├── chapter-universes.md
├── chapter-witness.md
├── chapter-formalization.md
├── chapter-categorical-semantic-audit.md
├── chapter-integration.md
├── chapter-philosophy.md
├── chapter-sparql-validation-protocol.md
├── chapter-semantic-web-rag.md
├── chapter-logic-lattice-extracted.md
├── chapter-curry-howard-extracted.md
└── chapter-appendices.md
```

### Mathematical Notation

Mathematical content uses proper LaTeX notation:
- Sequents: `\vdash`, `\Vdash` (turnstile package)
- Connectives: `\land`, `\lor`, `\to`, `\rightarrow`, `\otimes`, `\multimap`
- Categories: `\mathcal{C}`, `\mathbf{Set}`, `\mathsf{Type}`
- Functors: `\mathrm{F}`, `\mathbb{G}`

### Hierarchical Numbering Convention

The legacy ID patterns (`thm-*`, `def-*`, `lem-*`, `ex-*`) are replaced with hierarchical numbering:

| Legacy Pattern | New Convention | Example |
|----------------|----------------|---------|
| `thm-category-equivalence` | Theorem 3.2.1 | `\begin{theorem}`...`\label{thm-category-equivalence}` |
| `def-sequent-calculus` | Definition 2.1.1 | `\begin{definition}`...`\label{def-sequent-calculus}` |
| `lem-structural-property` | Lemma 4.3.2 | `\begin{lemma}`...`\label{lem-structural-property}` |
| `ex-natural-deduction` | Example 2.4.1 | `\begin{example}`...`\label{ex-natural-deduction}` |
| `sec-introduction` | Section 1 | `# Introduction {#sec-introduction}` |
| `subsec-motivation` | Section 1.1 | `## Motivation {#subsec-motivation}` |

Cross-references use Quarto/Pandoc syntax:
- `@sec-introduction` → "Section 1"
- `@thm-category-equivalence` → "Theorem 3.2.1"
- `@def-sequent-calculus` → "Definition 2.1.1"

## Core Constraints

- **Compilation**: Quarto required (`quarto render`). All files should compile without errors. Cross-references should resolve.
- **Content Requirements**:
  - Theorems require statements and proofs
  - Definitions require terms and precise meanings
  - Examples instantiate abstract concepts

## MSC2020 Classification

All chapters should include Mathematics Subject Classification codes:

| Code | Description |
|------|-------------|
| 03B22 | Abstract deductive systems |
| 03B47 | Substructural logics |
| 03G30 | Categorical logic, topoi |
| 18D15 | Closed categories |
| 18D20 | Enriched categories |
| 68N18 | Functional programming and lambda calculus |

## Validation

Validation is performed by Quarto compilation:

```bash
quarto render docs/
```

Successful compilation indicates:
- Valid Markdown syntax
- Resolvable cross-references
- Valid LaTeX math
- Proper YAML frontmatter

## See Also

- `../AGENTS.md` - Root repository policies
- `../RO-CRATE.md` - Research data packaging
- `src/schema/AGENTS.md` - Citation and ID constraints
- `bibliography/AGENTS.md` - Citation registry
