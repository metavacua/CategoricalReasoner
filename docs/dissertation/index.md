---
title: "Theoretical Metalinguistics"
author: "Ian Douglas Lawrence Norman McLean"
date: "2025-12-29"
msc-primary: "03B22"
msc-secondary: ["03B47", "03G30", "18D15", "18D20", "68N18"]
keywords: [categorical logic, logical pluralism, sequent calculus, proof theory, type theory]
abstract: |
  This work presents Catty, a formal investigation of categorical foundations 
  for logics and their morphisms. Rather than following a conventional linear 
  progression, this thesis serves as a review of the literature and a foundational 
  common reference for understanding logics as coordinate objects in a categorical 
  structure.
  
  The proliferation of formal logics demands a framework that captures their 
  structural relationships without imposing a rigid hierarchy. Catty adopts a 
  position of logical pluralism, where different logics are seen as distinct 
  computational universes, each with its own valid internal reasoning.
  
  By identifying initial logics as categorical foundations, we span the space 
  of categorizable logics through formal morphisms that preserve or restrict 
  logical properties. The framework provides computational witnesses through 
  executable Domain-Specific Languages (DSLs), commutative diagrams, and formal 
  ontologies. In this framework, syntactic minimality enables greater semantic 
  power.
---

# Preface {.unnumbered}

## About This Work {.unnumbered}

This document serves as both a traditional dissertation and a living reference 
for the Catty categorical logic framework. It is organized thematically rather 
than chronologically, allowing readers to explore specific aspects of the theory 
without requiring a linear progression.

## Structure {.unnumbered}

The reference is organized into themes that explore the genesis, architecture, 
and formalization of the logic category:

**Foundations**
: Establishes the conceptual and mathematical groundwork, including the 
  motivation for logical pluralism and the sequent calculus foundations.

**Architecture and Structure**
: Explores the geometry of the 2D lattice, morphisms between logics, and 
  logics as computational universes.

**Formalization**
: Presents machine-readable ontologies, categorical semantic audits, and 
  integration with the broader semantic web.

**Philosophy and Context**
: Examines the overarching implications of logical pluralism and the 
  inversion principle.

**Methodology**
: Documents the SPARQL validation protocols, semantic web research 
  methodology, and extracted formal content.

## Mathematical Prerequisites {.unnumbered}

Readers should be familiar with:

- Basic category theory (categories, functors, natural transformations)
- Proof theory and sequent calculus
- Type theory and the Curry-Howard correspondence

For those less familiar with these areas, the Genesis chapter provides 
foundational references.

## Notation Conventions {.unnumbered}

This work uses standard mathematical notation:

- Sequents: $\Gamma \seq \Delta$ (turnstile)
- Linear implication: $A \loll B$ (lollipop)
- Tensor product: $A \tensor B$
- Categories: $\Set$, $\Cat$, $\mathbf{Type}$

See the introductory chapters for complete notation tables.

## Computational Artifacts {.unnumbered}

This thesis is accompanied by:

- **Source Code**: Available at <https://github.com/metavacua/CategoricalReasoner>
- **Ontologies**: RDF/OWL formalizations of logical structures
- **Benchmarks**: Performance evaluations and query collections
- **Executable DSL**: The Catty implementation for categorical reasoning

## Research Data Management {.unnumbered}

This work follows OAIS-aligned research data management practices. See 
[RO-Crate Documentation](RO-CRATE.md) for information on research data 
packaging and preservation.

---

**Mathematics Subject Classification (2020)**  
Primary: 03B22 (Abstract deductive systems)  
Secondary: 03B47 (Substructural logics), 03G30 (Categorical logic), 
18D15 (Closed categories), 18D20 (Enriched categories), 
68N18 (Functional programming)
