# AGENTS.md - Structural Rules Monograph

## Scope
This directory contains the complete monograph on structural rules: Weakening, Contraction, and Exchange. The organization follows the LaTeX book document class hierarchy: Part → Chapter → Section → Subsection → Subsubsection → Paragraph → Subparagraph.

## Directory Structure

The directory structure mirrors the TeX document hierarchy:

```
structural-rules/
├── AGENTS.md                    # This file: structure encoding and shared references
├── README.md                    # Monograph overview
├── main.tex                     # Main document entry point
├── preamble.tex                 # Shared LaTeX preamble
├── Makefile                     # Build configuration
│
├── part-weakening/              # Part I: Weakening
│   ├── AGENTS.md               # Part-specific constraints
│   ├── README.md               # Part overview
│   ├── part-weakening.tex      # Part TeX file
│   │
│   ├── chap-symmetric-full-context-lhs-rhs/           # Chapter 1: Classical LK
│   │   ├── README.md
│   │   ├── sec-full-weakening/
│   │   ├── sec-linear-weakening/
│   │   ├── sec-affine-weakening/
│   │   └── sec-relevant-weakening/
│   │
│   ├── chap-asymmetric-full-context-lhs-single-succedent/  # Chapter 2: Intuitionistic LJ
│   │   ├── README.md
│   │   ├── sec-full-weakening/
│   │   ├── sec-linear-weakening/
│   │   ├── sec-affine-weakening/
│   │   └── sec-relevant-weakening/
│   │
│   ├── chap-asymmetric-single-antecedent-full-context-rhs/ # Chapter 3: Dual LJ
│   │   ├── README.md
│   │   ├── sec-full-weakening/
│   │   ├── sec-linear-weakening/
│   │   ├── sec-affine-weakening/
│   │   └── sec-relevant-weakening/
│   │
│   └── chap-symmetric-single-antecedent-single-succedent/  # Chapter 4: Minimal Logic
│       ├── README.md
│       ├── sec-full-weakening/
│       ├── sec-linear-weakening/
│       ├── sec-affine-weakening/
│       └── sec-relevant-weakening/
│
├── part-contraction/            # Part II: Contraction
│   ├── AGENTS.md
│   ├── README.md
│   ├── part-contraction.tex
│   │
│   ├── chap-symmetric-full-context-lhs-rhs/
│   │   ├── README.md
│   │   ├── sec-full-contraction/
│   │   ├── sec-linear-contraction/
│   │   ├── sec-affine-contraction/
│   │   └── sec-relevant-contraction/
│   │
│   ├── chap-asymmetric-full-context-lhs-single-succedent/
│   │   ├── README.md
│   │   ├── sec-full-contraction/
│   │   ├── sec-linear-contraction/
│   │   ├── sec-affine-contraction/
│   │   └── sec-relevant-contraction/
│   │
│   ├── chap-asymmetric-single-antecedent-full-context-rhs/
│   │   ├── README.md
│   │   ├── sec-full-contraction/
│   │   ├── sec-linear-contraction/
│   │   ├── sec-affine-contraction/
│   │   └── sec-relevant-contraction/
│   │
│   └── chap-symmetric-single-antecedent-single-succedent/
│       ├── README.md
│       ├── sec-full-contraction/
│       ├── sec-linear-contraction/
│       ├── sec-affine-contraction/
│       └── sec-relevant-contraction/
│
└── part-exchange/               # Part III: Exchange
    ├── AGENTS.md
    ├── README.md
    ├── part-exchange.tex
    │
    ├── chap-symmetric-full-context-lhs-rhs/
    │   ├── README.md
    │   ├── sec-full-exchange/
    │   ├── sec-linear-exchange/
    │   ├── sec-affine-exchange/
    │   └── sec-relevant-exchange/
    │
    ├── chap-asymmetric-full-context-lhs-single-succedent/
    │   ├── README.md
    │   ├── sec-full-exchange/
    │   ├── sec-linear-exchange/
    │   ├── sec-affine-exchange/
    │   └── sec-relevant-exchange/
    │
    ├── chap-asymmetric-single-antecedent-full-context-rhs/
    │   ├── README.md
    │   ├── sec-full-exchange/
    │   ├── sec-linear-exchange/
    │   ├── sec-affine-exchange/
    │   └── sec-relevant-exchange/
    │
    └── chap-symmetric-single-antecedent-single-succedent/
        ├── README.md
        ├── sec-full-exchange/
        ├── sec-linear-exchange/
        ├── sec-affine-exchange/
        └── sec-relevant-exchange/
```

## TeX Hierarchy Mapping

| Directory Prefix | TeX Command | Level | Description |
|-----------------|-------------|-------|-------------|
| `part-*/` | `\part{}` | 0 | Major division (Weakening, Contraction, Exchange) |
| `chap-*/` | `\chapter{}` | 1 | Major subdivision (Context configurations) |
| `sec-*/` | `\section{}` | 2 | Content section (Rule variants) |
| `subsec-*/` | `\subsection{}` | 3 | Subsection (Not yet populated) |
| `subsubsec-*/` | `\subsubsection{}` | 4 | Subsubsection (Not yet populated) |

## Chapter Naming Convention

Chapters are named using the pattern:
```
chap-{symmetry}-{lhs-context}-{rhs-context}
```

Where:
- `{symmetry}`: `symmetric` or `asymmetric`
- `{lhs-context}`: `full-context-lhs` or `single-antecedent`
- `{rhs-context}`: `full-context-rhs` or `single-succedent`

### Chapter Index

| ID | Chapter | Logic System |
|----|---------|--------------|
| `chap-symmetric-full-context-lhs-rhs` | Symmetric Full Context | Classical (LK) |
| `chap-asymmetric-full-context-lhs-single-succedent` | Asymmetric LHS/RHS | Intuitionistic (LJ) |
| `chap-asymmetric-single-antecedent-full-context-rhs` | Asymmetric RHS/LHS | Dual-Intuitionistic (LDJ) |
| `chap-symmetric-single-antecedent-single-succedent` | Symmetric Single | Minimal Logic |

## Section Naming Convention

Sections within each chapter follow the pattern:
```
sec-{variant}-{rule}
```

Where `{variant}` is:
- `full`: Classical/standard form
- `linear`: Linear logic with modalities
- `affine`: Affine logic (weakening without contraction)
- `relevant`: Relevant logic

And `{rule}` is one of: `weakening`, `contraction`, `exchange`

## Shared Content References (DAG Linking)

To minimize repetitive content, the following shared concepts are referenced rather than duplicated:

### Common Constraints (referenced from `AGENTS.md`)
- **Formats**: `*.md`, `*.tex` only
- **ID Patterns**: `sec-*`, `subsec-*`, `def-*`, `thm-*`, `lem-*`, `ex-*`
- **TeX File Size**: Minimum 1 page, maximum ~10 pages per subsection

### Chapter Structure Template
Each chapter directory contains:
- `README.md` - Chapter overview with links to corresponding chapters in other parts
- Section subdirectories with `README.md` files

### Cross-Part References

When documenting a structural rule variant, reference corresponding chapters:

| This Chapter | Weakening (I) | Contraction (II) | Exchange (III) |
|--------------|---------------|------------------|----------------|
| `chap-symmetric-full-context-lhs-rhs` | `../../part-weakening/chap-symmetric-full-context-lhs-rhs/` | `../../part-contraction/chap-symmetric-full-context-lhs-rhs/` | `../../part-exchange/chap-symmetric-full-context-lhs-rhs/` |
| `chap-asymmetric-full-context-lhs-single-succedent` | `../../part-weakening/chap-asymmetric-full-context-lhs-single-succedent/` | `../../part-contraction/chap-asymmetric-full-context-lhs-single-succedent/` | `../../part-exchange/chap-asymmetric-full-context-lhs-single-succedent/` |
| `chap-asymmetric-single-antecedent-full-context-rhs` | `../../part-weakening/chap-asymmetric-single-antecedent-full-context-rhs/` | `../../part-contraction/chap-asymmetric-single-antecedent-full-context-rhs/` | `../../part-exchange/chap-asymmetric-single-antecedent-full-context-rhs/` |
| `chap-symmetric-single-antecedent-single-succedent` | `../../part-weakening/chap-symmetric-single-antecedent-single-succedent/` | `../../part-contraction/chap-symmetric-single-antecedent-single-succedent/` | `../../part-exchange/chap-symmetric-single-antecedent-single-succedent/` |

## Assembly Algorithm

The TeX assembly follows a hierarchical traversal:

```
FUNCTION AssembleMonograph(output_file):
    INITIALIZE latex_document with preamble from preamble.tex
    
    FOR each part_dir IN ["part-weakening", "part-contraction", "part-exchange"]:
        part_file = part_dir + "/part-*.tex"
        ADD "\input{" + part_file + "}" TO latex_document
    
    WRITE latex_document TO output_file
    RETURN output_file
```

Each part file is responsible for assembling its chapters, and each chapter structure is assembled similarly.

## Build Process

```bash
make        # Build PDF
make clean  # Remove auxiliary files
make view   # Build and open PDF
```

## Core Constraints

- **Formats**: Read/write `*.md`, `*.tex`
- **IDs**: Globally unique across corpus following patterns: `thm-*`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`
- **TeX Files**: Subsections should be a page to a few pages of text minimum
- **No Local RDF**: Do not author local RDF schemas or instantiate ontology classes
- **No catty.org**: Do not use `http://catty.org/` domain

## Validation

All artifacts must pass automated validation against the thesis structure schema. All criteria must evaluate true.

## See Also

- `../dissertation/` - Main thesis directory
- `../dissertation/bibliography/` - Shared bibliography
