SPDX-FileCopyrightText: 2025 Ian Douglas Lawrence Norman McLean

SPDX-License-Identifier: CC-BY-SA-4.0

# AGENTS.md - Asymmetric Weakening with Full Context on LHS and Single Succedent

## Formal Policy Framework
ALL DOCUMENTATION POLICIES ARE FORMALLY DEFINED IN:
- **[Formal Document Policy](../../../formal-document-policy.html)** - Mathematical definitions, category-theoretic model
- **[Implementation Guide](../../../document-policy-implementation.html)** - Operational rules and validation

## Scope
This chapter covers asymmetric weakening corresponding to intuitionistic logic (LJ) where the left-hand side (antecedent) context is unrestricted but the right-hand side (succedent) is restricted to a single formula.
All content is classified as **Documentation** under the formal model.

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

## Classification
- **Type:** Documentation (A_doc)
- **License:** CC BY-SA v4.0 International
- **Format:** LaTeX, Markdown, HTML (for web deployment)
- **Copyrightable:** ⊤

## Constraints
- All files must include CC BY-SA v4.0 license headers
- IDs globally unique following patterns: `sec-*`, `subsec-*`
- Each section should be a page to a few pages of text minimum
- Content must be locally finite consistent

## Validation
- LaTeX compilation (pdflatex)
- License header completeness check
- ID uniqueness verification
- Thesis structure schema compliance

## See Also
- [Formal Document Policy](../../../formal-document-policy.html) - Mathematical model
- [Implementation Guide](../../../document-policy-implementation.html) - Rules and procedures
- [Root AGENTS.md](../../../../AGENTS.md) - Repository-wide policies
- `/docs/structural-rules/part/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
