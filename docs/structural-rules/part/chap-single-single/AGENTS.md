# AGENTS.md - chap-single-single: Single Antecedent and Single Succedent

## Scope
This chapter investigates weakening under the **Single/Single** context configuration: both the left-hand side (antecedent) and right-hand side (succedent) are restricted to at most one formula each.

This is a **context configuration**, not a named logic. Minimal logic (*Minimalkalkül*) by Ingebrigt Johansson (1936) is an example of a logic that satisfies this configuration, but the chapter defines the structural rule behavior, not a specific logic.

## Context Configuration Table

| LHS Context | RHS Context | Chapter Directory |
|-------------|-------------|-------------------|
| Full | Full | `chap-full-full` |
| Full | Single | `chap-full-single` |
| Single | Full | `chap-single-full` |
| Single | Single | `chap-single-single` (this) |

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

## Special Consideration: Asymmetric Weakening Rule

**CRITICAL**: This configuration requires the asymmetric weakening rule. The key insight is that weakening on one side EXCLUDES weakening on the other side:

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

## Historical Reference
Johansson, I. (1936). "Der Minimalkalkül, ein reduzierter intuitionistischer Formalismus." *Compositio Mathematica*, 4, 119–136.

## Theorem Scoping
Theorems in this chapter:
- Apply only within this chapter (`chap-single-single`)
- May refine theorems from `docs/structural-rules/part/theorems/` (part-level)
- May refine theorems from `docs/structural-rules/theorems/` (root-level)

## Validation
All artifacts must pass automated validation against the structure schema.

## See Also
- `/docs/structural-rules/part/AGENTS.md` - Part policies
- `/docs/structural-rules/AGENTS.md` - Monograph policies
- `/docs/structural-rules/theorems/AGENTS.md` - Theorem policies
