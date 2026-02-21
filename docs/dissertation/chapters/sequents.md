---
title: "Anatomy of Sequent Calculus"
abbrev-title: "Sequent Calculus"
msc-primary: "03F05"
msc-secondary: ["03B47", "03B20"]
keywords: [sequent calculus, proof theory, structural rules, cut elimination]
---

# Anatomy of Sequent Calculus {#sec-sequents}

## Reflexive Axiom Schemata {#subsec-reflexive-axiom}
Several reflexive axiom schemata exist, and the literature tends to abuse notations.
$$\Gamma, A \vdash A, \Delta$$ corresponds with certain symmetric inference calculi notably LK.
$$\Gamma, A \vdash A$$ corresponds with intuitionistic calculi like LJ.
$$A \vdash A, \Delta$$ corresponds with dual intuitionistic calculi like LDJ.
$$A \vdash A$$ corresponds with the intersection of intuitionistic and dual intuitionistic calculi; it shows affinity with logics defined by the highly restrictive set of connectives {conjunction, disjunction}.

Additional nuances exist regarding the definition of $\Gamma$ and $\Delta$ in specific substructural and structural restrictions; in general, they do not have the same meaning across all sequent calculi. The difference between classical linear logic and classical logic is a particular case study where they both are often notated as $$\Gamma, A \vdash A, \Delta$$ but they have different semantics due to the way that each part of the axiom schemata is explicitly defined given the structural rules, rules of inference, and unit rules.

The matrix of reflexive axiom schemata determines what structures and operations 
a logic can prove. If a logic proves every reflexive axiom scheme of a given form, 
it will have specific structures or operations and a characterizing cut metarule. 
If a logic does not have corresponding structures and operations, then either 
they're implicit or the logic is consistently characterized with respect to the 
reflexive axiom scheme.

## Cut Rules and Sub-Rule Preservation {#subsec-cut-rules}

A fundamental characteristic of subclassical calculi is that they preserve cut rules such that none contradict the cut rule of LK; this applies for structural calculi like LJ and LDJ as well as substructural calculi like linear logics, affine logics, relevant logics, and order or non-commucative logics. They are all sub-rules of the LK cut rule; classical linear logic and its non-commutative variants are case studies in that their cut rules make clear certain ambiguities about in-the-middle cuts that are valid in LK but which are not valid in these subcalculi; LJ is an example of asymmetric cut rules with LDJ having a dual asymmetric cut rule. The degree of granularity requires examining each component:

- Cut elimination procedures
- Cut as a meta-rule
- Restrictions on cut applicability
- Relationship between cut and other structural rules

The preservation of cut rules determines the computational complexity and 
proof-theoretic properties of each substructural logic.

## Structural Rules: The Logical Parameters {#subsec-structural-rules}

Structural rules serve as the primary parameters that distinguish subclassical calculi:

### Weakening Rules

- **Left weakening**: $\frac{\Gamma \vdash \Delta}{\Gamma, A \vdash \Delta}$
- **Right weakening**: $\frac{\Gamma \vdash \Delta}{\Gamma \vdash \Delta, A}$
- Their presence or absence fundamentally alters the logic's character

### Contraction Rules

- **Left contraction**: $\frac{\Gamma, A, A \vdash \Delta}{\Gamma, A \vdash \Delta}$
- **Right contraction**: $\frac{\Gamma \vdash \Delta, A, A}{\Gamma \vdash \Delta, A}$
- Critical for resource-sensitive reasoning and the explosion principle

### Exchange Rules

- Permutation of antecedent formulas
- Permutation of succedent formulas
- Essential for non-commutative logics

The matrix or vectorization of logical relations between units, axioms, rules, 
and operations is a key result that allows automatic construction of various 
categorizable logics in relation to each other; this is fundamentally based in the functional incompleteness of proper subclassical calculi.

## Operational Rules {#subsec-operational-rules}

The operational rules define connectives and their introduction/elimination:

### Multiplicative Connectives

- Tensor ($\otimes$) and par ($\parr$)
- Their structural rules and interactions
- Relationship to linear logic's resource semantics

### Additive Connectives

- Conjunction ($\with$) and disjunction ($\oplus$)
- Additive vs. multiplicative behavior
- Proof-theoretic significance

### Exponential Modality

- Of course ($!$) and why not ($?$)
- Resource management in linear logic
- Relationship to structural rules

## Functional Incompleteness and Classical Results {#subsec-functional-incompleteness}

The substructural calculi exhibit classical functional incompleteness, which 
is related to:

- The inability to derive certain classical tautologies
- Independence results between different logical principles
- The relationship between syntax and semantics in restricted systems

This functional incompleteness is not a deficiency but a feature that enables 
logical pluralism Catty advocates.

## From LK to Substructural Calculi: The Restriction Matrix {#subsec-restriction-matrix}

Every valid substructural calculus can be characterized by its restriction 
matrix relative to LK:

| Logic | Weakening | Contraction | Exchange | Cut Rule |
|-------|-----------|-------------|----------|----------|
| LK | Yes | Yes | Yes | Full |
| LJ | Yes | Yes | Yes | Restricted |
| LL | No | No | Yes | Linear |
| LM | No | No | Yes | Minimal |
| LDJ | Yes | Yes | Yes | Dual |

This matrix determines the computational and proof-theoretic properties of 
each logic. The existence of valid substructural calculi depends on which 
combinations of restrictions preserve consistency and meaningful proof theory.

## Dualization and Asymmetry {#subsec-dualization}

The asymmetry between left and right structural rules (e.g., LJ vs LDJ) produces 
distinct logical behaviors:

- **LJ**: Trivializes structural rules on the right (single conclusion)
- **LDJ**: Trivializes structural rules on the left (single antecedent)
- This asymmetry is fundamental to understanding the logic space

Understanding these asymmetries is crucial for constructing the full categorical 
model of substructural logics and their relationships.
