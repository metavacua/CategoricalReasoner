# Thesis Directory

## Purpose

The `thesis/` directory contains LaTeX source code for the Catty thesis. This is the primary artifact of the project: a formal academic work on categorical foundations for logics and their morphisms.

## Structure

### Root Files

- **`main.tex`**: Main thesis document that includes all chapters
- **`preamble.tex`**: LaTeX preamble with packages, macros, and settings
- **`Makefile`**: Build configuration for PDF compilation

### `chapters/` Directory

Contains individual chapter files following naming pattern `sec-[lowercase-hyphenated].tex`:

- `sec-introduction.tex` - Introduction and motivation
- `sec-categorical-semantic-audit.tex` - Audit of semantic web resources
- `sec-categorical-foundations.tex` - Categorical model of logics
- `sec-morphisms.tex` - Morphisms between logics
- `sec-two-d-lattice.tex` - Two-dimensional lattice structure
- `sec-curry-howard.tex` - Curry-Howard correspondence
- `sec-conclusions.tex` - Conclusions and future work

### `macros/` Directory

Contains LaTeX macro definitions:

- **`citations.tex`**: Citation macros (`\cite`, `\citepage`, etc.)
- **`mathematics.tex`**: Mathematical notation and symbols
- **`structure.tex`**: Structure macros (theorems, definitions, proofs)

## Compilation

### Prerequisites

- LaTeX distribution (e.g., TeX Live, MiKTeX)
- `pdflatex` compiler
- `biber` bibliography processor
- Standard LaTeX packages (see `preamble.tex`)

### Building PDF

```bash
cd thesis
make
```

Or manually:
```bash
pdflatex main.tex
biber main
pdflatex main.tex
pdflatex main.tex
```

The output PDF is `thesis/main.pdf`.

### Cleaning Build Artifacts

```bash
cd thesis
make clean
```

## Constraints and Guidelines

### Structure Validation

Thesis structure must validate against `src/schema/thesis-structure.schema.yaml`:

- All chapters must have unique IDs matching pattern `sec-[lowercase-hyphenated]`
- All theorems must have IDs matching pattern `thm-[lowercase-hyphenated]`
- All definitions must have IDs matching pattern `def-[lowercase-hyphenated]`
- All lemmas must have IDs matching pattern `lem-[lowercase-hyphenated]`
- All examples must have IDs matching pattern `ex-[lowercase-hyphenated]`

**Run validation**:
```bash
python src/schema/validators/validate_tex_structure.py --tex-dir thesis/chapters/
```

### Citation Usage

All citations must use keys from `docs/dissertation/bibliography/citations.yaml`:

```latex
% Good
This result follows from \cite{girard1987linear}.

% Bad - citation not in registry
This result follows from \cite{unregistered2020paper}.
```

**Citation macros**:
- `\cite{key}` - Simple citation
- `\citepage{key}{page}` - Citation with page number
- `\citefigure{key}{figure}` - Citation with figure/table reference
- `\definedfrom{term}{key}` - Definition cites source
- `\provedfrom{theorem}{key}` - Theorem cites proof source

**Validate citations**:
```bash
python src/schema/validators/validate_citations.py \
  --tex-dir thesis/chapters/ \
  --bibliography docs/dissertation/bibliography/citations.yaml
```

### Mathematical Notation

Use proper LaTeX mathematical notation:

- Sequent: `$\Gamma \vdash A$`
- Logical operators: `$\land$, `$\lor`, `$\to$, `$\rightarrow`, `$\otimes`
- Category theory arrows: `$f: A \to B$`, `$g \circ f$
- Funtors: `$F: \mathcal{C} \to \mathcal{D}$`

### Content Requirements

**Theorems**: Must include statement and proof
```latex
\begin{theorem}[thm-example]{Example Theorem}
  Statement of the theorem goes here.
\end{theorem}

\begin{proof}[proof-example]
  Proof steps go here.
  \qed
\end{proof}
```

**Definitions**: Must include term and meaning
```latex
\begin{definition}[def-example]{Example Definition}
  Term is defined as: meaning goes here.
\end{definition}
```

**Examples**: Must include description and instantiation
```latex
\begin{example}[ex-example]{Example}
  Description of the concept.
  Instantiation: specific example goes here.
\end{example}
```

## Relationship to Semantic Web

### Thesis is Primary

The thesis content is the core deliverable. RDF is used **only** for metadata and provenance tracking extracted from TeX, not as a primary representation.

**Key points**:
- Thesis is written in LaTeX, not RDF
- No bidirectional consistency with RDF required
- RDF can be extracted as provenance metadata (who cites what, structure tracking)
- Do not validate thesis content against local RDF schemas

### Metadata Extraction (Optional)

RDF metadata can be extracted from thesis for provenance tracking:

- Citation usage: Which theorems cite which sources
- Structure tracking: Section hierarchy and organization
- Content indexing: Theorems, definitions, and their locations

This extraction is **unidirectional** (TeX → RDF) and does not affect thesis content or validation.

## Content Overview

### Chapter 1: Introduction

Motivation, background, and contributions of the thesis.

### Chapter 2: Categorical Semantic Audit

Comprehensive audit of RDF/OWL schemas and knowledge graphs that support the category-theoretic framework:
1. Category Theory Foundation - RDF/OWL representations from DBPedia, Wikidata
2. Logics as Categorical Objects - Modeling LK, LJ, LDJ, linear logic
3. Morphism Catalog - Sequents restriction morphisms, structural rule morphisms
4. Two-Dimensional Lattice - Formalized as a poset category
5. Curry-Howard Model - Equivalence of logic and type theory categories
6. Reusable Ontologies - Inventory of 11+ resources with license compatibility
7. Integration Roadmap - How to import and extend external resources

### Chapter 3: Categorical Foundations

Formal development of the categorical model:
- Logics as objects in a category
- Morphisms as extension and interpretation relationships
- Functorial mappings between proof systems
- Two-dimensional lattice structure

### Chapter 4: Morphisms Between Logics

Detailed analysis of morphism types:
- Extension morphisms (adding axioms or structural rules)
- Interpretation morphisms (translating between logics)
- Adjoint functor pairs
- Preservation properties

### Chapter 5: Two-Dimensional Lattice

The lattice organization of logics:
- Horizontal dimension: Sequents restrictions (single vs. multi-conclusion)
- Vertical dimension: Structural rules (weakening, contraction, exchange)
- Poset category structure
- Order-theoretic properties

### Chapter 6: Curry-Howard Correspondence

Categorical equivalence between logic and type theory:
- Formulae-as-types interpretation
- Proofs-as-programs correspondence
- Cartesian closed categories and *-autonomous categories
- Applications to programming language semantics

### Chapter 7: Conclusions

Summary of contributions and future work directions.

## Validation Workflow

Run all validators before committing changes:

```bash
# Validate structure
python src/schema/validators/validate_tex_structure.py --tex-dir thesis/chapters/

# Validate citations
python src/schema/validators/validate_citations.py \
  --tex-dir thesis/chapters/ \
  --bibliography docs/dissertation/bibliography/citations.yaml

# Validate consistency
python src/schema/validators/validate_consistency.py \
  --tex-dir thesis/chapters/ \
  --bibliography docs/dissertation/bibliography/citations.yaml
```

## Common Issues and Solutions

### Compilation Errors

**Error**: `! Undefined control sequence.`
**Fix**: Ensure macro is defined in `macros/` or standard LaTeX package

**Error**: `! LaTeX Error: Environment theorem undefined.`
**Fix**: Include `\usepackage{amsthm}` or define custom environment in `macros/structure.tex`

**Error**: Citation warnings (undefined references)
**Fix**: Run `biber` and `pdflatex` twice to resolve cross-references

### Validation Failures

**Error**: `Invalid theorem ID 'thm.Example'`
**Fix**: Use lowercase hyphenated IDs: `thm-example`

**Error**: `Citation 'key' not found in docs/dissertation/bibliography/citations.yaml`
**Fix**: Use pre-registered citation key or add to registry

**Error**: `Duplicate ID 'thm-example'`
**Fix**: Use unique IDs across all chapters

## See Also

- `src/schema/README.md` - Validation schemas and constraints
- `docs/dissertation/bibliography/README.md` - Citation registry documentation
- `src/schema/AGENTS.md` - LaTeX structure and citation constraints for LLMs
- `.catty/README.md` - Operational model for thesis generation
