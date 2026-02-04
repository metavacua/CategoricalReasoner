# AGENTS.md - Thesis Development

## Scope
The `thesis/` directory contains LaTeX source code for the Catty thesis: chapters, macros, bibliography, and compiled PDF output.

## Core Constraints
- **Compilation**: `pdflatex` and `biber` required. All files must compile without errors. Cross-references must resolve.
- **Citations**: Use ONLY registered keys from `docs/dissertation/bibliography/citations.yaml`. Macros: `\cite{}`, `\citepage{}`, `\definedfrom{}`, `\provedfrom{}`.
- **IDs**: Globally unique across corpus. Patterns: `thm-*`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`.
- **Structure**: Chapters follow pattern `sec-[lowercase-hyphenated]`. Mathematical content uses proper notation: `\vdash`, `\land`, `\lor`, `\to`, `\rightarrow`, `\otimes`.
- **Content**: Theorems require statements and proofs. Definitions require terms and meanings. Examples instantiate abstract concepts.
- **Architecture Note**: Thesis is TeX primary artifact. RDF is secondary/metadata-only (provenance extraction optional, not bidirectional). No RDF content in thesis.

## Validation
Run `python src/schema/validators/validate_tex_structure.py --tex-dir thesis/chapters/` to verify structure, citations, and ID uniqueness.

## See Also
- `src/schema/AGENTS.md` - Citation and ID constraints
- `docs/dissertation/bibliography/AGENTS.md` - Citation registry
