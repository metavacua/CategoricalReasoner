# AGENTS.md - Subsection Directory

## Scope
This directory contains subsection-level content for the Weakening section of the Structural Rules monograph.

## Core Constraints
- **Formats**: Read/write `*.md`, `*.tex`.
- **IDs**: All IDs globally unique following patterns: `subsec-*`.
- **Content**: Subsections are atomic units of content, typically one to a few pages of text.

## Subsection Content Requirements

This directory contains detailed treatments of:

1. **Intuitionistic Weakening (LJ)**
   - Weakening restricted on RHS
   - Constructive character
   - Sequent calculus representation

2. **Dual Intuitionistic Weakening (LDJ)**
   - Weakening restricted on LHS
   - Restricted non-constructive character
   - Sequent calculus representation

3. **Classical Weakening**
   - Unrestricted on both sides
   - Non-constructive character

4. **Additive vs Multiplicative Weakening**
   - Via linear logic translation/interpretation
   - Additive weakening: resource-insensitive
   - Multiplicative weakening: controlled resource introduction

5. **Sub-intuitionistic and Sub-dual-intuitionistic Logics**
   - Based on which weakening is restricted
   - Connections to linear logic variants

6. **Absence of Weakening and Quantum Theorems**
   - Absence on LHS: corresponds to quantum theorems
   - Absence on RHS: corresponds to quantum theorems
   - Absence on both: corresponds to quantum theorems

7. **Logical Explosion and Weakening**
   - Principle of logical explosion depends on weakening
   - In LFI: weakening on LHS and RHS both involved
   - Linear modalities in standard sequent calculi

8. **Paola Zizzi's Work**
   - Correspondence between absence of weakening and no erasure
   - Quantum logical interpretations

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `/docs/structural-rules/part/chapter/section/README.md` - Parent directory
- `/docs/structural-rules/part/chapter/section/AGENTS.md` - Section-level constraints
