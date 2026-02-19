# Section: Full Weakening

## Scope
This section covers full weakening in the intuitionistic (LJ) setting where the left-hand side context can be freely extended but the right-hand side is restricted to a single formula.

## Exchange and Contraction Independence

**Important**: This section is ambivalent to whether Exchange or Contraction hold. We consider:
- Exchange holds (commutative logic) or is stripped to order logic
- Contraction holds or is rejected

## RHS Restriction: At Most One Formula

The RHS in LJ is restricted to **at most one formula**, not exactly one formula. This is intimately related to:

1. **Right Weakening and Bottom ($\bot$)**: In LJ, right weakening is not available as a primitive rule, but the treatment of $\bot$ (false/bottom) on the RHS provides a form of weakening-like behavior. The existence of weakening on the right is part of why the principle of explosion (*ex falso quodlibet*) is provable in LJ.

2. **The At-Most-One Restriction**: The restriction to at most one formula on the RHS (rather than exactly one) allows for the empty succedent case, which is semantically connected to $\bot$. This is what gives LJ its constructive character.

## Content
- Intuitionistic weakening in LJ sequent calculus
- Left weakening: $\dfrac{\Gamma \vdash B}{\Gamma, A \vdash B}$ (unrestricted)
- Right weakening: Not available as primitive, but $\bot$ provides related functionality
- Constructive character emerges from RHS "at most one formula" restriction
- Connection to explosion principle through $\bot$ on RHS

## References
- Gentzen's LJ calculus
- Intuitionistic logic literature

## Validation
All artifacts must pass automated validation against the thesis structure schema.
