# AGENTS.md - Asymmetric Weakening with Full Context on LHS and Single Succedent

## Scope
This chapter covers asymmetric weakening corresponding to intuitionistic logic (LJ) where the left-hand side (antecedent) context is unrestricted but the right-hand side (succedent) is restricted to a single formula.

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
- Intuitionistic weakening in LJ sequent calculus
- LHS can be freely extended; RHS restricted to single formula
- Constructive character emerges from RHS restriction
- Connection to intuitionistic logic principles

### Linear Weakening (sec-linear-weakening)
- Linear logic interpretation with single succedent restriction
- Exponential modality controls weakening availability
- Connection to linear intuitionistic logic

### Affine Weakening (sec-affine-weakening)
- Affine logic with single succedent
- Weakening admitted, contraction rejected
- Sub-intuitionistic logic characterization

### Relevant Weakening (sec-relevant-weakening)
- Relevant logic with single succedent
- Both weakening and contraction rejected
- Strict relevance requirements

## Key Distinction from Classical
The RHS restriction in LJ gives intuitionistic logic its constructive character. This is a fundamental distinction from classical logic's symmetric weakening.

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
