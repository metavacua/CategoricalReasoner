# AGENTS.md - Structural Rules Part Directory

## Scope
This directory contains the hierarchical structure for the Weakening (Part I) of the Structural Rules monograph.

## Core Constraints
- **Formats**: Read/write `*.md`, `*.tex`. Create subdirectories as needed.
- **IDs**: All IDs globally unique following patterns: `sec-*`, `subsec-*`.
- **TeX Files**: Subsections should be a page to a few pages of text. Do not create files smaller than subsection.
- **Content Structure**: 
  - `chap-*/` - Contains chapter-level content
  - `sec-*/` - Contains section-level content

## Exchange and Contraction Independence

**IMPORTANT - Applies to ALL chapters in this part**:

This part is ambivalent to whether Exchange or Contraction hold. We consider:
- The case where Exchange holds (commutative logic)
- The case where Exchange is stripped to non-commutative "order logic"
- The case where Contraction holds or is rejected

The admission of Exchange, its explicit inclusion, its modalization, or its operationalization are independent of the weakening dimensions studied in this part.

## Special Consideration: Symmetric Single Antecedent and Succedent

The "Symmetric Weakening with Single Antecedent and Single Succedent" chapter requires special handling:

**Asymmetric Weakening Rule**: Weakening on one side EXCLUDES weakening on the other side:
- **If LHS can be weakened**, then RHS CANNOT be weakened
- **If RHS can be weakened**, then LHS CANNOT be weakened
- This prevents the derivation of the empty sequent

If we naively allow weakening arbitrarily on both sides, we would derive the empty sequent, meaning the empty sequent is derivable in all extensions including LJ and LK. Instead, specific structural restrictions ensure that at least one formula is valid on LHS or RHS.

This corresponds to **Minimal Logic** (*Minimalkalkül*) by Ingebrigt Johansson (1936).

## Weakening Content Requirements

This part covers:
1. **Symmetric Full Context (Classical LK)**: Both LHS and RHS can be freely extended
2. **Asymmetric Full Context LHS / Single Succedent (Intuitionistic LJ)**: LHS unrestricted, RHS restricted to single formula
3. **Asymmetric Single Antecedent / Full Context RHS (Dual Intuitionistic LDJ)**: LHS restricted to single formula, RHS unrestricted
4. **Symmetric Single Antecedent and Single Succedent (Minimal Logic)**: Both restricted with asymmetric weakening rule

Each chapter contains four sections:
- **Full Weakening**: Classical weakening allowing arbitrary formula introduction
- **Linear Weakening**: Resource-sensitive via exponential modality
- **Affine Weakening**: Weakening admitted, contraction rejected
- **Relevant Weakening**: Weakening entirely rejected

Additional topics:
- Additive vs multiplicative weakening via linear logic translation
- Sub-intuitionistic and sub-dual-intuitionistic logics
- Absence of weakening on LHS/RHS/both corresponding to quantum theorems
- Logical explosion principle and its dependence on weakening
- LFI and linear modalities involvement
- Paola Zizzi's work on absence of weakening and no erasure

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
