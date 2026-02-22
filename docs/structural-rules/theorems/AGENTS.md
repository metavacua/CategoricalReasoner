# AGENTS.md - Structural Rules Monograph Theorems

## Scope
The `docs/structural-rules/theorems/` directory contains root-level theorems that apply to all parts of the monograph (weakening, contraction, exchange).

## Theorem Scoping by Directory Depth

### Root Level (this directory)
Theorems here apply to ALL parts of the monograph. They establish properties that are independent of which structural rule is being investigated.

Example theorems:
- `thm-reflexive-axiom-admissibility.md` - Reflexive axiom is admissible in all sequent calculi
- `thm-identity-cut-elimination.md` - Identity sequents are preserved under cut elimination

### Subdirectory Relationship
Theorems in subdirectories (e.g., `part-weakening/theorems/`, `part-contraction/theorems/`) refine or specialize these root theorems for their specific structural rule.

A theorem in a deeper directory may:
- Restrict the scope of a root theorem
- Add additional conditions
- Prove a stronger form applicable only to that part

## Core Constraints
- **ID Patterns**: `thm-*`, `def-*`, `lem-*`, `ex-*` (globally unique)
- **Format**: Primary `.md`, compiled via Quarto
- **Proof Requirements**: All theorems require formal proofs

## Relationship to Parts
- Root theorems → All parts
- Part theorems → All chapters in that part
- Chapter theorems → That chapter only

This follows the subformula principle: deeper theorems are subformulas of shallower ones.

## Validation
All theorems must pass formal verification and compile without errors.

## See Also
- `docs/structural-rules/AGENTS.md` - Monograph policies
- `docs/structural-rules/part/AGENTS.md` - Part policies
