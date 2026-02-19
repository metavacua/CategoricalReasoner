# Part I: Weakening

## Scope
This directory contains the hierarchical structure for the Weakening part (Part I) of the Structural Rules monograph.

## Directory Structure

See `../AGENTS.md` for complete directory structure specification.

```
part-weakening/
  ├── chap-symmetric-full-context-lhs-rhs/
  │   ├── sec-full-weakening/
  │   ├── sec-linear-weakening/
  │   ├── sec-affine-weakening/
  │   ├── sec-relevant-weakening/
  │   └── README.md
  ├── chap-asymmetric-full-context-lhs-single-succedent/
  │   ├── sec-full-weakening/
  │   ├── sec-linear-weakening/
  │   ├── sec-affine-weakening/
  │   ├── sec-relevant-weakening/
  │   └── README.md
  ├── chap-asymmetric-single-antecedent-full-context-rhs/
  │   ├── sec-full-weakening/
  │   ├── sec-linear-weakening/
  │   ├── sec-affine-weakening/
  │   ├── sec-relevant-weakening/
  │   └── README.md
  ├── chap-symmetric-single-antecedent-single-succedent/
  │   ├── sec-full-weakening/
  │   ├── sec-linear-weakening/
  │   ├── sec-affine-weakening/
  │   ├── sec-relevant-weakening/
  │   └── README.md
  ├── README.md
  ├── AGENTS.md
  └── part-weakening.tex
```

## Chapters

See `../AGENTS.md` for chapter naming convention and cross-part reference mapping.

### 1. Symmetric Weakening with Full Context on LHS and RHS
- Classical (LK) presentation
- Both LHS and RHS can be freely extended

### 2. Asymmetric Weakening with Full Context on LHS and Single Succedent
- Intuitionistic (LJ) presentation
- LHS unrestricted, RHS restricted to single formula

### 3. Asymmetric Weakening with Single Antecedent and Full Context on RHS
- Dual intuitionistic (LDJ) presentation
- LHS restricted to single formula, RHS unrestricted

### 4. Symmetric Weakening with Single Antecedent and Single Succedent
- Minimal logic presentation
- Both LHS and RHS restricted to single formulas

## Sections (in each chapter)
- Full Weakening
- Linear Weakening
- Affine Weakening
- Relevant Weakening

## Content

This part covers:
- Weakening in intuitionistic logic (LJ)
- Weakening in dual intuitionistic logic (LDJ)
- Classical weakening (LK)
- Linear logic weakening
- Sub-intuitionistic and sub-dual-intuitionistic logics
- Connection to logical explosion and LFI
- Paola Zizzi's work on absence of weakening and no erasure

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `../AGENTS.md` - Complete directory structure and shared references
- `../README.md` - Monograph overview
- `../part-contraction/` - Part II: Contraction
- `../part-exchange/` - Part III: Exchange
