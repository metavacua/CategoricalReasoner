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
├── thm-preamble.tex             # Minimal preamble for standalone theorems
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
│   │   │   ├── subsec-lhs-rules/       # LHS structural rules
│   │   │   ├── subsec-rhs-rules/       # RHS structural rules
│   │   │   ├── theorems/               # Theorem/proof documents
│   │   │   └── README.md
│   │   ├── sec-linear-weakening/
│   │   ├── sec-affine-weakening/
│   │   └── sec-relevant-weakening/
│   │
│   ├── chap-asymmetric-full-context-lhs-single-succedent/  # Chapter 2: Intuitionistic LJ
│   ├── chap-asymmetric-single-antecedent-full-context-rhs/ # Chapter 3: Dual LJ
│   └── chap-symmetric-single-antecedent-single-succedent/  # Chapter 4: Related to Minimal Logic
│
├── part-contraction/            # Part II: Contraction
│   ├── AGENTS.md
│   ├── README.md
│   ├── part-contraction.tex
│   └── (parallel chapter structure)
│
└── part-exchange/               # Part III: Exchange
    ├── AGENTS.md
    ├── README.md
    ├── part-exchange.tex
    └── (parallel chapter structure)
```

## TeX Hierarchy Mapping

| Directory Prefix | TeX Command | Level | Description |
|-----------------|-------------|-------|-------------|
| `part-*/` | `\part{}` | 0 | Major division (Weakening, Contraction, Exchange) |
| `chap-*/` | `\chapter{}` | 1 | Major subdivision (Context configurations) |
| `sec-*/` | `\section{}` | 2 | Content section (Rule variants: full, linear, affine, relevant) |
| `subsec-*/` | `\subsection{}` | 3 | Subsection (LHS rules, RHS rules) |
| `thm/` | N/A | - | Standalone theorem/proof documents |

## Subsection Structure

Each section contains two subsections:

### `subsec-lhs-rules/`
Covers left-hand side (antecedent) structural rules:
- Left weakening rules
- Left contraction rules
- Left exchange rules
- Focused variants ($\fweakeningL$, $\fcontractionL$, $\fexchangeL$)

### `subsec-rhs-rules/`
Covers right-hand side (succedent) structural rules:
- Right weakening rules
- Right contraction rules
- Right exchange rules
- Focused variants ($\fweakeningR$, $\fcontractionR$, $\fexchangeR$)

### `thm/` Directory
Contains standalone theorem and proof documents:
- `thm-{id}.tex` - Theorem statements
- `proof-{id}.tex` - Proof documents
- `thm-proof-{id}.tex` - Combined theorem with proof

Each theorem document is self-contained with minimal preamble for standalone compilation.

## Focused Sequent Calculi

This monograph uses focused sequent calculi following Andreoli (1992) and Liang & Miller (2009):

### Notation

| Notation | Meaning |
|----------|---------|
| `\Gamma \Uparrow \Theta \vdash \Delta` | Asynchronous phase (decomposing invertible formulas) |
| `\Gamma; \Omega \focal A` | Synchronous/focused phase (focused on non-invertible formula) |
| `\fweakeningL`, `\fweakeningR` | Focused weakening on left/right |
| `\fcontractionL`, `\fcontractionR` | Focused contraction on left/right |
| `\fexchangeL`, `\fexchangeR` | Focused exchange on left/right |

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
| `chap-symmetric-single-antecedent-single-succedent` | Symmetric Single | Related to Minimal Logic |

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

## Theorem Document Convention

Theorem files in `thm/` directories:
- Use `\input{../../../../../thm-preamble}` for minimal preamble
- Include `\thmid{id}` for theorem identification
- Self-contained for standalone compilation
- Can be included in larger documents

## Build System

### Targets
- `make all` - Build main monograph PDF
- `make theorems` - Build all standalone theorem PDFs
- `make theorem-ID` - Build specific theorem by ID
- `make list-theorems` - List available theorems
- `make html` - Generate HTML version
- `make test` - Run validation tests
- `make clean` - Remove auxiliary files

### Compilation
```bash
make              # Build main PDF
make theorems     # Build all theorem PDFs
make html         # Generate HTML (requires pandoc or tex4ht)
```

## Shared Content References (DAG Linking)

To minimize repetitive content:

### Common Constraints (referenced from `AGENTS.md`)
- **Formats**: `*.md`, `*.tex` only
- **ID Patterns**: `sec-*`, `subsec-*`, `def-*`, `thm-*`, `lem-*`, `ex-*`
- **TeX File Size**: Minimum 1 page, maximum ~10 pages per subsection

### Cross-Part References

| This Chapter | Weakening (I) | Contraction (II) | Exchange (III) |
|--------------|---------------|------------------|----------------|
| `chap-symmetric-full-context-lhs-rhs` | `../../part-weakening/...` | `../../part-contraction/...` | `../../part-exchange/...` |
| `chap-asymmetric-full-context-lhs-single-succedent` | `../../part-weakening/...` | `../../part-contraction/...` | `../../part-exchange/...` |
| `chap-asymmetric-single-antecedent-full-context-rhs` | `../../part-weakening/...` | `../../part-contraction/...` | `../../part-exchange/...` |
| `chap-symmetric-single-antecedent-single-succedent` | `../../part-weakening/...` | `../../part-contraction/...` | `../../part-exchange/...` |

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

Each part file assembles its chapters, and each chapter assembles its sections and theorems.

## Core Constraints

- **Formats**: Read/write `*.md`, `*.tex`
- **IDs**: Globally unique across corpus following patterns: `thm-*`, `def-*`, `lem-*`, `ex-*`, `sec-*`, `subsec-*`
- **TeX Files**: Subsections should be a page to a few pages of text minimum
- **No Local RDF**: Do not author local RDF schemas or instantiate ontology classes
- **No catty.org**: Do not use `http://catty.org/` domain

## Validation

All artifacts must pass automated validation against the thesis structure schema. All criteria must evaluate true.

Run validation:
```bash
make test
```

## External Knowledge Integration

### SPARQL Research Tool

Located at `../../src/utils/sparql_categorical_research.py`

This tool queries Wikidata, DBpedia, and other SPARQL endpoints for categorical definitions, theorems, and axioms.

**Usage:**
```bash
python3 ../../src/utils/sparql_categorical_research.py
```

**Features:**
- Queries multiple endpoints: Wikidata, DBpedia, FactGrid, ISIDORE
- Implements rate limiting (respects endpoint policies)
- Follows Wikidata User Agent Policy
- Verifies data consistency across sources
- Exports results to TeX fragments

**Query Categories:**
1. Category Theory Concepts
2. Logic Theorems
3. Structural Rules (Weakening, Contraction, Exchange)
4. Formal Logic
5. Proof Theory

**Endpoints:**
| Endpoint | URL | Description |
|----------|-----|-------------|
| Wikidata | https://query.wikidata.org/sparql | Primary knowledge graph |
| DBpedia | https://dbpedia.org/sparql | Structured Wikipedia content |
| FactGrid | https://database.factgrid.de/sparql | Historical research data |
| ISIDORE | https://isidore.science/sparql | Scientific publications |

### Research Workflow

1. Run research tool to query external knowledge bases
2. Review `categorical_research_results.json` for findings
3. Curate and verify exported `research_import.tex`
4. Include relevant theorems and definitions in monograph
5. Add citations to bibliography

## See Also

- `../dissertation/` - Main thesis directory
- `../dissertation/bibliography/` - Shared bibliography
- `../../src/utils/README.md` - Research tool documentation
