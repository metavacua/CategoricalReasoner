# AGENTS.md - Thesis Development

## Scope
The `docs/dissertation/` directory contains the thesis "Theoretical Metalinguistics": chapters, theorems, bibliography, and compiled output.

## Separation from Other Content
This directory is strictly separated from:
- `docs/structural-rules/` - The monograph "Structural Rules: A Categorical Investigation"
- `docs/dev/` - Development artifacts (reports, handbook)

## Core Constraints
- **Compilation**: Quarto workflow. `.md` primary, compiled to `.tex` and PDF.
- **Citations**: Citation system is under development. Direct citation key validation is not currently enforced. See `docs/dissertation/bibliography/README.md` for Java/RO-Crate system requirements. Macros: `\cite{}`, `\citepage{}`, `\definedfrom{}`, `\provedfrom{}`.
- **IDs**: Globally unique across corpus. Patterns: `thm-*`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **Structure**: Chapters follow pattern `sec-[lowercase-hyphenated]`. Mathematical content uses proper notation: `\vdash`, `\land`, `\lor`, `\to`, `\rightarrow`, `\otimes`.
- **Content**: Theorems require statements and proofs. Definitions require terms and meanings. Examples instantiate abstract concepts.
- **Architecture Note**: Thesis is Markdown primary (Quarto workflow), RDF is secondary/metadata-only. No RDF content in thesis chapters.

## Theorem Scoping
The thesis implements theorem scoping by directory depth:
- `theorems/` - Root-level theorems apply to entire thesis
- `chapters/*/theorems/` - Chapter-specific theorems

The subformula principle ensures logical coherence: chapter theorems may restrict or strengthen root theorems.

## Quarto Configuration
The thesis uses Quarto for compilation:
- Primary format: `.md` files
- Quarto renders to `.tex` and PDF
- Configuration: `docs/dissertation/_quarto.yml`

## Content Excluded from Thesis
The following are NOT part of the thesis:
- Reports (moved to `docs/dev/report/`)
- Architecture (moved to `docs/dev/handbook/`)

## Validation
Run Quarto validation:
```
quarto render docs/dissertation/
```

## See Also
- `docs/dissertation/bibliography/AGENTS.md` - Citation registry
- `docs/dissertation/theorems/AGENTS.md` - Theorem policies
- `AGENTS.md` - Root repository policies
