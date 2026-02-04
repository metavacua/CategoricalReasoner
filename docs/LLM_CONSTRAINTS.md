> [!IMPORTANT]
> This file has been consolidated into the "Architecture of Catty" TeX document and is marked for future removal.

# LLM Constraints for Catty Thesis Generation

## Overview

This document provides explicit instructions for LLMs generating thesis content for the Catty project. All generated content must comply with these constraints to pass automated validation.

## Citation Constraints (CRITICAL)

### You MAY ONLY Cite Pre-Registered Sources

**ABSOLUTE RULE:** You may only use `\cite{key}` where `key` exists in `docs/dissertation/bibliography/citations.yaml`.

### Citation Registry

The following citation keys are available (as of version 1.0):

- `girard1987linear` - Girard, J.-Y. (1987). Linear Logic
- `kripke1965semantical` - Kripke, S.A. (1965). Semantical Analysis of Intuitionistic Logic I
- `sambin2003basic` - Sambin, G. (2003). Basic Logic: Reflection, Symmetry, Visibility
- `urbas1993structural` - Urbas, J. (1993). On the Structural Rules of Linear Logic
- `trafford2018category` - Trafford, J. (2018). A Category Theory Approach to Conceptual Modelling
- `lawvere1963functorial` - Lawvere, F.W. (1963). Functorial Semantics of Algebraic Theories
- `maclane1971categories` - Mac Lane, S. (1971). Categories for the Working Mathematician
- `lambek1988category` - Lambek, J. (1988). Categories and Categorical Grammars
- `restall2000substructural` - Restall, G. (2000). Substructural Logics
- `pierce1991category` - Pierce, B.C. (1991). Basic Category Theory for Computer Scientists
- `curyhoward1934` - Curry, H.B. (1934). Functionality in Combinatory Logic
- `howard1969formulae` - Howard, W.A. (1969). The Formulae-as-Types Notion of Construction
- `negri2011proof` - Negri, S. (2011). Proof Analysis: A Contribution to Hilbert's Problem

### To Cite a Source

Use the appropriate macro:

- Simple citation: `\cite{key}`
- Citation with page: `\citepage{key}{42}`
- Citation with figure reference: `\citefigure{key}{Figure 1}`
- Definition with provenance: `\definedfrom{structural rule}{urbas1993structural}`
- Theorem with provenance: `\provedfrom{Main Theorem}{sambin2003basic}`

### If You Need to Cite a Source NOT in Registry

**STOP IMMEDIATELY.** Do not invent new citation keys.

Instead, report the missing citation:

```
MISSING CITATION: I need to cite [paper title] by [author] ([year]).
Please add this to docs/dissertation/bibliography/citations.yaml with key [suggested-key].
```

Do not proceed until the citation is added to the registry.

## Content Structure Constraints

### Theorems

Every theorem must have:
- Unique ID matching pattern `thm-[lowercase-hyphenated]`
- Title
- Statement
- Exactly one proof reference

Example:
```latex
\begin{theorem}[thm-weakening]{Weakening}
  If the weakening rule holds, then...
\end{theorem}
```

### Definitions

Every definition must have:
- Unique ID matching pattern `def-[lowercase-hyphenated]`
- Term being defined
- Meaning/definition

Example:
```latex
\begin{definition}[def-structural-rule]{Structural Rule}
  A structural rule is...
\end{definition}
```

### Lemmas

Every lemma must have:
- Unique ID matching pattern `lem-[lowercase-hyphenated]`
- Title
- Statement
- Exactly one proof reference

Example:
```latex
\begin{lemma}[lem-lattice-order]{Lattice Order}
  The lattice order satisfies...
\end{lemma}
```

### Examples

Every example must have:
- Unique ID matching pattern `ex-[lowercase-hyphenated]`
- Title
- Description
- Instantiation

Example:
```latex
\begin{example}[ex-linear-logic]{Linear Logic Example}
  Consider linear logic as...
\end{example}
```

### Sections

Every section must have:
- Unique ID matching pattern `sec-[lowercase-hyphenated]`
- Title

Example:
```latex
\section{Categorical Foundation}
  The categorical foundation...
```

## ID Uniqueness Constraints

**ABSOLUTE RULE:** All IDs must be globally unique across the entire thesis corpus.

ID patterns by type:
- Theorems: `thm-[name]` (e.g., `thm-weakening`)
- Definitions: `def-[name]` (e.g., `def-structural-rule`)
- Lemmas: `lem-[name]` (e.g., `lem-lattice-order`)
- Examples: `ex-[name]` (e.g., `ex-linear-logic`)
- Sections: `sec-[name]` (e.g., `sec-categorical-foundation`)
- Subsections: `subsec-[name]` (e.g., `subsec-morphisms`)

**DO NOT REUSE IDs.** If you detect a potential collision:
```
ID COLLISION: Proposed ID 'thm-weakening' may already exist.
Please check existing IDs and choose a unique identifier.
```

## RDF Constraints

### Do NOT Create New RDF Classes

You may only instantiate existing RDF classes defined in:
- `src/ontology/catty-categorical-schema.jsonld`
- `src/ontology/citation-usage.jsonld`

Do not define new `owl:Class` or `owl:ObjectProperty` resources.

### RDF Must Match TeX

Every TeX element with an ID must have a corresponding RDF resource with the same `dct:identifier`.

For example:
- TeX: `\begin{theorem}[thm-weakening]{Weakening}`
- RDF: `<http://catty.org/theorem/thm-weakening> dct:identifier "thm-weakening"`

### Bidirectional Consistency

All citations in TeX must have corresponding RDF provenance links:
- TeX: `\cite{girard1987linear}`
- RDF: `catty:thm-weakening dct:references catty:girard1987linear`

## Validation Requirements

Your output will be automatically validated. Validation failures are fatal.

### Validation Checks

1. **TeX Structure Validator**
   - Parses LaTeX source
   - Validates ID patterns
   - Checks ID uniqueness
   - Validates required fields

2. **Citation Validator**
   - Checks all `\cite{key}` exist in registry
   - Validates YAML ↔ RDF consistency
   - Validates external links (DOI, URLs, Wikidata)

3. **RDF/SHACL Validator**
   - Validates RDF against shapes
   - Checks required properties
   - Validates ID patterns

4. **Consistency Validator**
   - Checks TeX ↔ RDF bidirectional consistency
   - Validates citation provenance links
   - Checks property matching

### Validation Errors

If validation fails, you will receive specific error messages like:

```
ERROR: thesis/chapters/categorical-semantic-audit.tex:42
  Citation 'girard1987linear' not found in docs/dissertation/bibliography/citations.yaml
```

Fix the error by:
1. Reading the error message carefully
2. Locating the problematic line in the specified file
3. Fixing the specific issue (e.g., using correct citation key)
4. Re-running validation

## What NOT to Do

### ❌ DO NOT:
- Invent new citation keys
- Reuse existing IDs
- Create new RDF classes or properties
- Skip required fields (e.g., theorem without proof)
- Use citation keys not in the registry
- Mix ID patterns (e.g., using `thm-` prefix for definitions)
- Define theorems without proofs
- Create definitions without terms and meanings

### ✅ DO:
- Use only pre-registered citation keys
- Check ID uniqueness before using
- Instantiate only existing RDF classes
- Include all required fields
- Match ID patterns correctly
- Create bidirectional TeX ↔ RDF links
- Follow the schema in `src/schema/thesis-structure.schema.yaml`

## Example Valid Thesis Fragment

```latex
\section{Linear Logic}

We define linear logic following \cite{girard1987linear}.

\begin{definition}[def-linear-logic]{Linear Logic}
  Linear logic is a substructural logic that...
\end{definition}

\begin{theorem}[thm-weakening-fail]{Weakening Fails}
  The weakening rule does not hold in linear logic \cite{girard1987linear}.
\end{theorem}

\begin{example}[ex-linear-sequent]{Linear Sequent}
  The sequent $A \otimes B \vdash C$ is valid in...
\end{example}
```

This fragment:
- Uses only pre-registered citations
- Has unique IDs following correct patterns
- Includes all required fields
- Can be validated successfully

## Emergency Procedure

If you encounter a situation not covered by these constraints:

1. **STOP** - Do not proceed
2. **REPORT** - Clearly describe the issue
3. **WAIT** - Await instructions on how to proceed

Example emergency report:
```
CONSTRAINT VIOLATION: I need to cite a paper by [author] ([year])
titled "[title]" that is not in the registry.
This is required to complete [specific section].
```

## Summary

- **Only cite pre-registered sources** (from `docs/dissertation/bibliography/citations.yaml`)
- **All IDs must be unique** and follow correct patterns
- **TeX ↔ RDF must be consistent** (bidirectional)
- **Your output will be validated** - failures are fatal
- **STOP and report** if you cannot comply with constraints
