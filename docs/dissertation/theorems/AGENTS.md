# AGENTS.md - Thesis Theorems

## Scope
The `docs/dissertation/theorems/` directory contains root-level theorems that apply to the entire thesis ("Theoretical Metalinguistics").

## Theorem Scoping by Directory Depth

### Root Level (this directory)
Theorems here apply to ALL chapters of the thesis. They establish fundamental properties of the categorical framework.

Example theorems:
- `thm-functorial-mapping.md` - Functorial mapping from RDF to Java Records
- `thm-cut-elimination-correspondence.md` - Cut elimination corresponds to proof normalization

### Chapter-Level Theorems
Theorems in chapter directories (e.g., `chapters/sequents/theorems/`) refine or specialize these root theorems for their specific topic.

A theorem in a deeper directory may:
- Restrict the scope of a root theorem
- Add additional conditions specific to that chapter
- Prove a stronger form applicable only to that topic

## Subformula Relationship
The subformula principle applies: deeper theorems are subformulas of shallower ones. This ensures logical coherence across the thesis hierarchy.

## Core Constraints
- **ID Patterns**: `thm-*`, `def-*`, `lem-*`, `ex-*` (globally unique across thesis)
- **Format**: Primary `.md`, compiled via Quarto to `.tex`
- **Proof Requirements**: All theorems require formal proofs

## Separation from Monograph
This directory contains theorems for the thesis only. The monograph (`docs/structural-rules/`) has its own theorem directory at `docs/structural-rules/theorems/`.

## Validation
All theorems must pass formal verification and compile without errors.

## See Also
- `docs/dissertation/AGENTS.md` - Thesis policies
- `docs/structural-rules/AGENTS.md` - Monograph policies
- `docs/structural-rules/theorems/AGENTS.md` - Monograph theorem policies
