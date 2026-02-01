# Catty Thesis Schema Infrastructure

## Overview

This directory contains the prescriptive, machine-readable infrastructure for the Catty thesis, enabling robust development with automatic validation and LLM constraints.

## Core Principles

1. **Constraint-based design**: Remove degrees of freedom to force valid output
2. **Prescriptive schema**: Define ALL allowed structures; reject anything outside schema
3. **TeX as primary artifact**: Thesis is LaTeX; RDF is metadata/provenance only (unidirectional: TeX → RDF)
4. **Citation integrity**: All citations must be pre-registered; LLMs cannot invent new citations
5. **Automated validation**: CI/CD rejects invalid combinations
6. **Technology Note**: Current validation uses Python for pragmatic CI/CD orchestration. Long-term validation infrastructure should use Java (Jena SHACL support, JUnit).

## Directory Structure

```
schema/
├── thesis-structure.schema.yaml      # YAML schema for thesis structure
├── tex-rdf-mapping.yaml             # TeX → RDF provenance metadata mapping
├── LLM_CONSTRAINTS.md                # Explicit LLM instructions
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

### 2. Citation Infrastructure

Located in `../bibliography/`:

**Citation Registry** (`bibliography/citations.yaml`):
- Master registry of all approved citations (single source of truth)
- Prevents LLM invention of citations
- 13 pre-registered foundational references
- Each entry has: author, title, year, type, external identifiers

**Validation**: All citation keys used in TeX must exist in this registry. Compilation fails otherwise.

### 3. TeX Citation Macros

Located in `../thesis/macros/citations.tex`:

- `\cite{key}` - simple citation
- `\citepage{key}{page}` - citation with page number
- `\citefigure{key}{figure}` - citation with figure/table reference
- `\definedfrom{term}{key}` - definition cites source
- `\provedfrom{theorem}{key}` - theorem cites proof source

**Validation**: All citation keys must exist in registry; compilation fails otherwise.

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

All validators in `schema/validators/` are **temporary CI/CD helpers**. Long-term validation infrastructure should use Java libraries (Jena SHACL support, JUnit).

#### `validate_tex_structure.py`

Validates TeX files against `thesis-structure.schema.yaml`:
- Parse LaTeX source with proper macro awareness
- Extract structure (chapters, sections, theorems, definitions)
- Validate nesting follows schema
- Validate all IDs are unique and match patterns
- Validate all citations reference registry entries

**Usage**:
```bash
python schema/validators/validate_tex_structure.py --tex-dir thesis/chapters/
```

#### `validate_citations.py`

Validates citations:
- Check every `\cite{key}` in TeX has corresponding entry in `citations.yaml`
- Check external links (DOI, Wikidata, arXiv, URLs) are resolvable

**Usage**:
```bash
python schema/validators/validate_citations.py \
  --tex-dir thesis/chapters/ \
  --bibliography bibliography/citations.yaml \
  --check-external
```

#### `validate_consistency.py`

Validates TeX structure and citation consistency:
- Parse TeX structure
- Validate all citations are registered
- Validate all IDs are unique

**Usage**:
```bash
python schema/validators/validate_consistency.py \
  --tex-dir thesis/chapters/ \
  --bibliography bibliography/citations.yaml
```

### 6. CI/CD Integration

Workflow: `.github/workflows/thesis-validation.yml`

Runs on every PR:
1. Validate TeX structure
2. Validate citations
3. Validate consistency
4. Comment on PR with results
5. Only allow merge if all validations pass

### 7. LLM Constraint Documentation

Located in `LLM_CONSTRAINTS.md`:

**Explicit instructions for LLMs**:
- You may only cite sources from `bibliography/citations.yaml`
- To cite: use `\cite{key}` where key exists in registry
- To add new citation: STOP and report missing citation
- Every theorem must have exactly one proof block
- Every definition must have term and meaning
- All IDs must be unique globally
- Do not author local RDF schemas; generate provenance metadata only
- Your output will be validated; validation failures are fatal

## Validation Workflow

### Full Validation (All Must Pass)

```bash
# Validate TeX structure
python schema/validators/validate_tex_structure.py --tex-dir thesis/chapters/

# Validate citations
python schema/validators/validate_citations.py \
  --tex-dir thesis/chapters/ \
  --bibliography bibliography/citations.yaml \
  --check-external

# Validate consistency
python schema/validators/validate_consistency.py \
  --tex-dir thesis/chapters/ \
  --bibliography bibliography/citations.yaml
```

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
1. `girard2020new` not in citation registry (fails `validate_citations.py`)
2. ID `def-linear` doesn't match pattern `def-[lowercase-hyphenated]` (fails `validate_tex_structure.py`)
3. Theorem has no proof reference (fails `validate_tex_structure.py`)

## Citation Registry

### Available Citations (Version 1.0)

| Key | Author | Title | Year |
|-----|---------|-------|------|
| `girard1987linear` | J.-Y. Girard | Linear Logic | 1987 |
| `kripke1965semantical` | S.A. Kripke | Semantical Analysis of Intuitionistic Logic I | 1965 |
| `sambin2003basic` | G. Sambin | Basic Logic: Reflection, Symmetry, Visibility | 2003 |
| `urbas1993structural` | J. Urbas | On the Structural Rules of Linear Logic | 1993 |
| `trafford2018category` | J. Trafford | A Category Theory Approach to Conceptual Modelling | 2018 |
| `lawvere1963functorial` | F.W. Lawvere | Functorial Semantics of Algebraic Theories | 1963 |
| `mac lane1971categories` | S. Mac Lane | Categories for the Working Mathematician | 1971 |
| `lambek1988category` | J. Lambek | Categories and Categorical Grammars | 1988 |
| `restall2000substructural` | G. Restall | Substructural Logics | 2000 |
| `pierce1991category` | B.C. Pierce | Basic Category Theory for Computer Scientists | 1991 |
| `curyhoward1934` | H.B. Curry | Functionality in Combinatory Logic | 1934 |
| `howard1969formulae` | W.A. Howard | The Formulae-as-Types Notion of Construction | 1969 |
| `negri2011proof` | S. Negri | Proof Analysis: A Contribution to Hilbert's Problem | 2011 |

### Adding New Citations

To add a new citation:

1. **Add to YAML registry** (`bibliography/citations.yaml`):
   ```yaml
   authornew2020paper:
     author: "First Last"
     title: "Paper Title"
     year: 2020
     type: "journal"
     doi: "10.xxxx/..."
     notes: "Description"
   ```

2. **Add to RDF model** (`ontology/citations.jsonld`):
   ```jsonld
   {
     "@id": "http://metavacua.github.io/catty/citations/authornew2020paper",
     "@type": "bibo:Article",
     "dct:creator": "First Last",
     "dct:title": "Paper Title",
     "dct:issued": "2020",
     "dct:identifier": "authornew2020paper"
   }
   ```

3. **Run validation** to ensure consistency:
   ```bash
   python schema/validators/validate_citations.py \
     --tex-dir thesis/chapters/ \
     --bibliography bibliography/citations.yaml \
     --check-external
   ```

## Error Messages and Fixes

### TeX Structure Validation Errors

```
ERROR: thesis/chapters/categorical-semantic-audit.tex:42
  Invalid theorem ID 'thm.Weakening': must match pattern ^thm-[a-z0-9-]+$
```

**Fix**: Change ID to `thm-weakening` (lowercase, hyphens only).

```
ERROR: thesis/chapters/categorical-semantic-audit.tex:15
  Theorem thm-weakening missing title
```

**Fix**: Add title to theorem: `\begin{theorem}[thm-weakening]{Weakening}`.

```
ERROR: Duplicate ID 'thm-weakening' (first defined at thesis/chapters/intro.tex:10)
```

**Fix**: Use unique ID; change to `thm-weakening-ll` or similar.

### Citation Validation Errors

```
ERROR: thesis/chapters/categorical-semantic-audit.tex:42
  Citation 'girard2020new' not found in bibliography/citations.yaml
```

**Fix**: Use pre-registered key or add citation to registry (see above).

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
4. **Citation integrity**: All citations pre-registered; no LLM invention
5. **Automated enforcement**: CI/CD rejects invalid combinations

**For LLMs**: See `LLM_CONSTRAINTS.md` for explicit instructions.

**For developers**: All validators must pass before merging.

## References

- JSON Schema: https://json-schema.org/
- Dublin Core: https://www.dublincore.org/
