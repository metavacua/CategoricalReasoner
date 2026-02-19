# AGENTS.md - Structural Rules Part Directory

## Scope
This directory contains the hierarchical structure for the Weakening (Part I) of the Structural Rules monograph.

## Core Constraints
- **Formats**: Read/write `*.md`, `*.tex`. Create subdirectories as needed.
- **IDs**: All IDs globally unique following patterns: `sec-*`, `subsec-*`.
- **TeX Files**: Subsections should be a page to a few pages of text. Do not create files smaller than subsection.
- **Content Structure**: 
  - `part/` - Contains the part-level content
  - `chapter/` - Contains chapter-level content
  - `section/` - Contains section-level content
  - `subsection/` - Contains subsection-level content

## Weakening Content Requirements
This part covers:
1. Intuitionistic weakening (restricted on RHS in LJ)
2. Dual intuitionistic weakening (restricted on LHS in LDJ)
3. Classical weakening (both sides)
4. Additive vs multiplicative weakening via linear logic translation
5. Sub-intuitionistic and sub-dual-intuitionistic logics
6. Absence of weakening on LHS/RHS/both corresponding to quantum theorems
7. Logical explosion principle and its dependence on weakening
8. LFI and linear modalities involvement
9. Paola Zizzi's work on absence of weakening and no erasure

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/README.md` - Parent directory
- `/docs/structural-rules/parts/part-weakening.tex` - Main part file
