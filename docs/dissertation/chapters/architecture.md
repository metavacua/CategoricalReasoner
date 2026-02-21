---
title: "Architecture"
abbrev-title: "Architecture"
msc-primary: "03G30"
msc-secondary: ["18D15", "18D20", "03B22"]
keywords: [categorical logic, lattice, architecture, 2D logic space]
---

# Architecture {#sec-architecture}

## The Geometry of the Logic Lattice {#subsec-lattice-geometry}

The Catty framework organizes logics within a parameter space defined by the logical signature and theoretical signature of sequent calculi formalizations, 
where each logic occupies a position determined by its structural properties.

## Modular Construction {#subsec-modular}

Logics are constructed modularly from basic components:

1. **Base logic**: The initial logic; a non-trivial logic. Current candidate is a propositional logic of conjunction and disjunction.
2. **Extensions**: Addition of connectives and rules
3. **Dualizations**: Mirror constructions for co-intuitionistic variants
4. **Compositions**: Combinations via categorical products and sums
