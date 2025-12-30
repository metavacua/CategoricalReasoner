# Catty Ontology: Categorical Semantic Model

This directory contains the RDF/OWL schemas and knowledge graph data for Catty's category-theoretic model of formal logics.

## Overview

Catty models logics as objects in a category with morphisms representing extension and interpretation relationships. The ontology provides:

1. **Categorical Schema**: Core classes for category theory and formal logic (Logical Signature, Axioms, LHS/RHS Structural Rules, etc.)
2. **Logics-as-Objects**: Formal logics (LM, LK, LJ, LDJ, LL, etc.) represented as categorical objects with logical signatures and axioms.
3. **Morphism Catalog**: Categorical relationships between logics (Extension, Interpretation, Adjunction).
4. **Logic Category Structure**: The categorical structure emerging from rule and axiom configurations.
5. **Curry-Howard Model**: The categorical equivalence between logic and type theory.

## File Structure

### Core Schema

- **`catty-categorical-schema.jsonld`**: The complete RDF/OWL schema defining:
  - Category theory primitives (Category, Object, Morphism, Functor, Natural Transformation)
  - Logic-specific classes (Logic, LogicalTheory, LogicalSignature, LogicalAxiom, TheoreticalAxiom)
  - LHS/RHS Structural rules (Weakening, Contraction, Exchange)
  - Morphism types (Extension, Interpretation)
  - Curry-Howard equivalence classes

### Knowledge Graph Data

- **`logics-as-objects.jsonld`**: Instance data for logics as categorical objects:
  - LM (minimal common sublogic for LJ and LDJ)
  - LK (terminal classical logic)
  - LJ (intuitionistic logic)
  - LDJ (dual intuitionistic logic)
  - LL (linear logic)
  - ALL (affine linear logic)
  - RLL (relevant linear logic)

- **`morphism-catalog.jsonld`**: Morphisms between logics:
  - Extensions (LM → LJ, LM → LDJ, LJ → LK, LDJ → LK, LL → ALL → LK, LL → RLL → LK)
  - Interpretations (LK → LJ via double negation)
  - Adjoint functor pairs (LK ↔ LJ)

- **`two-d-lattice-category.jsonld`**: The structural organization of the logic category based on rule configurations.

- **`curry-howard-categorical-model.jsonld`**: The Curry-Howard correspondence:
  - Equivalence of categories (Logic ↔ Type Theory)
  - Curry-Howard functor and its inverse
  - Categorical semantics (CCCs, *-autonomous categories)

## Categorical Model

### Objects (Logics)

Logics are characterized by their logical signature and axioms. LK is the terminal logic in this framework.

```
       LM (Initial Base)
      /  \
     LJ   LDJ
      \  /
       LK (Terminal)
```

### Substructural Extensions

LL is the resource-sensitive core. Adding structural rules leads to LK.

```
       LL
      /  \
    ALL  RLL
      \  /
       LK
```

### Morphisms

A morphism A → B represents that B extends A (Extension) or that A is interpretable in B (Interpretation).

- **LL → ALL**: Adds weakening (allows erasure, maintains 'no cloning').
- **LL → RLL**: Adds contraction (allows cloning, maintains 'no erasure').
- **ALL/RLL → LK**: Adding the remaining structural rules results in Classical Logic.
- **LJ/LDJ → LK**: Proper extension to the terminal classical system.

### License

This ontology is licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**. External resources referenced have varying licenses:

- **Wikidata**: CC0 (public domain) - Fully compatible
- **DBPedia**: CC BY-SA 3.0 - Compatible with attribution
- **OpenMath**: BSD 3-Clause - Fully compatible
- **nLab**: CC BY-SA 3.0 - Compatible with attribution
- **Lean MathLib**: Apache 2.0 - Fully compatible
- **Coq**: CeCILL-B (BSD-compatible) - Fully compatible

## References

- Curry, H. B., & Howard, W. A. (1980). "Formulae-as-Types Notion of Control"
- Gentzen, G. (1935). "Untersuchungen über das logische Schließen"
- Girard, J.-Y. (1987). "Linear Logic"
- Lambek, J., & Scott, P. J. (1986). "Introduction to Higher Order Categorical Logic"
- nLab: https://ncatlab.org/nlab/
