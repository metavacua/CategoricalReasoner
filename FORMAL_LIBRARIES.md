# Formal Libraries Manifest

This document tracks the external formal libraries integrated into the Catty repository.

## 1. ULO (Universal Logic Ontology)
- **Repository**: `https://gl.mathhub.info/ulo/ulo`
- **License**: CC-SAv4 (Note: We treat this as mathematical reference/AGPLv3 compatible for code generation purposes)
- **Description**: Upper-level ontology for logic.
- **Size**: ~203 KB
- **Structure**:
  - `ulo.owl`: Main ontology file.
  - `classes/`, `objectproperties/`, `dataproperties/`: Decomposed ontology terms.

## 2. Isabelle Distribution
- **Repository**: `https://gl.mathhub.info/Isabelle/Distribution`
- **License**: BSD-3-Clause
- **Description**: Standard library for Isabelle/HOL.
- **Size**: ~170 MB (MathHub mirror)
- **Structure**:
  - `source/`: Contains the actual theory files (`HOL`, `Pure`, `Sequents`, etc).
  - `content/`, `narration/`: MathHub specific metadata.

## 3. Isabelle AFP (Archive of Formal Proofs) - Selected Subset
- **Repository**: `https://gl.mathhub.info/Isabelle/AFP`
- **License**: Various (mostly BSD/LGPL) per entry.
- **Size**: ~600 MB (Full shallow clone), ~50 MB (Selected subset estimated)
- **Integration Strategy**: Git submodule with sparse-checkout (if supported) or documented subset.

### Included AFP Subdirectories
We focus on entries related to Category Theory, Logics, and Constructive Mathematics.

| Subdirectory | Relevance |
|--------------|-----------|
| `Category` | Basic Category Theory |
| `Category2` | Category Theory extensions |
| `Category3` | Category Theory extensions |
| `MonoidalCategory` | Monoidal Categories |
| `Bicategory` | Bicategories |
| `AxiomaticCategoryTheory` | Axiomatic approach |
| `Constructive_Cryptography` | Constructive reasoning examples |
| `Abstract-Hoare-Logics` | Logic formalisms |
| `SequentInvertibility` | Sequent calculus properties |
| `Relation_Algebra` | Relational logic foundations |
| `Lifting_Package` | (If present in AFP, otherwise part of Dist) |

### Excluded
- Most applied computer science formalizations (e.g., `Jinja`, `IP_Tables`, `C_Parser`).
- Number theory and analysis (unless directly categorical).
- Hardware verification examples.

## Integration Notes
- All libraries are added as git submodules in `formal-libraries/`.
- AFP is large; we recommend using `git submodule update --init --recursive --depth 1` where possible, or configuring sparse-checkout to limit disk usage.

## Fair Use Rationale
The integration of these formal libraries relies on the principle that mathematical facts and structures are not copyrightable. We use these libraries as a reference corpus for:
1.  **Introspection**: Generating code structures (Java/Python classes) that mirror the formal definitions.
2.  **Validation**: Verifying that generated artifacts align with established formalisms.

We do not claim ownership of these libraries, and their use is intended to be transformative (enabling categorical reasoning over them) rather than a mere redistribution.
