# Catty Thesis Infrastructure

**Prescriptive, machine-readable infrastructure for robust thesis development with automatic validation and LLM constraints**

This infrastructure implements the specifications from the SWTI (Semantic Web Technology Index) conformance task, providing constraint-based design that forces LLMs and human authors to produce valid, verifiable thesis content.

---

## Table of Contents

1. [Overview](#overview)
2. [Core Principles](#core-principles)
3. [Directory Structure](#directory-structure)
4. [Components](#components)
5. [Validation Workflow](#validation-workflow)
6. [Usage Guide](#usage-guide)
7. [Examples](#examples)
8. [SWTI Compliance](#swti-compliance)

---

## Overview

The Catty thesis infrastructure provides:

- **Prescriptive schemas** defining all allowed thesis structures
- **Citation registry** preventing LLM citation invention
- **Bidirectional TeX ↔ RDF mapping** ensuring consistency
- **Automated validators** enforcing constraints in CI/CD
- **SHACL shapes** for semantic validation
- **LLM constraint documentation** preventing hallucination

All components are designed to **remove degrees of freedom** and **enforce verifiable correctness**.

---

## Core Principles

### 1. Constraint-Based Design

The system forces valid content by:
- Defining explicit schemas for all structures
- Rejecting anything outside the schema
- Validating at multiple levels (syntax, semantics, consistency)

### 2. Citation Integrity

- All citations must be pre-registered in `bibliography/citations.yaml`
- LLMs cannot invent new citations
- Every citation links to external resources (Wikidata, DBpedia, DOI)

### 3. Bidirectional Validation

- TeX elements generate RDF resources
- RDF resources must be referenced in TeX
- Validators check consistency in both directions

### 4. SWTI Compliance

The infrastructure implements:
- **S1**: Standard RDF/OWL models for knowledge representation
- **S2**: External data links (Wikidata, DBpedia, DOI)
- **S6**: Semantic reasoning with SHACL constraints

---

## Directory Structure

```
schema/
├── README.md                          # This file
├── LLM_CONSTRAINTS.md                 # Explicit instructions for LLMs
├── thesis-structure.schema.yaml       # JSON Schema for thesis structure
├── tex-rdf-mapping.yaml              # Bidirectional mapping specification
└── validators/                        # Validation scripts
    ├── validate_tex_structure.py      # TeX structure validator
    ├── validate_citations.py          # Citation validator
    ├── validate_rdf.py                # RDF/SHACL validator
    └── validate_consistency.py        # Bidirectional consistency validator

bibliography/
└── citations.yaml                     # Master citation registry

ontology/
├── citations.jsonld                   # RDF citation model (BIBO ontology)
├── citation-usage.jsonld             # Citation provenance model
└── catty-thesis-shapes.shacl         # SHACL constraint shapes

thesis/
└── macros/
    └── citations.tex                  # TeX citation macros with validation

.github/workflows/
└── thesis-validation.yml             # CI/CD validation pipeline
```

---

## Components

### 1. Thesis Structure Schema (`thesis-structure.schema.yaml`)

Defines all valid thesis structures using JSON Schema:

- **Element types**: theorem, lemma, proposition, corollary, definition, example, remark, conjecture, proof
- **Hierarchy**: part → chapter → section → subsection
- **Required fields**: id, title, type-specific properties
- **ID patterns**: Enforces `[type]-[lowercase-hyphenated]` format
- **Nesting constraints**: Defines allowed parent-child relationships

**Validation**: Rejects any structure not explicitly defined in schema.

### 2. Citation Registry (`bibliography/citations.yaml`)

Master registry of approved citations:

```yaml
citations:
  girard1987linear:
    author: "Jean-Yves Girard"
    title: "Linear Logic"
    year: 1987
    type: journal
    doi: "10.1016/0304-3975(87)90045-4"
    wikidata: "Q56558858"
    dbpedia: "http://dbpedia.org/resource/Linear_logic"
    local_ontology: true
```

**Constraint**: Every citation must have author, title, year, and at least one external link.

### 3. RDF Citation Model (`ontology/citations.jsonld`)

JSON-LD representation using BIBO ontology:

```json
{
  "@id": "catty:citations/girard1987linear",
  "@type": "bibo:AcademicArticle",
  "dct:identifier": "girard1987linear",
  "dct:creator": {"foaf:name": "Jean-Yves Girard"},
  "dct:title": "Linear Logic",
  "dct:issued": "1987",
  "bibo:doi": "10.1016/0304-3975(87)90045-4",
  "owl:sameAs": [
    {"@id": "wd:Q56558858"},
    {"@id": "dbr:Linear_logic"}
  ]
}
```

**SWTI S2 Compliance**: Every citation links to Wikidata, DBpedia, or DOI.

### 4. Citation Provenance Model (`ontology/citation-usage.jsonld`)

Tracks which thesis elements cite which sources:

```json
{
  "@type": "prov:Attribution",
  "prov:entity": {"@id": "catty:thm-weakening"},
  "dct:references": {"@id": "catty:citations/girard1987linear"}
}
```

**ISO 690 Conformance**: Provenance follows ISO 690:2021 bibliographic standard.

### 5. TeX Citation Macros (`thesis/macros/citations.tex`)

LaTeX macros with built-in validation:

```latex
\cite{girard1987linear}           % Standard citation
\definedfrom{def-category}{maclane1971categories}  % Definition source
\provedfrom{thm-weakening}{girard1987linear}      % Theorem source
```

**Enforcement**: Macro package validates citation keys at compile time.

### 6. TeX-RDF Mapping (`schema/tex-rdf-mapping.yaml`)

Defines how TeX elements map to RDF classes:

```yaml
theorem:
  rdf_class: "catty:Theorem"
  tex_macro: "\\begin{theorem}[id={id}]{title}"
  properties:
    id: "dct:identifier"
    title: "dct:title"
    statement: "catty:hasStatement"
    proof: "catty:hasProof"
```

**Bidirectional**: Specifies forward (TeX→RDF) and reverse (RDF→TeX) validation rules.

### 7. SHACL Constraint Shapes (`ontology/catty-thesis-shapes.shacl`)

Semantic constraints for RDF validation:

```turtle
catty:TheoremShape
    a sh:NodeShape ;
    sh:targetClass catty:Theorem ;
    sh:property [
        sh:path dct:identifier ;
        sh:pattern "^thm-[a-z0-9]+(-[a-z0-9]+)*$" ;
    ] ;
    sh:property [
        sh:path catty:hasProof ;
        sh:minCount 1 ;
        sh:maxCount 1 ;
    ] .
```

**SWTI S6 Compliance**: Provides semantic reasoning through constraint validation.

### 8. Validators

Four Python validators enforce constraints:

#### `validate_tex_structure.py`
- Parses LaTeX files
- Checks ID patterns and uniqueness
- Validates citation keys exist in registry
- Checks environment nesting

#### `validate_citations.py`
- Validates TeX → YAML → RDF citation chain
- Checks external links (DOI, Wikidata, URLs)
- Verifies citation key format
- Validates metadata completeness

#### `validate_rdf.py`
- Loads all RDF files
- Runs SHACL validation
- Checks ID uniqueness in RDF
- Validates citation references

#### `validate_consistency.py`
- Parses both TeX and RDF
- Checks TeX elements have RDF resources
- Checks RDF resources referenced in TeX
- Validates element types match
- Verifies citation consistency

---

## Validation Workflow

### Local Validation

Run all validators:

```bash
# 1. Validate TeX structure
python schema/validators/validate_tex_structure.py \
  --tex-dir thesis/chapters/

# 2. Validate citations
python schema/validators/validate_citations.py \
  --tex-dir thesis/chapters/ \
  --bibliography bibliography/citations.yaml \
  --ontology ontology/citations.jsonld

# 3. Validate citations with external link checking
python schema/validators/validate_citations.py \
  --tex-dir thesis/chapters/ \
  --bibliography bibliography/citations.yaml \
  --ontology ontology/citations.jsonld \
  --check-external

# 4. Validate RDF/SHACL
python schema/validators/validate_rdf.py \
  --ontology ontology/ \
  --shapes ontology/catty-thesis-shapes.shacl

# 5. Validate bidirectional consistency
python schema/validators/validate_consistency.py \
  --tex-dir thesis/chapters/ \
  --ontology ontology/ \
  --bibliography bibliography/citations.yaml \
  --mapping schema/tex-rdf-mapping.yaml
```

### CI/CD Validation

GitHub Actions automatically runs all validators on every push/PR.

**Fail conditions** (PR rejected):
- New citations not in registry
- Undefined citation keys
- Broken external links
- RDF violations
- TeX ↔ RDF inconsistencies
- ID collisions

---

## Usage Guide

### For Human Authors

1. **Read `LLM_CONSTRAINTS.md`** - Understand all constraints
2. **Check citation registry** - Only cite registered sources
3. **Follow ID patterns** - Use lowercase hyphenated IDs
4. **Run validators locally** - Fix errors before pushing
5. **Add new citations carefully** - Include external links

### For LLMs

1. **MANDATORY**: Read `LLM_CONSTRAINTS.md` before generating content
2. **ONLY cite from registry** - No invented citations
3. **Follow patterns exactly** - No deviations
4. **STOP if blocked** - Request clarification rather than guessing
5. **Validation is fatal** - Errors will reject your output

### Adding New Citations

1. Add entry to `bibliography/citations.yaml`:
```yaml
author2025paper:
  author: "Author Name"
  title: "Paper Title"
  year: 2025
  type: journal
  doi: "10.xxxx/yyyy"
  wikidata: "Q12345678"
```

2. Add RDF resource to `ontology/citations.jsonld`:
```json
{
  "@id": "catty:citations/author2025paper",
  "@type": "bibo:AcademicArticle",
  "dct:identifier": "author2025paper",
  "dct:creator": {"foaf:name": "Author Name"},
  "dct:title": "Paper Title",
  "dct:issued": "2025",
  "bibo:doi": "10.xxxx/yyyy",
  "owl:sameAs": {"@id": "wd:Q12345678"}
}
```

3. Update `thesis/macros/citations.tex`:
```latex
\newcommand{\@validcitationkeys}{%
  % ...existing keys...
  author2025paper%
}
```

4. Run validators to confirm

---

## Examples

### Valid Theorem

```latex
\begin{theorem}[id=thm-soundness]
\label{thm-soundness}
\textbf{Soundness of Linear Logic}

The proof system for linear logic is sound with respect to
phase space semantics \cite{girard1987linear}.

\begin{proof}[id=proof-soundness]
We proceed by induction on the derivation height...
\qed
\end{proof}
\end{theorem}
```

**TeX → RDF**:
```json
{
  "@id": "catty:thm-soundness",
  "@type": "catty:Theorem",
  "dct:identifier": "thm-soundness",
  "dct:title": "Soundness of Linear Logic",
  "catty:hasStatement": "The proof system...",
  "catty:hasProof": {"@id": "catty:proof-soundness"},
  "dct:references": {"@id": "catty:citations/girard1987linear"}
}
```

### Valid Definition

```latex
\begin{definition}[id=def-category]
\label{def-category}
\textbf{Category} \definedfrom{def-category}{maclane1971categories}

A \emph{category} $\mathcal{C}$ consists of:
\begin{itemize}
  \item A class of objects $\text{Ob}(\mathcal{C})$
  \item For each pair of objects, a set of morphisms
  \item Composition and identity morphisms satisfying axioms
\end{itemize}
\end{definition}
```

**TeX → RDF**:
```json
{
  "@id": "catty:def-category",
  "@type": "catty:Definition",
  "dct:identifier": "def-category",
  "dct:title": "Category",
  "skos:prefLabel": "Category",
  "skos:definition": "A category consists of...",
  "prov:hadPrimarySource": {"@id": "catty:citations/maclane1971categories"}
}
```

### Invalid Examples (REJECTED)

❌ **Invented Citation**:
```latex
% FAIL: 'smith2025new' not in registry
Linear logic was extended \cite{smith2025new}.
```

❌ **Wrong ID Pattern**:
```latex
% FAIL: Uses uppercase and underscore
\begin{theorem}[id=THM_Soundness]
```

❌ **Duplicate ID**:
```latex
% FAIL: ID used twice
\label{thm-soundness}  % Chapter 1
\label{thm-soundness}  % Chapter 2
```

❌ **Missing Proof**:
```latex
% FAIL: Theorem without proof
\begin{theorem}[id=thm-incomplete]
Some claim.
\end{theorem}
```

---

## SWTI Compliance

This infrastructure implements SWTI criteria:

### S1: Standard Models for Web-based Data/Knowledge Representation ✅

- RDF/OWL schemas in `ontology/`
- JSON-LD and Turtle formats
- Standard vocabularies: RDFS, OWL, SKOS, BIBO, DCTERMS, PROV

### S2: Enhancement with External Data/Knowledge Resources ✅

- All citations link to Wikidata, DBpedia, or DOI
- `owl:sameAs` links for entity alignment
- External resource validation in CI/CD

### S6: Semantic Reasoner for Knowledge Processing ✅

- SHACL shapes provide constraint-based reasoning
- Consistency checking in CI/CD
- Inference rules for provenance

### S10: Open Source Code ✅

- AGPL-3.0 license
- Public GitHub repository
- Community contribution guidelines

---

## Troubleshooting

### Validation Error: "Citation key not found"

**Fix**: Add citation to `bibliography/citations.yaml` and `ontology/citations.jsonld`

### Validation Error: "ID pattern mismatch"

**Fix**: Change ID to match pattern (e.g., `thm-my-theorem` not `THM_MyTheorem`)

### Validation Error: "Duplicate ID"

**Fix**: Ensure all IDs are globally unique across all files

### Validation Error: "RDF resource not found"

**Fix**: Generate RDF resource corresponding to TeX element

---

## Dependencies

### Python Dependencies

```bash
pip install pyyaml rdflib pyshacl requests jsonschema
```

### LaTeX Dependencies

```latex
\usepackage{xparse}
\usepackage{xstring}
\usepackage{etoolbox}
```

---

## Contributing

Before submitting thesis content:

1. Read `LLM_CONSTRAINTS.md`
2. Run all validators locally
3. Fix all errors (validation failures are fatal)
4. Ensure all citations are registered
5. Check IDs are unique and match patterns

---

## License

AGPL-3.0 (see LICENSE file)

---

## Contact

For questions about the infrastructure, open an issue on GitHub.

**END OF SCHEMA README**
