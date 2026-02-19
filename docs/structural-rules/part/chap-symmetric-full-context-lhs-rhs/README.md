# Chapter: Symmetric Weakening with Full Context on LHS and RHS

## Scope
This chapter examines symmetric weakening where both the left-hand side (antecedent) and right-hand side (succedent) contexts can be freely extended with arbitrary formulas. This represents the classical approach to weakening where context is unrestricted on both sides of the sequent.

## Exchange and Contraction Independence

**Important**: This chapter is ambivalent to whether Exchange or Contraction hold. We consider:
- The case where Exchange holds (commutative logic)
- The case where Exchange is stripped to non-commutative "order logic"
- The case where Contraction holds or is rejected

The admission of Exchange, its explicit inclusion, its modalization, or its operationalization are independent of the weakening dimensions studied here.

## Content

This chapter covers:
- **Full Weakening**: Classical weakening allowing arbitrary formula introduction on both LHS and RHS
- **Linear Weakening**: Resource-sensitive weakening in linear logic with exponential modality
- **Affine Weakening**: Weakening without contraction (resources can be discarded but not duplicated)
- **Relevant Weakening**: Weakening rejected entirely (premises must be relevant to conclusions)

## Sequent Calculus Representation

In classical sequent calculus (LK), symmetric weakening is expressed as:
- Left weakening: $\dfrac{\Gamma \vdash \Delta}{\Gamma, A \vdash \Delta}$
- Right weakening: $\dfrac{\Gamma \vdash \Delta}{\Gamma \vdash \Delta, A}$

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
