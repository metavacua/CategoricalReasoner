# Part III: Exchange

## Scope
This directory contains the hierarchical structure for the Exchange part (Part III) of the Structural Rules monograph.

## Directory Structure

```
part-exchange/
  ├── chap-symmetric-full-context-lhs-rhs/
  │   ├── sec-full-exchange/
  │   ├── sec-linear-exchange/
  │   ├── sec-affine-exchange/
  │   ├── sec-relevant-exchange/
  │   └── README.md
  ├── chap-asymmetric-full-context-lhs-single-succedent/
  │   ├── sec-full-exchange/
  │   ├── sec-linear-exchange/
  │   ├── sec-affine-exchange/
  │   ├── sec-relevant-exchange/
  │   └── README.md
  ├── chap-asymmetric-single-antecedent-full-context-rhs/
  │   ├── sec-full-exchange/
  │   ├── sec-linear-exchange/
  │   ├── sec-affine-exchange/
  │   ├── sec-relevant-exchange/
  │   └── README.md
  ├── chap-symmetric-single-antecedent-single-succedent/
  │   ├── sec-full-exchange/
  │   ├── sec-linear-exchange/
  │   ├── sec-affine-exchange/
  │   ├── sec-relevant-exchange/
  │   └── README.md
  ├── README.md
  ├── AGENTS.md
  └── part-exchange.tex
```

## Chapters

### 1. Symmetric Exchange with Full Context on LHS and RHS
- Classical (LK) presentation
- Both LHS and RHS can reorder formulas

### 2. Asymmetric Exchange with Full Context on LHS and Single Succedent
- Intuitionistic (LJ) presentation
- LHS has exchange, RHS restricted to single formula

### 3. Asymmetric Exchange with Single Antecedent and Full Context on RHS
- Dual intuitionistic (LDJ) presentation
- LHS restricted to single formula, RHS has exchange

### 4. Symmetric Exchange with Single Antecedent and Single Succedent
- Minimal logic presentation
- Both LHS and RHS restricted to single formulas

## Sections (in each chapter)
- Full Exchange
- Linear Exchange
- Affine Exchange
- Relevant Exchange

## Validation
All artifacts must pass automated validation against the thesis structure schema.

## See Also
- `../part-weakening/` - Part I: Weakening
- `../part-contraction/` - Part II: Contraction
- `../README.md` - Parent directory documentation
