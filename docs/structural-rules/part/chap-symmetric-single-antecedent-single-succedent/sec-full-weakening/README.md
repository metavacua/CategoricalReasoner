# Section: Full Weakening

## Scope
This section covers full weakening in the minimal logic setting where both the left-hand side and right-hand side are restricted to single formulas. This corresponds to **Minimal Logic** (*Minimalkalkül*) by **Ingebrigt Johansson** (1936).

## Asymmetric Weakening Rule

**CRITICAL**: Weakening on one side EXCLUDES weakening on the other side:
- If LHS can be weakened, then RHS CANNOT be weakened
- If RHS can be weakened, then LHS CANNOT be weakened

This prevents the derivation of the empty sequent.

### Variant A: At Most One Formula
Both LHS and RHS restricted to at most one formula with asymmetric weakening:
- Antecedent: $\leq 1$ formula
- Succedent: $\leq 1$ formula
- Weakening: Either LHS OR RHS (never both)

### Variant B: Non-Empty Context (More Precise)
At most one formula on each side, with at least one side non-empty:
- Antecedent: 0 or 1 formula
- Succedent: 0 or 1 formula
- Constraint: $\textAntecedent \neq \emptyset \lor \textSuccedent \neq \emptyset$
- Weakening: Either LHS OR RHS (never both)

## Exchange and Contraction Independence

**Important**: This section is ambivalent to whether Exchange or Contraction hold. We consider the case where:
- Exchange holds (commutative logic) or is stripped to order logic
- Contraction holds or is rejected

## Content
- Minimal logic (*Minimalkalkül*) weakening presentation
- Both LHS and RHS restricted to single formulas
- **Asymmetric weakening**: Either LHS or RHS can be weakened, never both
- Rejection of *ex falso quodlibet* (explosion)
- Minimal kernel of constructive logics

## Historical Reference
Johansson, I. (1936). "Der Minimalkalkül, ein reduzierter intuitionistischer Formalismus." *Compositio Mathematica*, 4, 119–136.

## Validation
All artifacts must pass automated validation against the thesis structure schema.
