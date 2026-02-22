# AGENTS.md - chap-full-single: Full Context LHS, Single Succedent

## Scope
This chapter investigates weakening under the **Full/Single** context configuration: the left-hand side (antecedent) can be freely extended, while the right-hand side (succedent) is restricted to at most one formula.

This is a **context configuration**, not a named logic. The intuitionistic sequent calculus LJ is an example of a logic that satisfies this configuration, but the chapter defines the structural rule behavior, not a specific logic.

## Context Configuration Table

| LHS Context | RHS Context | Chapter Directory |
|-------------|-------------|-------------------|
| Full | Full | `chap-full-full` |
| Full | Single | `chap-full-single` (this) |
| Single | Full | `chap-single-full` |
| Single | Single | `chap-single-single` |

## Core Constraints
- **IDs**: All IDs globally unique following patterns: `sec-*`, `subsec-*`, `thm-*`, `def-*`, `lem-*`.
- **Content**: Each section should be a page to a few pages of text minimum.
- **Format**: Primary `.md`, compiled via Quarto.

## Section Structure
- `sec-lhs-rules/` - Weakening rules for the left-hand side (antecedent)
- `sec-rhs-rules/` - Weakening rules for the right-hand side (succedent)

Each section directory contains subsections for specific topics (units, modalities, operations).

## Exchange and Contraction Independence

**IMPORTANT**: This chapter is ambivalent to whether Exchange or Contraction hold. We consider:
- The case where Exchange holds (commutative logic)
- The case where Exchange is stripped to non-commutative "order logic"
- The case where Contraction holds or is rejected

The admission of Exchange, its explicit inclusion, its modalization, or its operationalization are independent of the weakening dimensions studied here.

## Special Consideration: Single Succedent
The restriction to at most one formula on the RHS relates to:
- The logical explosion principle (ex falso quodlibet)
- The role of ⊥ (bottom/falsum)
- Constructive interpretations of implication

## Theorem Scoping
Theorems in this chapter:
- Apply only within this chapter (`chap-full-single`)
- May refine theorems from `docs/structural-rules/part/theorems/` (part-level)
- May refine theorems from `docs/structural-rules/theorems/` (root-level)

## Validation
All artifacts must pass automated validation against the structure schema.

## See Also
- `/docs/structural-rules/part/AGENTS.md` - Part policies
- `/docs/structural-rules/AGENTS.md` - Monograph policies
- `/docs/structural-rules/theorems/AGENTS.md` - Theorem policies
