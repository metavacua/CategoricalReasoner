# AGENTS.md - Symmetric Weakening with Single Antecedent and Single Succedent

## Scope
This chapter covers symmetric weakening where both the left-hand side (antecedent) and right-hand side (succedent) are restricted to single formulas. This represents **Minimal Logic** (*Minimalkalkül*), originally formulated by **Ingebrigt Johansson** (1936), or the most restrictive form of sequent calculus.

## Core Constraints
- **IDs**: All IDs globally unique following patterns: `sec-*`, `subsec-*`.
- **Content**: Each section should be a page to a few pages of text minimum.

## Special Consideration: Minimal Logic (Minimalkalkül)

This chapter requires special handling due to the dual characterization of minimal logic:

### Variant A: At Most One Formula
Both LHS and RHS inherit the "at most one formula" restriction from LJ and LDJ:
- Left side (antecedent): At most one formula
- Right side (succedent): At most one formula

### Variant B: Non-Empty Context Requirement (More Precise)
A further restriction that at least one side must be non-empty:
- Left side: At most one formula (may be empty)
- Right side: At most one formula (may be empty)
- **Constraint**: At least one of LHS or RHS must contain a non-empty formula

This distinction is critical for accurately representing Johansson's *Minimalkalkül*.

## Section Content Requirements

### Full Weakening (sec-full-weakening)
- Most restrictive weakening form in sequent calculus
- Both LHS and RHS restricted to single formulas (Variant A) or with non-empty constraint (Variant B)
- Minimal logic (*Minimalkalkül*) characterization
- Connection to constructive foundations
- Rejection of *ex falso quodlibet* (false implies anything)
- Reference: Johansson, I. (1936). "Der Minimalkalkül, ein reduzierter intuitionistischer Formalismus."

### Linear Weakening (sec-linear-weakening)
- Linear logic with single formula contexts
- Exponential modality required for any weakening
- Resource consciousness at maximum restriction
- Connection to minimal linear logic

### Affine Weakening (sec-affine-weakening)
- Affine logic with single formula contexts
- Weakening admitted, contraction rejected
- Minimal substructural logic
- Connection to minimal affine reasoning

### Relevant Weakening (sec-relevant-weakening)
- Relevant logic with single formula contexts
- Both weakening and contraction rejected
- Strictest relevance requirements
- Minimal relevant logic characterization

## Key Distinction
This represents the minimal kernel of logical systems—no structural rules beyond exchange. All other logics can be characterized by which structural rules they add back. Johansson's *Minimalkalkül* (1936) is the foundation for understanding this minimal framework.

## Historical Reference
Johansson, I. (1936). "Der Minimalkalkül, ein reduzierter intuitionistischer Formalismus." *Compositio Mathematica*, 4, 119–136.

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
