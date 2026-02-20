# Part II: Contraction

## Scope
This directory contains the hierarchical structure for the Contraction part (Part II) of the Structural Rules monograph.

## Directory Structure

```
part-contraction/
  ├── chap-symmetric-full-context-lhs-rhs/
  │   ├── sec-full-contraction/
  │   ├── sec-linear-contraction/
  │   ├── sec-affine-contraction/
  │   ├── sec-relevant-contraction/
  │   └── README.md
  ├── chap-asymmetric-full-context-lhs-single-succedent/
  │   ├── sec-full-contraction/
  │   ├── sec-linear-contraction/
  │   ├── sec-affine-contraction/
  │   ├── sec-relevant-contraction/
  │   └── README.md
  ├── chap-asymmetric-single-antecedent-full-context-rhs/
  │   ├── sec-full-contraction/
  │   ├── sec-linear-contraction/
  │   ├── sec-affine-contraction/
  │   ├── sec-relevant-contraction/
  │   └── README.md
  ├── chap-symmetric-single-antecedent-single-succedent/
  │   ├── sec-full-contraction/
  │   ├── sec-linear-contraction/
  │   ├── sec-affine-contraction/
  │   ├── sec-relevant-contraction/
  │   └── README.md
  ├── README.md
  ├── AGENTS.md
  └── part-contraction.tex
```

## Chapters

### 1. Symmetric Contraction with Full Context on LHS and RHS
- Classical (LK) presentation
- Both LHS and RHS can merge duplicate formulas

### 2. Asymmetric Contraction with Full Context on LHS and Single Succedent
- Intuitionistic (LJ) presentation
- LHS has contraction, RHS restricted to single formula

### 3. Asymmetric Contraction with Single Antecedent and Full Context on RHS
- Dual intuitionistic (LDJ) presentation
- LHS restricted to single formula, RHS has contraction

### 4. Symmetric Contraction with Single Antecedent and Single Succedent
- Related to Minimal Logic (Minimalkalkül) by Ingebrigt Johansson (1936)
- Both LHS and RHS restricted to single formulas
- Distinct from but structurally analogous to Minimal Logic

## Sections (in each chapter)
- Full Contraction
- Linear Contraction
- Affine Contraction
- Relevant Contraction

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `../part-weakening/` - Part I: Weakening
- `../part-exchange/` - Part III: Exchange
- `../README.md` - Parent directory documentation
