# AGENTS.md - Symmetric Weakening with Full Context on LHS and RHS

## Scope
This chapter covers symmetric weakening where both the left-hand side (antecedent) and right-hand side (succedent) contexts can be freely extended with arbitrary formulas. This is the classical (LK) presentation.

## Core Constraints
- **IDs**: All IDs globally unique following patterns: `sec-*`, `subsec-*`.
- **Content**: Each section should be a page to a few pages of text minimum.

## Section Content Requirements

### Full Weakening (sec-full-weakening)
- Classical weakening rule in LK sequent calculus
- Both LHS and RHS can be extended arbitrarily
- Connection to logical explosion (ex falso quodlibet)
- Paola Zizzi's work on absence of weakening and no erasure

### Linear Weakening (sec-linear-weakening)
- Weakening controlled via exponential modality $\oc A$ in linear logic
- Resource-sensitive: formulas marked with $\oc$ can be weakened
- Distinction between linear and non-linear contexts

### Affine Weakening (sec-affine-weakening)
- Affine logic: weakening admitted, contraction rejected
- Resources can be discarded but not duplicated
- Connection to substructural logic hierarchies

### Relevant Weakening (sec-relevant-weakening)
- Relevant logic: both weakening and contraction rejected
- Premises must be relevant to conclusions
- No arbitrary formula introduction permitted

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
