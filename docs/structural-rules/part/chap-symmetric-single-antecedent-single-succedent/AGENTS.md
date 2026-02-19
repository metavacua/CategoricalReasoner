# AGENTS.md - Symmetric Weakening with Single Antecedent and Single Succedent

## Scope
This chapter covers symmetric weakening where both the left-hand side (antecedent) and right-hand side (succedent) are restricted to single formulas. This represents **Minimal Logic** (*Minimalkalkül*), originally formulated by **Ingebrigt Johansson** (1936), or the most restrictive form of sequent calculus.

## Core Constraints
- **IDs**: All IDs globally unique following patterns: `sec-*`, `subsec-*`.
- **Content**: Each section should be a page to a few pages of text minimum.

## Special Consideration: Asymmetric Weakening Rule

**CRITICAL**: This is NOT symmetric. The key insight is that weakening on one side EXCLUDES weakening on the other side:

### The Asymmetric Rule
- **If LHS can be weakened**, then RHS CANNOT be weakened
- **If RHS can be weakened**, then LHS CANNOT be weakened
- This prevents the derivation of the empty sequent

### Why This Matters
If we naively allow weakening arbitrarily on both sides, we would derive the empty sequent, meaning the empty sequent is derivable in all extensions including LJ and LK. Instead, specific structural restrictions ensure that at least one formula is valid on LHS or RHS.

### Variant A: At Most One Formula with Asymmetric Weakening
- LHS: At most one formula
- RHS: At most one formula
- Weakening: Either LHS or RHS (never both)

### Variant B: Non-Empty Context (More Precise)
- LHS: 0 or 1 formula
- RHS: 0 or 1 formula
- Constraint: At least one of LHS or RHS must contain a non-empty formula
- Weakening: Either LHS or RHS (never both)

## Exchange and Contraction Independence

**IMPORTANT**: This chapter is ambivalent to whether Exchange or Contraction hold. We consider:
- The case where Exchange holds (commutative logic)
- The case where Exchange is stripped to non-commutative "order logic"
- The case where Contraction holds or is rejected

The admission of Exchange, its explicit inclusion, its modalization, or its operationalization are independent of the weakening dimensions studied here.

## Section Content Requirements

### Full Weakening (sec-full-weakening)
- Most restrictive weakening form in sequent calculus
- Both LHS and RHS restricted to single formulas
- **Asymmetric weakening rule**: Either LHS OR RHS can be weakened, never both
- Minimal logic (*Minimalkalkül*) characterization
- Connection to constructive foundations
- Rejection of *ex falso quodlibet* (false implies anything)
- Reference: Johansson, I. (1936). "Der Minimalkalkül, ein reduzierter intuitionistischer Formalismus."

### Linear Weakening (sec-linear-weakening)
- Linear logic with single formula contexts
- Exponential modality required for any weakening
- Asymmetric weakening constraint applies
- Resource consciousness at maximum restriction
- Connection to minimal linear logic

### Affine Weakening (sec-affine-weakening)
- Affine logic with single formula contexts
- Weakening admitted (asymmetrically), contraction rejected
- Minimal substructural logic
- Connection to minimal affine reasoning

### Relevant Weakening (sec-relevant-weakening)
- Relevant logic with single formula contexts
- Both weakening and contraction rejected
- Strictest relevance requirements
- Minimal relevant logic characterization

## Key Distinction
This represents the minimal kernel of logical systems—no structural rules beyond exchange. All other logics can be characterized by which structural rules they add back. Johansson's *Minimalkalkül* (1936) is the foundation for understanding this minimal framework.

The asymmetric weakening rule is critical: allowing weakening on both sides would collapse to classical logic's empty sequent derivability.

## Historical Reference
Johansson, I. (1936). "Der Minimalkalkül, ein reduzierter intuitionistischer Formalismus." *Compositio Mathematica*, 4, 119–136.

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
