# AGENTS.md - Symmetric Weakening with Single Antecedent and Single Succedent

## Scope
This chapter covers symmetric weakening where both the left-hand side (antecedent) and right-hand side (succedent) are restricted to single formulas. This represents minimal logic or the most restrictive form of sequent calculus.

## Core Constraints
- **IDs**: All IDs globally unique following patterns: `sec-*`, `subsec-*`.
- **Content**: Each section should be a page to a few pages of text minimum.

## Section Content Requirements

### Full Weakening (sec-full-weakening)
- Most restrictive weakening form in sequent calculus
- Both LHS and RHS restricted to single formulas
- Minimal logic characterization
- Connection to constructive foundations

### Linear Weakening (sec-linear-weakening)
- Linear logic with single formula contexts
- Exponential modality required for any weakening
- Resource consciousness at maximum restriction

### Affine Weakening (sec-affine-weakening)
- Affine logic with single formula contexts
- Weakening admitted, contraction rejected
- Minimal substructural logic

### Relevant Weakening (sec-relevant-weakening)
- Relevant logic with single formula contexts
- Both weakening and contraction rejected
- Strictest relevance requirements

## Key Distinction
This represents the minimal kernel of logical systems - no structural rules beyond exchange. All other logics can be characterized by which structural rules they add back.

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
