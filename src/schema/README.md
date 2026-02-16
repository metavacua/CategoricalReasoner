# Catty Thesis Schema Infrastructure

## Overview

This directory contains the prescriptive, machine-readable infrastructure for the Catty thesis, enabling robust development with automatic validation and LLM constraints.

## Core Principles

1. **Constraint-based design**: Remove degrees of freedom to force valid output
2. **Prescriptive schema**: Define ALL allowed structures; reject anything outside schema
3. **TeX as primary artifact**: Thesis is LaTeX; RDF is metadata/provenance only (unidirectional: TeX → RDF)
4. **Citation integrity**: Citation system is under development; Java/RO-Crate implementation required
5. **Automated validation**: CI/CD rejects invalid combinations
6. **Technology Note**: Current validation uses Python for pragmatic CI/CD orchestration. Long-term validation infrastructure should use Java (Jena SHACL support, JUnit).

## Directory Structure

```
src/schema/
├── thesis-structure.schema.yaml      # YAML schema for thesis structure
├── tex-rdf-mapping.yaml             # TeX → RDF provenance metadata mapping
├── README.md                        # This file
└── validators/
    ├── validate_tex_structure.py       # TeX structure validator
    ├── validate_citations.py          # Citation validator
    └── validate_consistency.py       # TeX structure and citation consistency validator
```

## Core Components

### 1. Thesis Structure Schema (`thesis-structure.schema.yaml`)

Defines all valid thesis structures:

- **Root level**: thesis definition (thesis ID, title, optional description)
- **Hierarchy levels**: part → chapter → section → subsection (optional)
- **Content types**: text, definition, theorem, lemma, proof, example, remark, proposition, corollary, conjecture
- **ID patterns**:
  - Theorems: `thm-[lowercase-hyphenated]`
  - Definitions: `def-[lowercase-hyphenated]`
  - Lemmas: `lem-[lowercase-hyphenated]`
  - Examples: `ex-[lowercase-hyphenated]`
  - Sections: `sec-[lowercase-hyphenated]`
  - Subsections: `subsec-[lowercase-hyphenated]`

**Required fields per type**:
- All elements: `id`, `title` (except text)
- Theorems: `statement`, `proof`
- Definitions: `term`, `meaning`
- Proofs: `steps`, `conclusion`
- Examples: `description`, `instantiation`

### 2. TeX Citation Macros

Located in `../docs/dissertation/macros/citations.tex`:

- `\cite{key}` - simple citation
- `\citepage{key}{page}` - citation with page number
- `\citefigure{key}{figure}` - citation with figure/table reference
- `\definedfrom{term}{key}` - definition cites source
- `\provedfrom{theorem}{key}` - theorem cites proof source

**Note**: Citation validation is temporarily disabled pending Java/RO-Crate implementation. See `docs/dissertation/bibliography/README.md` for details.

### 4. TeX → RDF Provenance Metadata Extraction (`tex-rdf-mapping.yaml`)

Defines how TeX elements are extracted as provenance metadata:

```yaml
mappings:
  theorem:
    metadata_type: "provenance"
    tex_macro: "\\theorem{id}{title}"
    properties:
      id: "dct:identifier"
      title: "dct:title"
```

**Unidirectional extraction**:
- Direction: TeX element → provenance metadata (e.g., citation usage tracking)
- RDF generation is one-way: extract metadata from TeX, do not validate against local schema
- Do not author local RDF schemas; generate provenance metadata only

### 5. Automated Validators

All validators in `src/schema/validators/` are **temporary CI/CD helpers**. Long-term validation infrastructure should use Java libraries (Jena SHACL support, JUnit).

#### `validate_tex_structure.py`

Validates TeX files against `thesis-structure.schema.yaml`:
- Parse LaTeX source with proper macro awareness
- Extract structure (chapters, sections, theorems, definitions)
- Validate nesting follows schema
- Validate all IDs are unique and match patterns
- Validate all citations reference registry entries (disabled pending implementation)

**Usage**:
```bash
python src/schema/validators/validate_tex_structure.py --tex-dir docs/dissertation/chapters/
```

#### `validate_citations.py` (TEMPORARILY DISABLED)

Validates citations - temporarily disabled pending Java/RO-Crate implementation.
See `docs/dissertation/bibliography/README.md` for implementation requirements.

#### `validate_consistency.py` (TEMPORARILY DISABLED)

Validates TeX structure and citation consistency - temporarily disabled pending Java/RO-Crate implementation.
See `docs/dissertation/bibliography/README.md` for implementation requirements.

### 6. CI/CD Integration

Workflow: `.github/workflows/thesis-validation.yml`

Runs on every PR:
1. Validate TeX structure
2. Comment on PR with results
3. Only allow merge if all validations pass

Note: Citation and consistency validators are temporarily disabled.

## Validation Workflow

### Full Validation (All Must Pass)

```bash
# Validate TeX structure
python src/schema/validators/validate_tex_structure.py --tex-dir docs/dissertation/chapters/
```

Note: Citation and consistency validators are temporarily disabled pending Java/RO-Crate implementation.
See `docs/dissertation/bibliography/README.md` for implementation requirements.

All validators must exit with status 0 (success).

## Examples

### Valid Thesis Fragment

```latex
\section{Linear Logic}

We define linear logic following \cite{girard1987linear}.

\begin{definition}[def-linear-logic]{Linear Logic}
  Linear logic is a substructural logic that restricts structural rules.
\end{definition}

\begin{theorem}[thm-weakening-fail]{Weakening Fails}
  The weakening rule does not hold in linear logic \cite{girard1987linear}.
\end{theorem}

\begin{example}[ex-linear-sequent]{Linear Sequent}
  The sequent $A \otimes B \vdash C$ is valid in linear logic.
\end{example}
```

This fragment:
- Uses only pre-registered citations
- Has unique IDs following correct patterns
- Includes all required fields
- Passes all validators

### Invalid Thesis Fragment (Will Fail)

```latex
\section{Linear Logic}

We define linear logic following \cite{girard2020new}.

\begin{definition}[def-linear]{Linear Logic}
  Linear logic is...
\end{definition}

\begin{theorem}[thm-weakening]{Weakening}
  The weakening rule holds.
\end{theorem}
```

**Why this fails**:
1. ID `def-linear` doesn't match pattern `def-[lowercase-hyphenated]` (fails `validate_tex_structure.py`)
2. Theorem has no proof reference (fails `validate_tex_structure.py`)
3. Citation validation is temporarily disabled pending Java/RO-Crate implementation

## Error Messages and Fixes

### TeX Structure Validation Errors

```
ERROR: docs/dissertation/chapters/categorical-semantic-audit.tex:42
  Invalid theorem ID 'thm.Weakening': must match pattern ^thm-[a-z0-9-]+$
```

**Fix**: Change ID to `thm-weakening` (lowercase, hyphens only).

```
ERROR: docs/dissertation/chapters/categorical-semantic-audit.tex:15
  Theorem thm-weakening missing title
```

**Fix**: Add title to theorem: `\begin{theorem}[thm-weakening]{Weakening}`.

```
ERROR: Duplicate ID 'thm-weakening' (first defined at docs/dissertation/chapters/intro.tex:10)
```

**Fix**: Use unique ID; change to `thm-weakening-ll` or similar.

Note: Citation validation is temporarily disabled. See `docs/dissertation/bibliography/README.md` for Java/RO-Crate implementation requirements.

## Installation

Install required Python packages:

```bash
pip install pyyaml rdflib
```

Note: Current validation uses Python for pragmatic CI/CD orchestration. Long-term validation infrastructure should use Java (Jena SHACL support, JUnit).

## CI/CD

The GitHub Actions workflow (`.github/workflows/thesis-validation.yml`) automatically validates all changes on PR.

**Workflow steps**:
1. Checkout code
2. Set up Python 3.10
3. Install dependencies (pyyaml, rdflib)
4. Run all 3 validators
5. Comment results on PR
6. Allow merge only if all pass

**View validation results**:
- On GitHub PR page, check the "Thesis Validation" job
- Validator comments are posted automatically
- Fix errors and push again

## Design Philosophy

This infrastructure enforces:

1. **Constraint-based design**: Removes degrees of freedom to force valid output
2. **Prescriptive schema**: Defines ALL allowed structures; rejects anything outside
3. **TeX as primary artifact**: Thesis is LaTeX; RDF is metadata/provenance only (unidirectional: TeX → RDF)
4. **Citation integrity**: Citation system under development; Java/RO-Crate implementation required
5. **Automated enforcement**: CI/CD rejects invalid combinations

**For developers**: All validators must pass before merging. Note: Citation and consistency validators are temporarily disabled.

## References

- JSON Schema: https://json-schema.org/
- Dublin Core: https://www.dublincore.org/
