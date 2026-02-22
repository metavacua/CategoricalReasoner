# Chapter: Asymmetric Weakening with Single Antecedent and Full Context on RHS

## Scope
This chapter examines asymmetric weakening where the left-hand side (antecedent) is restricted to a single formula, but the right-hand side (succedent) context can be freely extended. This corresponds to dual intuitionistic logic (LDJ).

## Exchange and Contraction Independence

**Important**: This chapter is ambivalent to whether Exchange or Contraction hold. We consider:
- The case where Exchange holds (commutative logic)
- The case where Exchange is stripped to non-commutative "order logic"
- The case where Contraction holds or is rejected

The admission of Exchange, its explicit inclusion, its modalization, or its operationalization are independent of the weakening dimensions studied here.

## LHS Restriction: At Most One Formula

The LHS in LDJ is restricted to **at most one formula**, not exactly one formula. This is intimately related to:

1. **Left Weakening and Top ($\top$)**: In LDJ, left weakening is not available as a primitive rule, but the treatment of $\top$ (true/top) on the LHS provides a form of weakening-like behavior. The existence of a form of weakening on the left is part of the logic's characterization.

2. **The At-Most-One Restriction**: The restriction to at most one formula on the LHS (rather than exactly one) allows for the empty antecedent case, which is semantically connected to $\top$.

3. **Dual Logic to Minimalkalkül**: There exists a dual logic to Minimalkalkül which has a similar relation to non-implication as Minimalkalkül has to implication. Like Minimalkalkül on the RHS, this dual logic has exactly one formula on the LHS.

## Content

This chapter covers:
- **Full Weakening**: Weakening allowed on RHS with single antecedent restriction
- **Linear Weakening**: Resource-sensitive weakening in linear logic interpretation
- **Affine Weakening**: Affine logic variant with single antecedent
- **Relevant Weakening**: Relevant logic variant with single antecedent

## Sequent Calculus Representation

In dual intuitionistic sequent calculus (LDJ):
- Left weakening: Not available in pure LDJ (restricted to single antecedent)
- Right weakening: $\dfrac{\Gamma \vdash B}{\Gamma \vdash B, A}$ (unrestricted)

## Non-Constructive Character
The restriction on LHS gives dual intuitionistic logic its restricted non-constructive character, contrasting with both classical logic (symmetric) and intuitionistic logic (restricted RHS).

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
