# Chapter: Symmetric Weakening with Single Antecedent and Single Succedent

## Scope
This chapter examines symmetric weakening where both the left-hand side (antecedent) and right-hand side (succedent) are restricted to single formulas. This represents the most restrictive form of weakening in sequent calculi and corresponds to **Minimal Logic** (German: *Minimalkalkül*), originally formulated by **Ingebrigt Johansson** (1936).

## Special Consideration: Minimal Logic (Minimalkalkül)

Minimal logic can be characterized in two equivalent ways:

### Variant A: At Most One Formula
Both LHS and RHS inherit the "at most one formula" restriction from LJ and LDJ:
- Left side: At most one formula in the antecedent
- Right side: At most one formula in the succedent

### Variant B: Non-Empty Context Requirement
A further restriction that at least one side must be non-empty:
- Left side: At most one formula in the antecedent (may be empty)
- Right side: At most one formula in the succedent (may be empty)
- **Constraint**: At least one of LHS or RHS must contain a non-empty formula

This second variant captures the minimal logic character more precisely, excluding the degenerate case where both sides are empty.

## Content

This chapter covers:
- **Full Weakening**: Both LHS and RHS restricted to single formulas
- **Linear Weakening**: Resource-sensitive weakening in linear logic with restrictions
- **Affine Weakening**: Affine logic variant with single formula contexts
- **Relevant Weakening**: Relevant logic variant with single formula contexts

## Sequent Calculus Representation

In restricted sequent calculi (Minimal Logic):
- Left side: $\Gamma$ where $|\Gamma| \leq 1$ (at most one formula, may be empty)
- Right side: $\Delta$ where $|\Delta| \leq 1$ (at most one formula, may be empty)
- With constraint: $\Gamma \neq \emptyset$ or $\Delta \neq \emptyset$ (in Variant B)

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
