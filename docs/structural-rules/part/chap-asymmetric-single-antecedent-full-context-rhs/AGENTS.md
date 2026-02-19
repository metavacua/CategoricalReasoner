# AGENTS.md - Asymmetric Weakening with Single Antecedent and Full Context on RHS

## Scope
This chapter covers asymmetric weakening corresponding to dual intuitionistic logic (LDJ) where the left-hand side (antecedent) is restricted to a single formula but the right-hand side (succedent) context is unrestricted.

## Core Constraints
- **IDs**: All IDs globally unique following patterns: `sec-*`, `subsec-*`.
- **Content**: Each section should be a page to a few pages of text minimum.

## Exchange and Contraction Independence

**IMPORTANT**: This chapter is ambivalent to whether Exchange or Contraction hold. We consider:
- The case where Exchange holds (commutative logic)
- The case where Exchange is stripped to non-commutative "order logic"
- The case where Contraction holds or is rejected

The admission of Exchange, its explicit inclusion, its modalization, or its operationalization are independent of the weakening dimensions studied here.

## Section Content Requirements

### Full Weakening (sec-full-weakening)
- Dual intuitionistic weakening in LDJ sequent calculus
- RHS can be freely extended; LHS restricted to single formula
- Restricted non-constructive character emerges from LHS restriction
- Connection to dual intuitionistic logic principles

### Linear Weakening (sec-linear-weakening)
- Linear logic interpretation with single antecedent restriction
- Exponential modality controls weakening availability
- Connection to linear dual intuitionistic logic

### Affine Weakening (sec-affine-weakening)
- Affine logic with single antecedent
- Weakening admitted, contraction rejected
- Sub-dual-intuitionistic logic characterization

### Relevant Weakening (sec-relevant-weakening)
- Relevant logic with single antecedent
- Both weakening and contraction rejected
- Strict relevance requirements

## Key Distinction
The LHS restriction in LDJ creates a non-constructive logic that differs from both classical (symmetric) and intuitionistic (restricted RHS) logic. This is the dual perspective on constructive logic.

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
