# Chapter: Asymmetric Weakening with Full Context on LHS and Single Succedent

## Scope
This chapter examines asymmetric weakening where the left-hand side (antecedent) context can be freely extended, but the right-hand side (succedent) is restricted to a single formula. This corresponds to intuitionistic logic (LJ) presentation.

## Exchange and Contraction Independence

**Important**: This chapter is ambivalent to whether Exchange or Contraction hold. We consider:
- The case where Exchange holds (commutative logic)
- The case where Exchange is stripped to non-commutative "order logic"
- The case where Contraction holds or is rejected

The admission of Exchange, its explicit inclusion, its modalization, or its operationalization are independent of the weakening dimensions studied here.

## Content

This chapter covers:
- **Full Weakening**: Weakening allowed on LHS with single succedent restriction
- **Linear Weakening**: Resource-sensitive weakening in linear logic interpretation
- **Affine Weakening**: Affine logic variant with single succedent
- **Relevant Weakening**: Relevant logic variant with single succedent

## Sequent Calculus Representation

In intuitionistic sequent calculus (LJ):
- Left weakening: $\dfrac{\Gamma \vdash B}{\Gamma, A \vdash B}$ (unrestricted)
- Right weakening: Not available in pure LJ (restricted to single succedent)

## Constructive Character
The restriction on RHS gives intuitionistic logic its constructive character, preventing the derivation of non-constructive principles like double-negation elimination.

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
