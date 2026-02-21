# Structural Rules: A Categorical Investigation

A standalone monograph examining the three fundamental structural rules of sequent calculi:
**Weakening**, **Contraction**, and **Exchange**.

## Table of Contents / Outline

### Part I: Weakening

**Directory:** `part-weakening/`
**File:** `part-weakening/part-weakening.tex`

| Section | ID | Status | Description |
|---------|-----|--------|-------------|
| Introduction | `sec-weakening-intro` | Skeleton | Motivation and historical context |
| Full Weakening | `sec-full-weakening` | Planned | Classical weakening in LK |
| Linear Weakening | `sec-linear-weakening` | Planned | Resource-sensitive weakening |
| Affine Weakening | `sec-affine-weakening` | Planned | Weakening without contraction |
| Relevant Weakening | `sec-relevant-weakening` | Planned | Weakening rejected |

---

### Part II: Contraction

**Directory:** `part-contraction/`
**File:** `part-contraction/part-contraction.tex`

| Section | ID | Status | Description |
|---------|-----|--------|-------------|
| Introduction | `sec-contraction-intro` | Skeleton | Motivation and historical context |
| Full Contraction | `sec-full-contraction` | Planned | Classical contraction in LK |
| Linear Contraction | `sec-linear-contraction` | Planned | Resource-sensitive contraction |
| Affine Contraction | `sec-affine-contraction` | Planned | Contraction in affine logic |
| Relevant Contraction | `sec-relevant-contraction` | Planned | Contraction in relevance logic |

---

### Part III: Exchange

**Directory:** `part-exchange/`
**File:** `part-exchange/part-exchange.tex`

| Section | ID | Status | Description |
|---------|-----|--------|-------------|
| Introduction | `sec-exchange-intro` | Skeleton | Motivation and historical context |
| Full Exchange | `sec-full-exchange` | Planned | Classical exchange in LK |
| Linear Exchange | `sec-linear-exchange` | Planned | Exchange in linear logic |
| Affine Exchange | `sec-affine-exchange` | Planned | Exchange in affine logic |
| Relevant Exchange | `sec-relevant-exchange` | Planned | Exchange in relevance logic |

---

## Structural Rules Overview

The three structural rules govern the manipulation of sequent contexts:

| Rule | Sequent Form | Interpretation | Category Theory |
|------|--------------|----------------|-----------------|
| **Weakening (W)** | $\dfrac{\Gamma \vdash C}{\Gamma, A \vdash C}$ | Add irrelevant premises | Terminal object, projection |
| **Contraction (C)** | $\dfrac{\Gamma, A, A \vdash C}{\Gamma, A \vdash C}$ | Reuse premises | Diagonal map, comonoid |
| **Exchange (E)** | $\dfrac{\Gamma, A, B, \Delta \vdash C}{\Gamma, B, A, \Delta \vdash C}$ | Reorder premises | Symmetry, braiding |

---

## Directory Organization

The directory structure mirrors the LaTeX book document class hierarchy:

```
structural-rules/
├── part-weakening/          # Part I
│   ├── chap-symmetric-full-context-lhs-rhs/
│   ├── chap-asymmetric-full-context-lhs-single-succedent/
│   ├── chap-asymmetric-single-antecedent-full-context-rhs/
│   └── chap-symmetric-single-antecedent-single-succedent/
├── part-contraction/        # Part II
│   └── (parallel chapter structure)
└── part-exchange/           # Part III
    └── (parallel chapter structure)
```

Each chapter contains four sections:
- Full (classical form)
- Linear (linear logic with modalities)
- Affine (affine logic)
- Relevant (relevance logic)

See `AGENTS.md` for complete directory structure encoding and DAG linking references.

---

## Build Instructions

```bash
make        # Build PDF
make clean  # Remove auxiliary files
make view   # Build and open PDF
```

---

## Development Workflow

Each part is organized for atomic development:

1. **Skeleton files** (`part-*/part-*.tex`) contain the Part structure
2. **Chapter directories** (`chap-*/`) contain chapter-level content
3. **Section directories** (`sec-*/`) contain section-level content
4. **Subsection files** (`subsec-*.tex`) are 250-1000 word atomic documents

---

## Related Documents

- `AGENTS.md` - Directory structure and shared references
- `main.tex` - Main document entry point
- `preamble.tex` - Shared LaTeX preamble
- `../dissertation/` - Main thesis

---

## Validation

All artifacts must pass automated validation against the thesis structure schema.
