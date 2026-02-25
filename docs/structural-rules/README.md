# Structural Rules: A Categorical Investigation

A standalone monograph examining the three fundamental structural rules of sequent calculi:
**Weakening**, **Contraction**, and **Exchange**.

Directory structure should be structural-rules/part-[ID]/chapter-[ID]/section-[ID]/subsection[ID]. There should be a structural-rules/standards/ subdirectory that includes a standards-compliant subdirectory layout for W3C, OMG, ISO, and relevant standards-defining members of the International Science Council such as but not limited to Committee on Data (CODATA) and World Data System (WDS).

## Table of Contents / Outline

### Part I: Weakening

**File:** `parts/part-weakening.tex`

| Section | ID | Status | Description |
|---------|-----|--------|-------------|
| Introduction | `sec-weakening-intro` | Skeleton | Motivation and historical context for weakening rules |
| Additive Weakening | `sec-weakening-additive` | Planned | Weakening in additive context; resource-insensitive expansion |
| Multiplicative Weakening | `sec-weakening-multiplicative` | Planned | Weakening in multiplicative context; controlled resource introduction |
| Weakening-Free Logics | `sec-weakening-free` | Planned | Logics without weakening: relevant, linear, affine |
| Categorical Semantics | `sec-weakening-categorical` | Planned | Cartesian structure and terminal objects |

**Subsections to Develop:**
- `parts/sec-weakening-history.tex` - Historical development from Gentzen to Girard
- `parts/sec-weakening-additive-formal.tex` - Formal presentation of additive weakening
- `parts/sec-weakening-multiplicative-formal.tex` - Formal presentation of multiplicative weakening
- `parts/sec-weakening-semantics.tex` - Model-theoretic and categorical semantics
- `parts/sec-weakening-computational.tex` - Computational interpretations

---

### Part II: Contraction

**File:** `parts/part-contraction.tex`

| Section | ID | Status | Description |
|---------|-----|--------|-------------|
| Introduction | `sec-contraction-intro` | Skeleton | Motivation and historical context for contraction rules |
| Additive Contraction | `sec-contraction-additive` | Planned | Contraction in additive context; implicit duplication |
| Multiplicative Contraction | `sec-contraction-multiplicative` | Planned | Contraction in multiplicative context; explicit resource management |
| Contraction-Free Logics | `sec-contraction-free` | Planned | Logics without contraction: linear, affine, relevant |
| Categorical Semantics | `sec-contraction-categorical` | Planned | Cartesian structure and diagonal maps |

**Subsections to Develop:**
- `parts/sec-contraction-history.tex` - Historical development
- `parts/sec-contraction-additive-formal.tex` - Formal presentation of additive contraction
- `parts/sec-contraction-multiplicative-formal.tex` - Formal presentation of multiplicative contraction
- `parts/sec-contraction-semantics.tex` - Model-theoretic and categorical semantics
- `parts/sec-contraction-computational.tex` - Computational interpretations (contraction as duplication)

---

### Part III: Exchange

**File:** `parts/part-exchange.tex`

| Section | ID | Status | Description |
|---------|-----|--------|-------------|
| Introduction | `sec-exchange-intro` | Skeleton | Motivation and historical context for exchange rules |
| Permutation of Antecedents | `sec-exchange-antecedent` | Planned | Reordering premises in sequent contexts |
| Permutation of Succedents | `sec-exchange-succedent` | Planned | Reordering conclusions in sequent contexts |
| Exchange-Free Logics | `sec-exchange-free` | Planned | Non-commutative logics: Lambek, bunched implications |
| Categorical Semantics | `sec-exchange-categorical` | Planned | Symmetric monoidal structure and braidings |

**Subsections to Develop:**
- `parts/sec-exchange-history.tex` - Historical development
- `parts/sec-exchange-antecedent-formal.tex` - Formal presentation of antecedent exchange
- `parts/sec-exchange-succedent-formal.tex` - Formal presentation of succedent exchange
- `parts/sec-exchange-semantics.tex` - Model-theoretic and categorical semantics
- `parts/sec-exchange-computational.tex` - Computational interpretations (exchange as permutation)

---

## Structural Rules Overview

The three structural rules govern the manipulation of sequent contexts:

| Rule | Sequent Form | Interpretation | Category Theory |
|------|--------------|----------------|-----------------|
| **Weakening (W)** | $\dfrac{\Gamma \vdash C}{\Gamma, A \vdash C}$ | Add irrelevant premises | Terminal object, projection |
| **Contraction (C)** | $\dfrac{\Gamma, A, A \vdash C}{\Gamma, A \vdash C}$ | Reuse premises | Diagonal map, comonoid |
| **Exchange (E)** | $\dfrac{\Gamma, A, B, \Delta \vdash C}{\Gamma, B, A, \Delta \vdash C}$ | Reorder premises | Symmetry, braiding |

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

1. **Skeleton files** (`part-*.tex`) contain the Part structure and `\input{}` statements for subsections
2. **Subsection files** (`sec-*.tex`) are 250-1000 word atomic documents
3. **Each subsection** corresponds to a single PR for focused review

---

## Related Documents

- Main thesis: `../dissertation/`
- Architecture appendix: `../dissertation/architecture-appendix.tex`
- AGENTS.md constraints apply to all generated content
