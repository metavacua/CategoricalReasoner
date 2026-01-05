# LLM Constraints for Catty Thesis Development

**CRITICAL INSTRUCTIONS FOR LANGUAGE MODELS GENERATING THESIS CONTENT**

This document provides explicit constraints and instructions to prevent LLM hallucination, citation invention, and structural violations when generating content for the Catty categorical reasoner thesis.

---

## 1. Citation Constraints

### ✅ ALLOWED: Cite only registered sources

You may **ONLY** cite sources from `bibliography/citations.yaml`. The approved citation keys are:

```
girard1987linear
kripke1965semantical
sambin2003basic
lawvere1963functorial
maclane1971categories
prawitz1965natural
troelstra2000basic
abramsky1994proofs
seely1989linear
jacobs1999categorical
lambek1988introduction
barr1990category
urban2015categorical
trafford2020categorical
curry1958combinatory
howard1980formulae
```

### ❌ FORBIDDEN: Inventing new citations

- **DO NOT** create citation keys that are not in the registry
- **DO NOT** cite papers you "think" might exist
- **DO NOT** guess at citation formats

### ✅ CORRECT USAGE:

```latex
Linear logic was introduced by Girard \cite{girard1987linear}.
```

### ❌ INCORRECT USAGE:

```latex
% WRONG - this key does not exist in registry
Linear logic was extended by Smith \cite{smith1995extended}.
```

### 🛑 REQUIRED ACTION: If you need to cite a missing source

**STOP** and output:

```
CITATION_REQUEST: I need to cite [Author Year Title] but the key
'[proposed-key]' is not in bibliography/citations.yaml.
Please add this citation to the registry before I proceed.
```

**DO NOT** proceed with content generation until the citation is added.

---

## 2. ID Pattern Constraints

All IDs must follow strict patterns. **DO NOT** deviate from these patterns.

### Required ID Patterns:

| Element Type | Pattern | Example |
|--------------|---------|---------|
| Theorem | `thm-[lowercase-hyphenated]` | `thm-weakening` |
| Lemma | `lem-[lowercase-hyphenated]` | `lem-substitution` |
| Proposition | `prop-[lowercase-hyphenated]` | `prop-commutativity` |
| Corollary | `cor-[lowercase-hyphenated]` | `cor-transitivity` |
| Definition | `def-[lowercase-hyphenated]` | `def-category` |
| Example | `ex-[lowercase-hyphenated]` | `ex-natural-numbers` |
| Remark | `rem-[lowercase-hyphenated]` | `rem-historical-note` |
| Conjecture | `conj-[lowercase-hyphenated]` | `conj-completeness` |
| Proof | `proof-[lowercase-hyphenated]` | `proof-weakening` |
| Section | `sec-[lowercase-hyphenated]` | `sec-linear-logic` |
| Subsection | `subsec-[lowercase-hyphenated]` | `subsec-introduction` |
| Chapter | `ch-[lowercase-hyphenated]` | `ch-foundations` |
| Part | `part-[lowercase-hyphenated]` | `part-theoretical-framework` |

### ✅ CORRECT:

```latex
\begin{theorem}[id=thm-curry-howard]
\label{thm-curry-howard}
```

### ❌ INCORRECT:

```latex
% WRONG - uses capital letters and underscores
\begin{theorem}[id=THM_Curry_Howard]
\label{THM_Curry_Howard}
```

---

## 3. Global ID Uniqueness

**CRITICAL**: All IDs must be globally unique across the entire thesis.

### ✅ ALLOWED:

```latex
% In chapter1.tex
\begin{theorem}[id=thm-associativity]
```

### ❌ FORBIDDEN:

```latex
% In chapter1.tex
\begin{theorem}[id=thm-associativity]

% In chapter2.tex (DUPLICATE - FORBIDDEN)
\begin{lemma}[id=thm-associativity]
```

### 🛑 VALIDATOR WILL REJECT: Duplicate IDs

The validation system checks for duplicate IDs and will **FAIL** the build if any are found.

---

## 4. Structural Constraints

### Nesting Rules:

#### ✅ ALLOWED:

- Chapter contains sections
- Section contains subsections
- Section contains theorems, definitions, examples, remarks
- Theorem contains exactly one proof
- Subsection contains theorems, definitions, examples

#### ❌ FORBIDDEN:

- Proof cannot contain theorem
- Proof cannot contain definition
- Subsection cannot contain subsection
- Definition cannot contain theorem
- Proof cannot be nested inside proof

### ✅ CORRECT:

```latex
\begin{theorem}[id=thm-soundness]
The system is sound.
\begin{proof}[id=proof-soundness]
...proof steps...
\end{proof}
\end{theorem}
```

### ❌ INCORRECT:

```latex
% WRONG - proof inside proof
\begin{proof}[id=proof-outer]
Step 1...
\begin{proof}[id=proof-inner]  % FORBIDDEN
Step 2...
\end{proof}
\end{proof}
```

---

## 5. Required Theorem Components

Every theorem **MUST** have:

1. **Unique ID** (matching pattern `thm-*`)
2. **Title**
3. **Statement** (what is being claimed)
4. **Exactly one proof** (with ID matching `proof-*`)

### ✅ CORRECT:

```latex
\begin{theorem}[id=thm-weakening]
\label{thm-weakening}
\textbf{Weakening Property}

If $\Gamma \vdash A$, then $\Gamma, B \vdash A$.

\begin{proof}[id=proof-weakening]
We proceed by induction on the derivation...
\end{proof}
\end{theorem}
```

### ❌ INCORRECT:

```latex
% WRONG - missing proof
\begin{theorem}[id=thm-no-proof]
Some claim.
\end{theorem}
```

---

## 6. Required Definition Components

Every definition **MUST** have:

1. **Unique ID** (matching pattern `def-*`)
2. **Title**
3. **Term** (what is being defined)
4. **Meaning** (the definition)

### ✅ CORRECT:

```latex
\begin{definition}[id=def-functor]
\label{def-functor}
\textbf{Functor}

A \emph{functor} $F: \mathcal{C} \to \mathcal{D}$ between categories
$\mathcal{C}$ and $\mathcal{D}$ is a mapping that preserves structure...
\end{definition}
```

---

## 7. Citation Provenance Macros

Use specialized macros to link definitions and theorems to their sources:

### For Definitions:

```latex
\definedfrom{def-linear-logic}{girard1987linear}
```

This generates an RDF provenance link: `catty:def-linear-logic dct:references catty:citations/girard1987linear`

### For Theorems:

```latex
\provedfrom{thm-cut-elimination}{girard1987linear}
```

### For Sections:

```latex
\derivedfrom{sec-category-theory}{maclane1971categories}
```

---

## 8. Validation Enforcement

Your output will be **automatically validated** by:

1. **TeX Structure Validator**: Checks IDs, patterns, citations, nesting
2. **Citation Validator**: Ensures all cited keys exist in registry
3. **RDF/SHACL Validator**: Validates RDF resources against constraints
4. **Consistency Validator**: Checks TeX ↔ RDF bidirectional consistency

### Validation Failures Are Fatal

If any validator fails:
- The CI/CD pipeline will **REJECT** your content
- You will receive specific error messages with file names and line numbers
- You **MUST** fix the errors before proceeding

---

## 9. What To Do When Blocked

### Scenario 1: Need to cite a source not in registry

**Output:**
```
CITATION_REQUEST: I need to cite [Author Year "Title"] for [reason].
Proposed key: [lastname][year][shortword]
DOI/URL: [if available]
Wikidata ID: [if known]

BLOCKING: Cannot proceed without this citation in registry.
```

### Scenario 2: Unsure about ID naming

**Output:**
```
ID_CLARIFICATION_REQUEST: I need to create a [theorem/definition/section]
about [topic]. What should the ID be?
Proposed ID: [your-suggestion]
```

### Scenario 3: Structural ambiguity

**Output:**
```
STRUCTURE_CLARIFICATION: Should [element X] be nested inside [element Y]?
The schema allows [options A, B, C].
```

---

## 10. Examples of Valid Thesis Fragments

### Complete Theorem Example:

```latex
\begin{theorem}[id=thm-curry-howard]
\label{thm-curry-howard}
\textbf{Curry-Howard Correspondence}

There is a direct correspondence between proofs in intuitionistic logic
and programs in simply-typed lambda calculus \cite{howard1980formulae}.

\begin{proof}[id=proof-curry-howard]
We establish the correspondence by induction on the structure of proofs:
\begin{enumerate}
    \item Implication introduction corresponds to lambda abstraction
    \item Implication elimination corresponds to function application
    \item ... (remaining cases)
\end{enumerate}
Therefore, the correspondence holds. \qed
\end{proof}
\end{theorem}
```

### Complete Definition Example:

```latex
\begin{definition}[id=def-monoidal-category]
\label{def-monoidal-category}
\textbf{Monoidal Category} \definedfrom{def-monoidal-category}{maclane1971categories}

A \emph{monoidal category} is a category $\mathcal{C}$ equipped with:
\begin{itemize}
    \item A bifunctor $\otimes: \mathcal{C} \times \mathcal{C} \to \mathcal{C}$
    \item A unit object $I \in \mathcal{C}$
    \item Natural isomorphisms $\alpha$, $\lambda$, $\rho$ satisfying coherence conditions
\end{itemize}
\end{definition}
```

---

## 11. Summary: The Three Golden Rules

1. **ONLY cite from the registry** - no invented citations
2. **Follow ID patterns exactly** - all lowercase, hyphenated
3. **IDs must be globally unique** - no duplicates anywhere

**Your content WILL be validated. Violations WILL cause failures.**

---

## 12. Getting Help

If you encounter any situation not covered by this document:

1. **STOP** generating content
2. **OUTPUT** a clear description of the ambiguity or blocker
3. **REQUEST** clarification before proceeding

It is **ALWAYS better to ask** than to guess and generate invalid content.

---

**END OF LLM CONSTRAINTS DOCUMENT**
