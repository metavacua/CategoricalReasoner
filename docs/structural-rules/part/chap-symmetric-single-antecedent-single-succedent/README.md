# Chapter: Symmetric Weakening with Single Antecedent and Single Succedent

## Scope
This chapter examines symmetric weakening where both the left-hand side (antecedent) and right-hand side (succedent) are restricted to single formulas. This represents the most restrictive form of weakening in sequent calculi and corresponds to **Minimal Logic** (German: *Minimalkalkül*), originally formulated by **Ingebrigt Johansson** (1936).

## Special Consideration: Minimal Logic (Minimalkalkül)

The key insight is that in the minimal logic setting with single formula contexts, weakening on one side EXCLUDES weakening on the other side. This is NOT symmetric—weakening rules are mutually exclusive:

### Asymmetric Weakening Rule
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

**Important**: This chapter is ambivalent to whether Exchange or Contraction hold. We consider:
- The case where Exchange holds (commutative logic)
- The case where Exchange is stripped to non-commutative "order logic"
- The case where Contraction holds or is rejected

The admission of Exchange, its explicit inclusion, its modalization, or its operationalization are independent of the weakening dimensions studied here.

## Content

This chapter covers:
- **Full Weakening**: Both LHS and RHS restricted to single formulas with asymmetric weakening rule
- **Linear Weakening**: Resource-sensitive weakening in linear logic with restrictions
- **Affine Weakening**: Affine logic variant with single formula contexts
- **Relevant Weakening**: Relevant logic variant with single formula contexts

## Sequent Calculus Representation

In restricted sequent calculi (Minimal Logic):
- LHS: $\Gamma$ where $|\Gamma| \leq 1$
- RHS: $\Delta$ where $|\Delta| \leq 1$
- **Asymmetric weakening**: Either $\Gamma$ can be weakened OR $\Delta$ can be weakened, never both

## Minimal Logic Character
This most restrictive form corresponds to minimal logic (*Minimalkalkül*)—the kernel of constructive logics where no structural rules beyond exchange are assumed. Johansson introduced minimal logic as a subsystem of intuitionistic logic that rejects the principle of *ex falso quodlibet* (false implies anything).

## Historical Note
Ingebrigt Johansson's *Minimalkalkül* (1936) is the foundation for understanding the relationship between intuitionistic, dual-intuitionistic, and minimal logics. It provides the minimal logical framework from which other constructive logics can be obtained by adding specific principles.

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
- Johansson, I. (1936). "Der Minimalkalkül, ein reduzierter intuitionistischer Formalismus." *Compositio Mathematica*, 4, 119–136.
